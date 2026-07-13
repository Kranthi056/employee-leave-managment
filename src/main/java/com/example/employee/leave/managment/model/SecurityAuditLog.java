package com.example.employee.leave.managment.model;

import java.time.LocalDateTime;

/**
 * One row = one security-relevant administrative action, primarily HR
 * deleting/deactivating/restoring an employee or manager account.
 *
 * Written by SecurityAuditService, never updated or deleted by the
 * application itself — this table is the non-repudiation record, so it's
 * intentionally append-only from the app's point of view.
 */
public class SecurityAuditLog {

    private long id;
    private LocalDateTime eventTime;

    private Integer adminId;
    private String  adminUsername;

    private String action;              // e.g. DEACTIVATE, PERMANENT_DELETE, RESTORE

    private Integer targetEmployeeId;
    private String  targetEmployeeName;

    private String ipAddress;
    private String userAgent;

    private String status;              // SUCCESS | FAILURE

    public SecurityAuditLog() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Integer getTargetEmployeeId() { return targetEmployeeId; }
    public void setTargetEmployeeId(Integer targetEmployeeId) { this.targetEmployeeId = targetEmployeeId; }

    public String getTargetEmployeeName() { return targetEmployeeName; }
    public void setTargetEmployeeName(String targetEmployeeName) { this.targetEmployeeName = targetEmployeeName; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
