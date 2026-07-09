package com.example.employee.leave.managment.service;

import com.example.employee.leave.managment.config.AppConstants;
import com.example.employee.leave.managment.model.DeletedUser;
import com.example.employee.leave.managment.model.ManagerAssignmentLog;
import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.repository.DeletedUserRepository;
import com.example.employee.leave.managment.repository.LeaveRepository;
import com.example.employee.leave.managment.repository.ManagerAssignmentLogRepository;
import com.example.employee.leave.managment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import io.micrometer.core.instrument.MeterRegistry;
@Service
public class UserService {

    private final UserRepository                  userRepository;
    private final DeletedUserRepository            deletedUserRepository;
    private final ManagerAssignmentLogRepository   managerAssignmentLogRepository;
    private final MeterRegistry meterRegistry;
    private final LeaveRepository                  leaveRepository;

    public UserService(UserRepository userRepository,
                       DeletedUserRepository deletedUserRepository,
                       ManagerAssignmentLogRepository managerAssignmentLogRepository,
                       MeterRegistry meterRegistry,
                       LeaveRepository leaveRepository) {
        this.userRepository                = userRepository;
        this.deletedUserRepository         = deletedUserRepository;
        this.managerAssignmentLogRepository = managerAssignmentLogRepository;
        this.leaveRepository               = leaveRepository;
        this.meterRegistry                 = meterRegistry;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getAllEmployees() {
        return userRepository.getAllEmployees();
    }

    public List<User> getAllManagers() {
        return userRepository.getAllManagers();
    }

    public List<User> getUnassignedEmployees() {
        return userRepository.getUnassignedEmployees();
    }

    public List<User> getAssignedEmployees() {
        return userRepository.getAssignedEmployees();
    }

    public List<User> getTeamByManager(int managerId) {
        return userRepository.getTeamByManager(managerId);
    }

    public void assignManager(int employeeId, int managerId, String changedBy) {
        userRepository.assignManager(employeeId, managerId);

        User emp = userRepository.findById(employeeId);
        User mgr = userRepository.findById(managerId);
        managerAssignmentLogRepository.log(
                employeeId, emp != null ? emp.getName() : null,
                managerId,  mgr != null ? mgr.getName() : null,
                "ASSIGNED", changedBy);
    }

    public void unassignEmployee(int employeeId, String changedBy) {
        User emp = userRepository.findById(employeeId);
        Integer oldManagerId = emp != null ? emp.getManagerId() : null;
        User oldManager = oldManagerId != null ? userRepository.findById(oldManagerId) : null;

        userRepository.unassignEmployee(employeeId);

        managerAssignmentLogRepository.log(
                employeeId, emp != null ? emp.getName() : null,
                oldManagerId, oldManager != null ? oldManager.getName() : null,
                "UNASSIGNED", changedBy);
    }

    public void deactivateUser(int id) {
        userRepository.deactivateUser(id);
    }

    public void restoreUser(int id) {
        userRepository.restoreUser(id);
    }

    public void updateProfile(int id, String phone, String address, String bio) {
        userRepository.updateProfile(id, phone, address, bio);
    }

    public void updateEmployeeInfo(int id, String name, String department, String phone, String address) {
        userRepository.updateEmployeeInfo(id, name, department, phone, address);
    }

    public void updatePassword(int id, String hashedPassword) {
        userRepository.updatePassword(id, hashedPassword);
    }

    public int countByRole(String role) {
        return userRepository.countByRole(role);
    }

    public List<User> getAllEmployeesAndManagers() {
        return userRepository.getAllEmployeesAndManagers();
    }

    public void registerEmployee(User employee) {
        userRepository.save(employee);
    }

    public boolean deleteUserPermanently(int id, String deletedBy) {
        User user = userRepository.findById(id);
        if (user == null) {
            return false;
        }

        deletedUserRepository.archive(user, deletedBy);

        if ("ROLE_MANAGER".equals(user.getRole())) {
            List<User> team = userRepository.getTeamByManager(id);
            for (User member : team) {
                userRepository.unassignEmployee(member.getId());
                managerAssignmentLogRepository.log(
                        member.getId(), member.getName(),
                        id, user.getName(),
                        "UNASSIGNED_MANAGER_DELETED", deletedBy);
            }
        }

        leaveRepository.deleteByUserId(id);
        userRepository.deleteById(id);
        return true;
    }

    public List<DeletedUser> getDeletedUsers() {
        return deletedUserRepository.getAll();
    }

    public List<ManagerAssignmentLog> getManagerAssignmentLogs() {
        return managerAssignmentLogRepository.getAll();
    }

    // ─────────────────────────────────────────────────────────────────
    // NEW: login-attempt tracking / lockout.
    //
    // HR accounts are intentionally exempt from lockout. Rationale: this
    // app typically has very few HR accounts (often just one). Locking
    // the only HR account out for 5 minutes after mistyped attempts would
    // have no one else able to unlock it — unlike an employee/manager,
    // who can always be helped by HR. If you want HR included too, delete
    // the `if ("ROLE_HR"...)` guard below.
    // ─────────────────────────────────────────────────────────────────
    public void recordFailedLogin(String email) {
    	meterRegistry.counter("logins", "outcome", "failure").increment();
        User user = userRepository.findByEmail(email);
        if (user == null) return;
        if ("ROLE_HR".equals(user.getRole())) return;

        int attempts = user.getFailedAttempts() + 1;
        if (attempts >= AppConstants.MAX_LOGIN_ATTEMPTS) {
            userRepository.lockUser(email, LocalDateTime.now().plusMinutes(AppConstants.LOCKOUT_MINUTES));
            // Reset the raw counter now that the lock itself is in effect,
            // so the next 5-attempt cycle starts clean once the lock expires.
            userRepository.setFailedAttempts(email, 0);
        } else {
            userRepository.setFailedAttempts(email, attempts);
        }
    }

    public int getRemainingAttempts(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return AppConstants.MAX_LOGIN_ATTEMPTS;
        return Math.max(AppConstants.MAX_LOGIN_ATTEMPTS - user.getFailedAttempts(), 0);
    }

    public void resetFailedLogin(String email) {
    	meterRegistry.counter("logins", "outcome", "success").increment();
        userRepository.resetFailedAttempts(email);
    }
}
