package com.example.employee.leave.managment.service;

import com.example.employee.leave.managment.model.LeaveRequest;
import com.example.employee.leave.managment.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveService {

    public static final int TOTAL_LEAVE_ALLOWANCE = 24;

    private final LeaveRepository leaveRepository;

    public LeaveService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public String validateLeave(LeaveRequest leave, int userId) {
        LocalDate today = LocalDate.now();

        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            return "Start date and end date are required.";
        }
        if (leave.getStartDate().isBefore(today)) {
            return "Start date cannot be in the past.";
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            return "End date must be on or after start date.";
        }
        if (countLeftLeaves(userId) <= 0) {
            return "You have no leave balance remaining.";
        }
        if (leaveRepository.hasOverlappingLeave(userId, leave.getStartDate(), leave.getEndDate())) {
            return "You already have a leave request for overlapping dates.";
        }
        return null;
    }

    public void applyLeave(LeaveRequest leave) {
        leaveRepository.applyLeave(leave);
    }

    public List<LeaveRequest> getAll() {
        return leaveRepository.getAllLeaves();
    }

    public List<LeaveRequest> getByUser(int userId) {
        return leaveRepository.getByUser(userId);
    }

    public List<LeaveRequest> getByManagerTeam(int managerId) {
        return leaveRepository.getByManagerTeam(managerId);
    }

    // ── NEW: all leave requests submitted by managers — used by HR's
    // manager-leave approval screen, since managers' own leave isn't
    // approved by another manager, it's approved by HR.
    public List<LeaveRequest> getManagerLeaves() {
        return leaveRepository.getByRole("ROLE_MANAGER");
    }

    public void updateStatus(int id, String status) {
        leaveRepository.updateStatus(id, status);
    }

    public void rejectWithReason(int id, String reason) {
        leaveRepository.rejectWithReason(id, reason);
    }

    public void cancelLeave(int id, int userId) {
        leaveRepository.cancelLeave(id, userId);
    }

    public int countByStatus(int userId, String status) {
        return leaveRepository.countByUserAndStatus(userId, status);
    }

    public int countLeftLeaves(int userId) {
        int approved = leaveRepository.countByUserAndStatus(userId, "APPROVED");
        return Math.max(TOTAL_LEAVE_ALLOWANCE - approved, 0);
    }

    public int countAll() {
        return leaveRepository.countAll();
    }

    public int countAllByStatus(String status) {
        return leaveRepository.countAllByStatus(status);
    }

    public List<LeaveRequest> getRecentLeaves(int limit) {
        return leaveRepository.getRecentLeaves(limit);
    }

    public void deleteLeave(int id) {
        leaveRepository.deleteLeave(id);
    }
}
