package com.example.employee.leave.managment.repository;

import com.example.employee.leave.managment.model.DeletedUser;
import com.example.employee.leave.managment.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DeletedUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeletedUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<DeletedUser> mapper = (rs, rowNum) -> {
        DeletedUser d = new DeletedUser();
        d.setId(rs.getInt("id"));
        d.setOriginalUserId(rs.getInt("original_user_id"));
        d.setEmpId(rs.getString("emp_id"));
        d.setName(rs.getString("name"));
        d.setEmail(rs.getString("email"));
        d.setPhone(rs.getString("phone"));
        d.setAddress(rs.getString("address"));
        d.setRole(rs.getString("role"));
        d.setDepartment(rs.getString("department"));
        d.setDeletedBy(rs.getString("deleted_by"));
        d.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return d;
    };

    // Copies a user's core details into the archive table BEFORE it is removed from `users`.
    public void archive(User user, String deletedBy) {
        String sql = "INSERT INTO deleted_users(original_user_id, emp_id, name, email, phone, address, role, department, deleted_by) " +
                     "VALUES(?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                user.getId(), user.getEmpId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getAddress(), user.getRole(), user.getDepartment(), deletedBy);
    }

    public List<DeletedUser> getAll() {
        String sql = "SELECT * FROM deleted_users ORDER BY deleted_at DESC";
        return jdbcTemplate.query(sql, mapper);
    }

    public List<DeletedUser> getByRole(String role) {
        String sql = "SELECT * FROM deleted_users WHERE role = ? ORDER BY deleted_at DESC";
        return jdbcTemplate.query(sql, mapper, role);
    }
}
