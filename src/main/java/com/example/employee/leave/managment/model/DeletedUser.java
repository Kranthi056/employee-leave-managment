package com.example.employee.leave.managment.model;

import java.time.LocalDateTime;

public class DeletedUser {

    private int id;
    private int originalUserId;
    private String empId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String department;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public DeletedUser() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOriginalUserId() { return originalUserId; }
    public void setOriginalUserId(int originalUserId) { this.originalUserId = originalUserId; }

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

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
