package com.example.employee.leave.managment.repository;

import com.example.employee.leave.managment.model.SecurityAuditLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * All statements here use JdbcTemplate's `?` placeholders — values are
 * always bound as parameters, never string-concatenated into SQL, so this
 * (like every other repository in the app) is not vulnerable to SQL
 * injection regardless of what an admin or attacker types into a form.
 */
@Repository
public class SecurityAuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public SecurityAuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SecurityAuditLog> mapper = (rs, rowNum) -> {
        SecurityAuditLog a = new SecurityAuditLog();
        a.setId(rs.getLong("id"));
        a.setEventTime(rs.getObject("event_time", LocalDateTime.class));
        a.setAdminId((Integer) rs.getObject("admin_id"));
        a.setAdminUsername(rs.getString("admin_username"));
        a.setAction(rs.getString("action"));
        a.setTargetEmployeeId((Integer) rs.getObject("target_employee_id"));
        a.setTargetEmployeeName(rs.getString("target_employee_name"));
        a.setIpAddress(rs.getString("ip_address"));
        a.setUserAgent(rs.getString("user_agent"));
        a.setStatus(rs.getString("status"));
        return a;
    };

    public void log(Integer adminId, String adminUsername, String action,
                     Integer targetEmployeeId, String targetEmployeeName,
                     String ipAddress, String userAgent, String status) {
        String sql = "INSERT INTO security_audit_log " +
                "(admin_id, admin_username, action, target_employee_id, target_employee_name, ip_address, user_agent, status) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, adminId, adminUsername, action,
                targetEmployeeId, targetEmployeeName, ipAddress, userAgent, status);
    }

    public List<SecurityAuditLog> getAll() {
        String sql = "SELECT * FROM security_audit_log ORDER BY event_time DESC";
        return jdbcTemplate.query(sql, mapper);
    }

    public List<SecurityAuditLog> getRecent(int limit) {
        String sql = "SELECT * FROM security_audit_log ORDER BY event_time DESC LIMIT ?";
        return jdbcTemplate.query(sql, mapper, limit);
    }

    public List<SecurityAuditLog> getByAdmin(int adminId) {
        String sql = "SELECT * FROM security_audit_log WHERE admin_id = ? ORDER BY event_time DESC";
        return jdbcTemplate.query(sql, mapper, adminId);
    }
}
