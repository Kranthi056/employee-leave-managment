package com.example.employee.leave.managment.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

/**
 * Spring Boot auto-registers any bean implementing a servlet listener
 * interface (HttpSessionListener here) with the embedded container — no
 * extra @Bean/@WebListener wiring needed.
 *
 * Every session destruction increments `session_events_total`, tagged
 * outcome=logout (LogoutMarkerFilter set the flag) or outcome=timeout
 * (session simply expired after server.servlet.session.timeout of
 * inactivity — see application.properties). This is what feeds the
 * "session timeout events" panel on the security dashboard.
 */
@Component
public class SessionAuditListener implements HttpSessionListener {

    private final MeterRegistry meterRegistry;

    public SessionAuditListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Boolean explicitLogout = null;
        try {
            explicitLogout = (Boolean) se.getSession().getAttribute(LogoutMarkerFilter.EXPLICIT_LOGOUT_ATTR);
        } catch (IllegalStateException alreadyInvalidated) {
            // Some containers invalidate the session before this listener
            // fires; treat unreadable state as "unknown" rather than crash.
        }

        String outcome = Boolean.TRUE.equals(explicitLogout) ? "logout" : "timeout";
        meterRegistry.counter("session_events", "outcome", outcome).increment();
    }
}
