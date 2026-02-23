package com.example.employee.leave.managment.repository;

import com.example.employee.leave.managment.model.LeaveRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class LeaveRepository {

    private final JdbcTemplate jdbcTemplate;

    public LeaveRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final org.springframework.jdbc.core.RowMapper<LeaveRequest> leaveMapper = (rs, rowNum) -> {
        LeaveRequest l = new LeaveRequest();
        l.setId(rs.getInt("id"));
        l.setUserId(rs.getInt("user_id"));
        l.setLeaveType(rs.getString("leave_type"));
        l.setStartDate(rs.getObject("start_date", LocalDate.class));
        l.setEndDate(rs.getObject("end_date", LocalDate.class));
        l.setReason(rs.getString("reason"));
        l.setStatus(rs.getString("status"));
        try { l.setRejectionReason(rs.getString("rejection_reason")); } catch (Exception ignored) {}
        try { l.setName(rs.getString("name")); }                       catch (Exception ignored) {}
        try { l.setDepartment(rs.getString("department")); }           catch (Exception ignored) {}
        return l;
    };

    public void applyLeave(LeaveRequest leave) {
        String sql = "INSERT INTO leave_request(user_id, leave_type, start_date, end_date, reason, status) VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                leave.getUserId(), leave.getLeaveType(),
                leave.getStartDate(), leave.getEndDate(),
                leave.getReason(), "PENDING");
    }

    public List<LeaveRequest> getAllLeaves() {
        String sql = "SELECT lr.*, u.name, u.department FROM leave_request lr JOIN users u ON lr.user_id = u.id ORDER BY lr.id DESC";
        return jdbcTemplate.query(sql, leaveMapper);
    }

    public List<LeaveRequest> getByUser(int userId) {
        String sql = "SELECT lr.*, u.name, u.department FROM leave_request lr JOIN users u ON lr.user_id = u.id WHERE lr.user_id = ? ORDER BY lr.id DESC";
        return jdbcTemplate.query(sql, leaveMapper, userId);
    }

    public List<LeaveRequest> getByManagerTeam(int managerId) {
        String sql = "SELECT lr.*, u.name, u.department FROM leave_request lr " +
                     "JOIN users u ON lr.user_id = u.id " +
                     "WHERE u.manager_id = ? ORDER BY lr.id DESC";
        return jdbcTemplate.query(sql, leaveMapper, managerId);
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE leave_request SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void rejectWithReason(int id, String reason) {
        String sql = "UPDATE leave_request SET status = 'REJECTED', rejection_reason = ? WHERE id = ?";
        jdbcTemplate.update(sql, reason, id);
    }

    public void cancelLeave(int id, int userId) {
        String sql = "DELETE FROM leave_request WHERE id = ? AND user_id = ? AND status = 'PENDING'";
        jdbcTemplate.update(sql, id, userId);
    }

    public int countByUserAndStatus(int userId, String status) {
        String sql = "SELECT COUNT(*) FROM leave_request WHERE user_id = ? AND status = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, status);
        return count != null ? count : 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM leave_request";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int countAllByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM leave_request WHERE status = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, status);
        return count != null ? count : 0;
    }

    public List<LeaveRequest> getRecentLeaves(int limit) {
        String sql = "SELECT lr.*, u.name, u.department FROM leave_request lr JOIN users u ON lr.user_id = u.id ORDER BY lr.id DESC LIMIT ?";
        return jdbcTemplate.query(sql, leaveMapper, limit);
    }

    // Check if user already has a leave overlapping the given dates (PENDING or APPROVED)
    public boolean hasOverlappingLeave(int userId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM leave_request WHERE user_id = ? AND status IN ('PENDING','APPROVED') " +
                     "AND start_date <= ? AND end_date >= ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, endDate, startDate);
        return count != null && count > 0;
    }
    public void deleteLeave(int id) {
        String sql = "DELETE FROM leave_request WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}