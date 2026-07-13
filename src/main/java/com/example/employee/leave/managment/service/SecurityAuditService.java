package com.example.employee.leave.managment.service;

import com.example.employee.leave.managment.model.SecurityAuditLog;
import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.repository.SecurityAuditLogRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityAuditService {

    public static final String ACTION_DEACTIVATE       = "DEACTIVATE";
    public static final String ACTION_PERMANENT_DELETE = "PERMANENT_DELETE";
    public static final String ACTION_RESTORE          = "RESTORE";
    public static final String ACTION_ASSIGN_MANAGER   = "ASSIGN_MANAGER";
    public static final String ACTION_UNASSIGN         = "UNASSIGN_EMPLOYEE";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    private final SecurityAuditLogRepository auditLogRepository;
    private final UserService                userService;
    private final MeterRegistry              meterRegistry;

    public SecurityAuditService(SecurityAuditLogRepository auditLogRepository,
                                UserService userService,
                                MeterRegistry meterRegistry) {
        this.auditLogRepository = auditLogRepository;
        this.userService        = userService;
        this.meterRegistry      = meterRegistry;
    }

    public void logUserAction(Authentication admin, User target, int targetId,
                              String action, String status, HttpServletRequest request) {

        User adminUser = admin != null ? userService.findByEmail(admin.getName()) : null;

        Integer adminId       = adminUser != null ? adminUser.getId() : null;
        String  adminUsername = admin != null ? admin.getName() : "unknown";
        String  targetName    = target != null ? target.getName() : null;
        String  targetRole    = target != null ? target.getRole() : "unknown";
        String  ip            = extractClientIp(request);
        String  userAgent     = request != null ? request.getHeader("User-Agent") : null;

        auditLogRepository.log(adminId, adminUsername, action, targetId, targetName, ip, userAgent, status);

        // Enhanced metrics
        meterRegistry.counter("audit_log_events_total",
                "action", action,
                "status", status,
                "target_role", targetRole).increment();

        // Specific counters for easy Grafana panels
        meterRegistry.counter("hr_action_" + action.toLowerCase(),
                "status", status,
                "target_role", targetRole).increment();
    }

    public List<SecurityAuditLog> getAuditTrail() {
        return auditLogRepository.getAll();
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}