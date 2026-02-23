package com.example.employee.leave.managment.service;

import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void assignManager(int employeeId, int managerId) {
        userRepository.assignManager(employeeId, managerId);
    }

    public void unassignEmployee(int employeeId) {
        userRepository.unassignEmployee(employeeId);
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
}