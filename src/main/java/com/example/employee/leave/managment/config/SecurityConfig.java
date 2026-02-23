package com.example.employee.leave.managment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomerUserDetailsService userDetailsService;

    public SecurityConfig(CustomerUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                    new AntPathRequestMatcher("/webjars/**")
                ).permitAll()
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/hr/**").hasRole("HR")
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
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
                .failureUrl("/login?error")
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