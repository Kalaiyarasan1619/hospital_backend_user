package com.hospital_management.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hospital_management.security.AuthEntryPointJwt;
import com.hospital_management.security.AuthTokenFilter;
import com.hospital_management.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // ===== PUBLIC ENDPOINTS (NO AUTHENTICATION REQUIRED) =====
                .requestMatchers("/api/auth/**").permitAll()                    // Auth endpoints
                .requestMatchers("/api/categories/**").permitAll()              // Categories - PUBLIC
                .requestMatchers("/api/restaurants/**").permitAll()             // Restaurants - PUBLIC  
                .requestMatchers("/api/menu-items/**").permitAll()              // Menu items - PUBLIC
                
                .requestMatchers("/eureka/**").permitAll()
                
                // ===== PROTECTED ENDPOINTS (AUTHENTICATION REQUIRED) =====
                .requestMatchers("/api/admin/**").hasRole("ADMIN")              // Admin only
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")    // User endpoints
                .requestMatchers("/api/orders/**").authenticated()              // Orders need auth
                .requestMatchers("/api/payments/**").authenticated()            // Payments need auth
                .requestMatchers("/api/profile/**").authenticated()
                .requestMatchers("/api/stripe/create-payment-sheet").authenticated()             // Stripe payment sheet need auth

                // All other requests need authentication
                .anyRequest().authenticated()
            );

        // Disable frame options for H2 console
        http.headers(headers -> headers.frameOptions().disable());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
