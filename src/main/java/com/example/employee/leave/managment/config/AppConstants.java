package com.example.employee.leave.managment.config;

public final class AppConstants {

    private AppConstants() {}

    // Maximum team size a single manager can have. Kept in one place so
    // HR's assign-manager screen and the manager's own "My Team" screen
    // always agree on the same number — previously this was hardcoded
    // as 5 in the templates and 10 only in HRController, which is what
    // caused the mismatch you noticed.
    public static final int MAX_TEAM_SIZE = 10;

    // Login lockout policy (applies to EMPLOYEE and MANAGER accounts only —
    // HR accounts are intentionally exempt, see UserService.recordFailedLogin
    // for the reasoning).
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int LOCKOUT_MINUTES    = 5;
}
