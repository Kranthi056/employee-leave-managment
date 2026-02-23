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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/hr")
public class HRController {

    private final LeaveService          leaveService;
    private final UserService           userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PublicHolidayService  holidayService;

    public HRController(LeaveService leaveService, UserService userService,
                        BCryptPasswordEncoder passwordEncoder,
                        PublicHolidayService holidayService) {
        this.leaveService    = leaveService;
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
        this.holidayService  = holidayService;
    }
    @PostMapping("/delete-leave")
    public String deleteLeave(@RequestParam int id,
                              @RequestParam(defaultValue = "/hr/dashboard") String redirectTo) {
        leaveService.deleteLeave(id);
        return "redirect:" + redirectTo + "?deleted";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("user",           userService.findByEmail(auth.getName()));
        model.addAttribute("totalEmployees", userService.countByRole("ROLE_EMPLOYEE"));
        model.addAttribute("totalManagers",  userService.countByRole("ROLE_MANAGER"));
        model.addAttribute("approvedLeaves", leaveService.countAllByStatus("APPROVED"));
        model.addAttribute("pendingLeaves",  leaveService.countAllByStatus("PENDING"));
        model.addAttribute("recentLeaves",   leaveService.getRecentLeaves(10));
        model.addAttribute("holidays",       holidayService.getUpcomingIndianHolidays());
        return "Hr/dashboard";
    }

    @GetMapping("/employees")
    public String viewEmployees(Model model, Authentication auth) {
        model.addAttribute("user",      userService.findByEmail(auth.getName()));
        model.addAttribute("employees", userService.getAllEmployees());
        return "Hr/employees";
    }

    @GetMapping("/managers")
    public String viewManagers(Model model, Authentication auth) {
        List<User> managers = userService.getAllManagers();
        for (User mgr : managers) {
            List<User> team = userService.getTeamByManager(mgr.getId());
            mgr.setTeamSize(team.size());
            mgr.setTeamMembers(team);
        }
        model.addAttribute("user",     userService.findByEmail(auth.getName()));
        model.addAttribute("managers", managers);
        return "Hr/managers";
    }

    @GetMapping("/add-employee")
    public String addEmployeePage(Model model, Authentication auth) {
        model.addAttribute("user",        userService.findByEmail(auth.getName()));
        model.addAttribute("newEmployee", new User());
        return "Hr/add-employee";
    }

    @PostMapping("/add-employee")
    public String addEmployee(@ModelAttribute("newEmployee") User emp,
                              @RequestParam("confirmPassword") String confirmPassword,
                              Model model, Authentication auth) {
        if (!emp.getPassword().equals(confirmPassword)) {
            model.addAttribute("user",        userService.findByEmail(auth.getName()));
            model.addAttribute("newEmployee", emp);
            model.addAttribute("error",       "Passwords do not match!");
            return "Hr/add-employee";
        }
        if (userService.findByEmail(emp.getEmail()) != null) {
            model.addAttribute("user",        userService.findByEmail(auth.getName()));
            model.addAttribute("newEmployee", emp);
            model.addAttribute("error",       "Email already registered.");
            return "Hr/add-employee";
        }
        String empId = "EMP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        emp.setEmpId(empId);
        emp.setRole("ROLE_EMPLOYEE");
        emp.setActive(true);
        emp.setPassword(passwordEncoder.encode(emp.getPassword()));
        userService.saveUser(emp);
        return "redirect:/hr/add-employee?success";
    }

    @GetMapping("/add-manager")
    public String addManagerPage(Model model, Authentication auth) {
        model.addAttribute("user",       userService.findByEmail(auth.getName()));
        model.addAttribute("newManager", new User());
        return "Hr/add-manager";
    }

    @PostMapping("/add-manager")
    public String addManager(@ModelAttribute("newManager") User mgr,
                             @RequestParam("confirmPassword") String confirmPassword,
                             Model model, Authentication auth) {
        if (!mgr.getPassword().equals(confirmPassword)) {
            model.addAttribute("user",       userService.findByEmail(auth.getName()));
            model.addAttribute("newManager", mgr);
            model.addAttribute("error",      "Passwords do not match!");
            return "Hr/add-manager";
        }
        if (userService.findByEmail(mgr.getEmail()) != null) {
            model.addAttribute("user",       userService.findByEmail(auth.getName()));
            model.addAttribute("newManager", mgr);
            model.addAttribute("error",      "Email already registered.");
            return "Hr/add-manager";
        }
        String empId = "MGR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        mgr.setEmpId(empId);
        mgr.setRole("ROLE_MANAGER");
        mgr.setActive(true);
        mgr.setPassword(passwordEncoder.encode(mgr.getPassword()));
        userService.saveUser(mgr);
        return "redirect:/hr/add-manager?success";
    }

    // Edit employee name, dept, phone, address
    @PostMapping("/edit-employee")
    public String editEmployee(@RequestParam int id,
                               @RequestParam String name,
                               @RequestParam String department,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String address) {
        userService.updateEmployeeInfo(id, name, department, phone, address);
        return "redirect:/hr/employees?updated";
    }

    // Edit manager name, dept, phone, address
    @PostMapping("/edit-manager")
    public String editManager(@RequestParam int id,
                              @RequestParam String name,
                              @RequestParam String department,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String address) {
        userService.updateEmployeeInfo(id, name, department, phone, address);
        return "redirect:/hr/managers?updated";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam int id,
                             @RequestParam(defaultValue = "/hr/employees") String redirectTo) {
        userService.deactivateUser(id);
        return "redirect:" + redirectTo + "?deleted";
    }

    @PostMapping("/restore-user")
    public String restoreUser(@RequestParam int id,
                              @RequestParam(defaultValue = "/hr/employees") String redirectTo) {
        userService.restoreUser(id);
        return "redirect:" + redirectTo + "?restored";
    }

    @GetMapping("/assign-manager")
    public String assignManagerPage(Model model, Authentication auth) {
        List<User> allManagers = userService.getAllManagers();
        List<User> activeManagers = new ArrayList<>();
        for (User mgr : allManagers) {
            if (mgr.isActive()) {
                mgr.setTeamSize(userService.getTeamByManager(mgr.getId()).size());
                activeManagers.add(mgr);
            }
        }
        model.addAttribute("user",                userService.findByEmail(auth.getName()));
        model.addAttribute("managers",            activeManagers);   // for assignment buttons
        model.addAttribute("allManagers",         allManagers);       // for name lookup in assigned table
        model.addAttribute("unassignedEmployees", userService.getUnassignedEmployees());
        model.addAttribute("assignedEmployees",   userService.getAssignedEmployees());
        return "Hr/assign-manager";
    }

    @PostMapping("/assign-manager")
    public String assignManager(@RequestParam int employeeId,
                                @RequestParam int managerId) {
        int teamSize = userService.getTeamByManager(managerId).size();
        if (teamSize >= 5) {
            return "redirect:/hr/assign-manager?error=Team+is+full";
        }
        userService.assignManager(employeeId, managerId);
        return "redirect:/hr/assign-manager?assigned";
    }

    @PostMapping("/unassign-employee")
    public String unassignEmployee(@RequestParam int employeeId) {
        userService.unassignEmployee(employeeId);
        return "redirect:/hr/assign-manager?unassigned";
    }

    @GetMapping("/reports")
    public String reports(Model model, Authentication auth) {
        List<User> allUsers = userService.getAllEmployeesAndManagers();
        int allowance = LeaveService.TOTAL_LEAVE_ALLOWANCE;
        for (User u : allUsers) {
            int approved = leaveService.countByStatus(u.getId(), "APPROVED");
            int rejected = leaveService.countByStatus(u.getId(), "REJECTED");
            u.setTotalLeaves(allowance);
            u.setApprovedLeaves(approved);
            u.setRejectedLeaves(rejected);
            u.setLeftLeaves(Math.max(allowance - approved, 0));
        }
        model.addAttribute("user",    userService.findByEmail(auth.getName()));
        model.addAttribute("reports", allUsers);
        return "Hr/reports";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        model.addAttribute("user", userService.findByEmail(auth.getName()));
        return "Hr/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String bio,
                                Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        userService.updateProfile(user.getId(), phone, address, bio);
        return "redirect:/hr/profile?updated";
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
            return "Hr/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "New passwords do not match.");
            return "Hr/profile";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("user",    user);
            model.addAttribute("pwError", "Password must be at least 6 characters.");
            return "Hr/profile";
        }

        userService.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        return "redirect:/hr/profile?pwchanged";
    }
}