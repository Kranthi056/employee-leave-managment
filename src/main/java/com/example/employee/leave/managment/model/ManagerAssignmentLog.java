package com.example.employee.leave.managment.model;

import java.time.LocalDateTime;

public class ManagerAssignmentLog {

    private int id;
    private int employeeId;
    private String employeeName;
    private Integer managerId;
    private String managerName;
    private String action;       // ASSIGNED | UNASSIGNED | UNASSIGNED_MANAGER_DELETED
    private String changedBy;
    private LocalDateTime changedAt;

    public ManagerAssignmentLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
