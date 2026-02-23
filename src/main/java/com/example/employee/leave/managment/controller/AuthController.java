package com.example.employee.leave.managment.controller;

import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
public class AuthController {

    private final UserService           userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
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
        String empId = "HR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        user.setEmpId(empId);
        user.setRole("ROLE_HR");
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/login?registered";
    }
}