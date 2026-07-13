package com.example.employee.leave.managment.controller;

import com.example.employee.leave.managment.model.LeaveRequest;
import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.service.LeaveService;
import com.example.employee.leave.managment.service.PublicHolidayService;
import com.example.employee.leave.managment.service.UserService;
import com.example.employee.leave.managment.util.InputSanitizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final LeaveService          leaveService;
    private final UserService           userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PublicHolidayService  holidayService;
    private final InputSanitizer        inputSanitizer;

    public EmployeeController(LeaveService leaveService, UserService userService,
                              BCryptPasswordEncoder passwordEncoder,
                              PublicHolidayService holidayService,
                              InputSanitizer inputSanitizer) {
        this.leaveService    = leaveService;
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
        this.holidayService  = holidayService;
        this.inputSanitizer  = inputSanitizer;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("user",           user);
        model.addAttribute("allLeaves",      leaveService.getByUser(user.getId()));
        model.addAttribute("totalLeaves",    LeaveService.TOTAL_LEAVE_ALLOWANCE);
        model.addAttribute("approvedLeaves", leaveService.countByStatus(user.getId(), "APPROVED"));
        model.addAttribute("rejectedLeaves", leaveService.countByStatus(user.getId(), "REJECTED"));
        model.addAttribute("pendingLeaves",  leaveService.countByStatus(user.getId(), "PENDING"));
        model.addAttribute("leftLeaves",     leaveService.countLeftLeaves(user.getId()));
        return "employee/dashboard";
    }

    @GetMapping("/apply")
    public String applyPage(Model model, Authentication auth) {
        model.addAttribute("leave",    new LeaveRequest());
        model.addAttribute("user",     userService.findByEmail(auth.getName()));
        model.addAttribute("holidays", holidayService.getUpcomingIndianHolidays());
        return "employee/request-leave";
    }

    @PostMapping("/apply")
    public String applyLeave(@ModelAttribute LeaveRequest leave, Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());

        String error = leaveService.validateLeave(leave, user.getId());
        if (error != null) {
            model.addAttribute("leave",    leave);
            model.addAttribute("user",     user);
            model.addAttribute("error",    error);
            model.addAttribute("holidays", holidayService.getUpcomingIndianHolidays());
            return "employee/request-leave";
        }

        leave.setUserId(user.getId());
        leave.setStatus("PENDING");
        leaveService.applyLeave(leave);
        return "redirect:/employee/status?applied";
    }

    @GetMapping("/status")
    public String leaveStatus(Model model, Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        model.addAttribute("user",   user);
        model.addAttribute("leaves", leaveService.getByUser(user.getId()));
        return "employee/leave-status";
    }

    @PostMapping("/cancel")
    public String cancelLeave(@RequestParam int id, Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        leaveService.cancelLeave(id, user.getId());
        return "redirect:/employee/status?cancelled";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        model.addAttribute("user", userService.findByEmail(auth.getName()));
        return "employee/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String bio,
                                Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        userService.updateProfile(user.getId(),
                inputSanitizer.clean(phone,   "phone",   "employee-update-profile"),
                inputSanitizer.clean(address, "address", "employee-update-profile"),
                inputSanitizer.clean(bio,     "bio",     "employee-update-profile"));
        return "redirect:/employee/profile?updated";
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
            return "employee/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "New passwords do not match.");
            return "employee/profile";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "Password must be at least 6 characters.");
            return "employee/profile";
        }

        userService.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        return "redirect:/employee/profile?pwchanged";
    }
}