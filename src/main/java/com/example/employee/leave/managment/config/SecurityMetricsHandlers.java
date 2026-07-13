package com.example.employee.leave.managment.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Every 401/403 in the app flows through one of these two beans, and both
 * increment `unauthorized_access_attempts_total` before doing the normal
 * Spring Security thing (redirect to /login). The `type` tag is what lets
 * Grafana tell "someone hit a protected URL while logged out" apart from
 * "a logged-in EMPLOYEE tried to open an /hr/** URL" (privilege escalation).
 */
@Configuration
public class SecurityMetricsHandlers {

    @Bean
    public AccessDeniedHandler accessDeniedHandler(MeterRegistry meterRegistry) {
        return (request, response, deniedException) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null) ? auth.getName() : "anonymous";

            meterRegistry.counter("unauthorized_access_attempts",
                    "type", "privilege_escalation",
                    "path", request.getRequestURI()).increment();

            request.setAttribute("deniedUser", username); // available to logging/filters if needed
            response.sendRedirect(request.getContextPath() + "/login?forbidden");
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(MeterRegistry meterRegistry) {
        LoginUrlAuthenticationEntryPoint delegate = new LoginUrlAuthenticationEntryPoint("/login");
        return (request, response, authException) -> {
            meterRegistry.counter("unauthorized_access_attempts",
                    "type", "unauthenticated",
                    "path", request.getRequestURI()).increment();
            delegate.commence(request, response, authException);
        };
    }
}
