package com.example.employee.leave.managment.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * IMPORTANT — read before using this class.
 *
 * This controller is intentionally NOT reachable in a normal run of the app.
 * @Profile("dev") means Spring will not even create this bean — and therefore
 * these URLs will not exist at all — unless the app is started with the
 * "dev" profile active, e.g.:
 *
 *     mvn spring-boot:run -Dspring-boot.run.profiles=dev
 *   or
 *     java -jar app.jar --spring.profiles.active=dev
 *
 * There is no link to this page anywhere in the HR navbar/UI on purpose.
 * It exists only so you can quickly wipe your local/demo database while
 * developing, without needing to drop the schema manually. It must never
 * be enabled with the "dev" profile in a production deployment.
 */
@Controller
@RequestMapping("/dev")
@Profile("dev")
public class DevAdminController {

    private static final String CONFIRM_PHRASE = "DELETE EVERYTHING";

    private final JdbcTemplate jdbcTemplate;

    public DevAdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/reset-database")
    public String resetPage() {
        return "dev/reset-database";
    }

    @PostMapping("/reset-database")
    public String resetDatabase(@RequestParam("confirmText") String confirmText, Model model) {
        if (!CONFIRM_PHRASE.equals(confirmText)) {
            model.addAttribute("error", "Confirmation text did not match exactly. Nothing was deleted.");
            return "dev/reset-database";
        }

        // Child tables first, to respect foreign key relationships.
        jdbcTemplate.update("DELETE FROM leave_request");
        jdbcTemplate.update("DELETE FROM manager_assignment_log");
        jdbcTemplate.update("DELETE FROM deleted_users");
        jdbcTemplate.update("DELETE FROM users");

        model.addAttribute("success",
            "All application data has been wiped. No HR account exists anymore — " +
            "go to /hrsignup to register a new HR account before logging in again.");
        return "dev/reset-database";
    }
}
