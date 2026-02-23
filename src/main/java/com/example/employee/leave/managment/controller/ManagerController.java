package com.example.employee.leave.managment.controller;

import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.service.LeaveService;
import com.example.employee.leave.managment.service.PublicHolidayService;
import com.example.employee.leave.managment.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final LeaveService          leaveService;
    private final UserService           userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PublicHolidayService  holidayService;

    public ManagerController(LeaveService leaveService, UserService userService,
                             BCryptPasswordEncoder passwordEncoder,
                             PublicHolidayService holidayService) {
        this.leaveService    = leaveService;
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
        this.holidayService  = holidayService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        // Count leaves only from this manager's team, not the whole system
        long total    = leaveService.getByManagerTeam(user.getId()).size();
        long approved = leaveService.getByManagerTeam(user.getId()).stream()
                            .filter(l -> "APPROVED".equals(l.getStatus())).count();
        long rejected = leaveService.getByManagerTeam(user.getId()).stream()
                            .filter(l -> "REJECTED".equals(l.getStatus())).count();
        long pending  = leaveService.getByManagerTeam(user.getId()).stream()
                            .filter(l -> "PENDING".equals(l.getStatus())).count();
        model.addAttribute("user",           user);
        model.addAttribute("totalLeaves",    total);
        model.addAttribute("approvedLeaves", approved);
        model.addAttribute("rejectedLeaves", rejected);
        model.addAttribute("pendingLeaves",  pending);
        model.addAttribute("holidays",       holidayService.getUpcomingIndianHolidays());
        return "manager/dashboard";
    }

    @GetMapping("/employees")
    public String employeeRequests(Model model, Authentication auth) {
        User manager = userService.findByEmail(auth.getName());
        model.addAttribute("user",   manager);
        model.addAttribute("leaves", leaveService.getByManagerTeam(manager.getId()));
        return "manager/employees";
    }

    @GetMapping("/approve/{id}")
    public String approve(@PathVariable int id) {
        leaveService.updateStatus(id, "APPROVED");
        return "redirect:/manager/employees?approved";
    }

    @PostMapping("/reject")
    public String reject(@RequestParam int id,
                         @RequestParam String rejectionReason) {
        leaveService.rejectWithReason(id, rejectionReason);
        return "redirect:/manager/employees?rejected";
    }

    @GetMapping("/team")
    public String viewTeam(Model model, Authentication auth) {
        User manager = userService.findByEmail(auth.getName());
        model.addAttribute("user",        manager);
        model.addAttribute("teamMembers", userService.getTeamByManager(manager.getId()));
        return "manager/team";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        model.addAttribute("user", userService.findByEmail(auth.getName()));
        return "manager/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String bio,
                                Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        userService.updateProfile(user.getId(), phone, address, bio);
        return "redirect:/manager/profile?updated";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "Current password is incorrect.");
            return "manager/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "New passwords do not match.");
            return "manager/profile";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "Password must be at least 6 characters.");
            return "manager/profile";
        }

        userService.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        return "redirect:/manager/profile?pwchanged";
    }
}