package com.example.employee.leave.managment.config;

import com.example.employee.leave.managment.model.User;
import com.example.employee.leave.managment.service.UserService;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomerUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        if (!user.isActive()) {
            throw new UsernameNotFoundException("Account is deactivated: " + email);
        }

        // NEW: if this account is currently locked out (5 failed attempts within
        // the last AppConstants.LOCKOUT_MINUTES), mark it as locked. Spring
        // Security's DefaultPreAuthenticationChecks will then automatically
        // throw a LockedException BEFORE checking the password — so a correct
        // password entered during a lockout still won't let anyone in.
        boolean locked = user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .accountLocked(locked)
                .build();
    }
}
