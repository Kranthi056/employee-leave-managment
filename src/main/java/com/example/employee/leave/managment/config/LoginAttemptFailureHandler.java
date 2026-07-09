package com.example.employee.leave.managment.config;

import com.example.employee.leave.managment.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles every failed login attempt.
 *
 * Two distinct cases:
 *  1. The account was ALREADY locked before this attempt (CustomerUserDetailsService
 *     reported accountLocked=true) -> Spring throws LockedException before even
 *     checking the password. We just redirect with a lockout message.
 *  2. The password was simply wrong -> we increment that user's failed-attempt
 *     counter. If this attempt was the 5th, the account becomes locked from now on
 *     (see UserService.recordFailedLogin), so we redirect with the lockout message
 *     immediately rather than waiting for the next attempt to discover it.
 */
@Component
public class LoginAttemptFailureHandler implements AuthenticationFailureHandler {
	

    private final UserService userService;
    private final MeterRegistry meterRegistry;

    public LoginAttemptFailureHandler(UserService userService,MeterRegistry meterRegistry) {
        this.userService = userService;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException {

        if (exception instanceof LockedException) {
        	meterRegistry.counter("logins", "outcome", "locked").increment();
            response.sendRedirect("/login?locked");
            return;
        }

        String email = request.getParameter("username");

        // Only track attempts for emails that actually correspond to a real
        // account — avoids leaking "how many attempts remain" for an email
        // that was never registered at all.
        if (email != null && !email.isBlank() && userService.findByEmail(email) != null) {
            userService.recordFailedLogin(email);
            int remaining = userService.getRemainingAttempts(email);

            if (remaining <= 0) {
                response.sendRedirect("/login?locked");
            } else {
                response.sendRedirect("/login?error&attempts=" + remaining);
            }
            return;
        }

        response.sendRedirect("/login?error");
    }
}
