package com.example.employee.leave.managment.repository;

import com.example.employee.leave.managment.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        try { u.setDepartment(rs.getString("department")); } catch (Exception ignored) {}
        try { u.setBio(rs.getString("bio")); }              catch (Exception ignored) {}
        try { u.setEmpId(rs.getString("emp_id")); }         catch (Exception ignored) {}
        try { u.setPhone(rs.getString("phone")); }          catch (Exception ignored) {}
        try { u.setAddress(rs.getString("address")); }      catch (Exception ignored) {}
        try { u.setManagerId((Integer) rs.getObject("manager_id")); } catch (Exception ignored) {}
        try { u.setFailedAttempts(rs.getInt("failed_attempts")); }    catch (Exception ignored) {}
        try { u.setLockUntil(rs.getObject("lock_until", LocalDateTime.class)); } catch (Exception ignored) {}
        return u;
    };

    public void save(User user) {
        String sql = "INSERT INTO users(emp_id, name, email, phone, address, password, role, department, bio, active) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                user.getEmpId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getAddress(), user.getPassword(),
                user.getRole(), user.getDepartment(), user.getBio(), true);
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, userRowMapper, email)
                .stream().findFirst().orElse(null);
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id)
                .stream().findFirst().orElse(null);
    }

    public List<User> getAllEmployees() {
        String sql = "SELECT * FROM users WHERE role = 'ROLE_EMPLOYEE' ORDER BY name";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public List<User> getAllManagers() {
        String sql = "SELECT * FROM users WHERE role = 'ROLE_MANAGER' ORDER BY name";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public List<User> getUnassignedEmployees() {
        String sql = "SELECT * FROM users WHERE role = 'ROLE_EMPLOYEE' AND active = true AND manager_id IS NULL ORDER BY name";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public List<User> getAssignedEmployees() {
        String sql = "SELECT * FROM users WHERE role = 'ROLE_EMPLOYEE' AND active = true AND manager_id IS NOT NULL ORDER BY name";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public List<User> getTeamByManager(int managerId) {
        String sql = "SELECT * FROM users WHERE manager_id = ? AND active = true ORDER BY name";
        return jdbcTemplate.query(sql, userRowMapper, managerId);
    }

    public int countTeamSize(int managerId) {
        String sql = "SELECT COUNT(*) FROM users WHERE manager_id = ? AND active = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, managerId);
        return count != null ? count : 0;
    }

    public void assignManager(int employeeId, int managerId) {
        String sql = "UPDATE users SET manager_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, managerId, employeeId);
    }

    public void unassignEmployee(int employeeId) {
        String sql = "UPDATE users SET manager_id = NULL WHERE id = ?";
        jdbcTemplate.update(sql, employeeId);
    }

    public void deactivateUser(int id) {
        String sql = "UPDATE users SET active = false WHERE id = ?";
        jdbcTemplate.update(sql, id);
        String unassign = "UPDATE users SET manager_id = NULL WHERE manager_id = ?";
        jdbcTemplate.update(unassign, id);
    }

    public void restoreUser(int id) {
        String sql = "UPDATE users SET active = true WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void updateProfile(int id, String phone, String address, String bio) {
        String sql = "UPDATE users SET phone = ?, address = ?, bio = ? WHERE id = ?";
        jdbcTemplate.update(sql, phone, address, bio, id);
    }

    public void updateEmployeeInfo(int id, String name, String department, String phone, String address) {
        String sql = "UPDATE users SET name = ?, department = ?, phone = ?, address = ? WHERE id = ?";
        jdbcTemplate.update(sql, name, department, phone, address, id);
    }

    public void updatePassword(int id, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, hashedPassword, id);
    }

    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ? AND active = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, role);
        return count != null ? count : 0;
    }

    public List<User> getAllEmployeesAndManagers() {
        String sql = "SELECT * FROM users WHERE role IN ('ROLE_EMPLOYEE','ROLE_MANAGER') ORDER BY role, name";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ── NEW: login lockout support ──

    public void setFailedAttempts(String email, int attempts) {
        String sql = "UPDATE users SET failed_attempts = ? WHERE email = ?";
        jdbcTemplate.update(sql, attempts, email);
    }

    // Clears both the attempt counter AND any active lock — called on every
    // successful login, and right after a lock is freshly applied (see
    // UserService.recordFailedLogin: the counter resets to 0 once locked,
    // so the next 5 attempts after the lock expires start fresh).
    public void resetFailedAttempts(String email) {
        String sql = "UPDATE users SET failed_attempts = 0, lock_until = NULL WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }

    public void lockUser(String email, LocalDateTime until) {
        String sql = "UPDATE users SET lock_until = ? WHERE email = ?";
        jdbcTemplate.update(sql, until, email);
    }
}
