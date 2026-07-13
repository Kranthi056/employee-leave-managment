package com.example.employee.leave.managment.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs BEFORE Spring Security's LogoutFilter (see SecurityConfig) and marks
 * the session as an explicit logout. SessionAuditListener reads this flag
 * in sessionDestroyed() to tell the difference between:
 *   - a user clicking "Logout"                    -> outcome=logout
 *   - the session simply expiring from inactivity  -> outcome=timeout
 *
 * It has to run before the session is invalidated, which is why it's a
 * plain filter rather than a LogoutHandler (by the time custom
 * LogoutHandlers run, Spring Security's own SecurityContextLogoutHandler
 * may already have invalidated the session).
 */
public class LogoutMarkerFilter extends OncePerRequestFilter {

    public static final String EXPLICIT_LOGOUT_ATTR = "EXPLICIT_LOGOUT";

    private final RequestMatcher logoutMatcher = new AntPathRequestMatcher("/logout", "POST");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (logoutMatcher.matches(request)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute(EXPLICIT_LOGOUT_ATTR, Boolean.TRUE);
            }
        }
        chain.doFilter(request, response);
    }
}
