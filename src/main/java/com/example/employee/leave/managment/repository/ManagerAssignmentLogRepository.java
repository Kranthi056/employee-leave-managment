package com.example.employee.leave.managment.repository;

import com.example.employee.leave.managment.model.ManagerAssignmentLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ManagerAssignmentLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public ManagerAssignmentLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ManagerAssignmentLog> mapper = (rs, rowNum) -> {
        ManagerAssignmentLog l = new ManagerAssignmentLog();
        l.setId(rs.getInt("id"));
        l.setEmployeeId(rs.getInt("employee_id"));
        l.setEmployeeName(rs.getString("employee_name"));
        l.setManagerId((Integer) rs.getObject("manager_id"));
        l.setManagerName(rs.getString("manager_name"));
        l.setAction(rs.getString("action"));
        l.setChangedBy(rs.getString("changed_by"));
        l.setChangedAt(rs.getObject("changed_at", LocalDateTime.class));
        return l;
    };

    public void log(int employeeId, String employeeName, Integer managerId, String managerName,
                     String action, String changedBy) {
        String sql = "INSERT INTO manager_assignment_log(employee_id, employee_name, manager_id, manager_name, action, changed_by) " +
                     "VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(sql, employeeId, employeeName, managerId, managerName, action, changedBy);
    }

    public List<ManagerAssignmentLog> getAll() {
        String sql = "SELECT * FROM manager_assignment_log ORDER BY changed_at DESC";
        return jdbcTemplate.query(sql, mapper);
    }
}
