package com.example.employee.leave.managment.util;

import com.example.employee.leave.managment.model.User;
import io.micrometer.core.instrument.MeterRegistry;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * Strips ALL markup from free-text fields before they ever reach a
 * repository/SQL statement — defense in depth against stored XSS from the
 * "Add Employee" / "Add Manager" / profile-edit forms, on top of
 * Thymeleaf's own output-escaping.
 *
 * Uses an allow-nothing Safelist (Safelist.none()): plain text survives,
 * every tag/attribute is removed. If the cleaned value differs from what
 * was submitted, we treat that as a validation failure and increment the
 * `xss_validation_failures_total` counter (tagged by field + form) so
 * attempted-injection volume is visible in Grafana/Prometheus.
 */
@Component
public class InputSanitizer {

    private final MeterRegistry meterRegistry;

    public InputSanitizer(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /** Sanitizes a single field. fieldName/formName are metric tags only. */
    public String clean(String input, String fieldName, String formName) {
        if (input == null) return null;
        String trimmed = input.trim();
        String cleaned = Jsoup.clean(trimmed, Safelist.none());

        if (!cleaned.equals(trimmed)) {
            meterRegistry.counter("xss_validation_failures",
                    "field", fieldName, "form", formName).increment();
        }
        return cleaned;
    }

    /**
     * Sanitizes every free-text field on a User before it's persisted.
     * Email and password are intentionally excluded — email is validated
     * as an email address, and password is hashed, never rendered as HTML.
     */
    public void sanitizeUser(User user, String formName) {
        if (user == null) return;
        user.setName(clean(user.getName(), "name", formName));
        user.setDepartment(clean(user.getDepartment(), "department", formName));
        user.setPhone(clean(user.getPhone(), "phone", formName));
        user.setAddress(clean(user.getAddress(), "address", formName));
        user.setBio(clean(user.getBio(), "bio", formName));
    }
}
