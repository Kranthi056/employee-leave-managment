package com.example.employee.leave.managment.model;

import java.util.List;

public class User {

    private int id;
    private String empId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String role;
    private String department;
    private String bio;
    private boolean active = true;
    private Integer managerId;

    private int teamSize;
    private List<User> teamMembers;

    private int totalLeaves;
    private int approvedLeaves;
    private int rejectedLeaves;
    private int leftLeaves;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public List<User> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<User> teamMembers) { this.teamMembers = teamMembers; }

    public int getTotalLeaves() { return totalLeaves; }
    public void setTotalLeaves(int totalLeaves) { this.totalLeaves = totalLeaves; }

    public int getApprovedLeaves() { return approvedLeaves; }
    public void setApprovedLeaves(int approvedLeaves) { this.approvedLeaves = approvedLeaves; }

    public int getRejectedLeaves() { return rejectedLeaves; }
    public void setRejectedLeaves(int rejectedLeaves) { this.rejectedLeaves = rejectedLeaves; }

    public int getLeftLeaves() { return leftLeaves; }
    public void setLeftLeaves(int leftLeaves) { this.leftLeaves = leftLeaves; }
}