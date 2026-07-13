package com.example.employee.leave.managment.config;

import com.example.employee.leave.managment.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomerUserDetailsService userDetailsService;
    private final UserService                userService;
    private final LoginAttemptFailureHandler loginAttemptFailureHandler;
    private final MeterRegistry meterRegistry;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(CustomerUserDetailsService userDetailsService,
                          UserService userService,
                          MeterRegistry meterRegistry,
                          LoginAttemptFailureHandler loginAttemptFailureHandler,
                          AccessDeniedHandler accessDeniedHandler,
                          AuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService         = userDetailsService;
        this.userService                = userService;
        this.loginAttemptFailureHandler = loginAttemptFailureHandler;
        this.meterRegistry              = meterRegistry;
        this.accessDeniedHandler        = accessDeniedHandler;
        this.authenticationEntryPoint   = authenticationEntryPoint;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // NEW: required for .maximumSessions(1) to actually find out when a
    // session dies (logout or timeout) — without this, the concurrent-session
    // registry can go stale and refuse a legitimate second login after the
    // first session's ended.
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .cacheControl(cache -> {})
                .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    new AntPathRequestMatcher("/login"),
            			    new AntPathRequestMatcher("/login/**"),
            			    new AntPathRequestMatcher("/hrsignup"),
            			    new AntPathRequestMatcher("/hrsignup/**"),
            			    new AntPathRequestMatcher("/Hrsignup"),
            			    new AntPathRequestMatcher("/Hrsignup/**"),
            			    new AntPathRequestMatcher("/error"),
            			    new AntPathRequestMatcher("/css/**"),
            			    new AntPathRequestMatcher("/js/**"),
            			    new AntPathRequestMatcher("/images/**"),
            			    new AntPathRequestMatcher("/webjars/**"),
            			    new AntPathRequestMatcher("/dev/**"),

            			    // ADD THIS
            			    new AntPathRequestMatcher("/actuator/**")
            			).permitAll()
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/hr/**").hasRole("HR")
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)

            // ── NEW: session security ──
            // - IF_REQUIRED: don't create sessions for stateless calls (e.g. /api/**)
            // - migrateSession: rotate the session ID on login (session-fixation protection)
            // - maximumSessions(1): a second login from elsewhere invalidates the first
            //   session, so a forgotten/unattended login can't be silently reused
            //   alongside a fresh one. The actual inactivity timeout that logs an
            //   unattended session out automatically is server.servlet.session.timeout
            //   in application.properties (10 minutes).
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(1)
                .expiredUrl("/login?sessionExpired")
            )

            // ── NEW: every 401/403 increments unauthorized_access_attempts_total
            // (see SecurityMetricsHandlers) before falling back to the normal
            // redirect-to-login behaviour.
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            )

            // ── NEW: marks the session as an explicit logout BEFORE Spring
            // Security's LogoutFilter invalidates it, so SessionAuditListener
            // can tell a real timeout apart from someone clicking "Logout".
            .addFilterBefore(new LogoutMarkerFilter(), LogoutFilter.class)

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // NEW: a successful login always clears any failed-attempt count
                    // and any lock on this account.
                    userService.resetFailedLogin(authentication.getName());

                    String dbRole       = authentication.getAuthorities().iterator().next().getAuthority();
                    String selectedRole = request.getParameter("selectedRole");

                    if (selectedRole == null) selectedRole = "";
                    selectedRole = selectedRole.trim().toLowerCase();

                    boolean mismatch = false;
                    if      (dbRole.equals("ROLE_EMPLOYEE") && !selectedRole.equals("employee")) mismatch = true;
                    else if (dbRole.equals("ROLE_MANAGER")  && !selectedRole.equals("manager"))  mismatch = true;
                    else if (dbRole.equals("ROLE_HR")       && !selectedRole.equals("hr"))       mismatch = true;

                    if (mismatch) {
                        request.getSession().invalidate();
                        response.sendRedirect("/login?wrongrole");
                        return;
                    }

                    if      (dbRole.equals("ROLE_EMPLOYEE")) response.sendRedirect("/employee/dashboard");
                    else if (dbRole.equals("ROLE_MANAGER"))  response.sendRedirect("/manager/dashboard");
                    else if (dbRole.equals("ROLE_HR"))       response.sendRedirect("/hr/dashboard");
                    else                                     response.sendRedirect("/login?error");
                })
                // NEW: replaced .failureUrl(...) with a custom handler that tracks
                // attempts and applies lockout.
                .failureHandler(loginAttemptFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            .requestCache(cache -> cache.disable());

        return http.build();
    }
}
