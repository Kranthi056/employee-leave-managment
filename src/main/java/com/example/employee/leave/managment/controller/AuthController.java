package com.example.employee.leave.managment.controller;

import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.service.UserService;
import com.example.employee.leave.managment.util.InputSanitizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
public class AuthController {

    private final UserService           userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InputSanitizer        inputSanitizer;

    public AuthController(UserService userService, BCryptPasswordEncoder passwordEncoder,
                          InputSanitizer inputSanitizer) {
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
        this.inputSanitizer  = inputSanitizer;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/hrsignup", "/Hrsignup"})
    public String hrSignupPage() {
        return "Hrsignup";
    }

    @PostMapping({"/hrsignup", "/Hrsignup"})
    public String registerHR(@ModelAttribute User user,
                             @RequestParam("confirmPassword") String confirmPassword,
                             Model model) {
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "Hrsignup";
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email is already registered.");
            return "Hrsignup";
        }
        // NEW: same allow-nothing HTML sanitization as HR-created accounts —
        // this form is public (no login required), so it's the highest-risk
        // entry point for a stored-XSS attempt in the whole app.
        inputSanitizer.sanitizeUser(user, "hr-signup");

        String empId = "HR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        user.setEmpId(empId);
        user.setRole("ROLE_HR");
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/login?registered";
    }
}