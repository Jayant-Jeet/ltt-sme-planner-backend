package com.edulearnorg.ltt.smeplanner.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/ltt-sme-planner/v1/auth/login").permitAll() // Login endpoint
                .requestMatchers("/ltt-sme-planner/v1/activities").hasAnyRole("SME", "SUPERVISOR", "LEAD") // Activities endpoint - all roles
                .requestMatchers("/ltt-sme-planner/v1/admin/**").hasRole("LEAD") // Admin endpoints - Lead only
                .requestMatchers("/ltt-sme-planner/v1/supervisor/**").hasAnyRole("SUPERVISOR", "LEAD") // Supervisor endpoints
                .requestMatchers("/ltt-sme-planner/v1/api-docs/**").permitAll() // Swagger API docs
                .requestMatchers("/ltt-sme-planner/v1/swagger-ui/**").permitAll() // Swagger UI
                .requestMatchers("/ltt-sme-planner/v1/swagger-ui.html").permitAll() // Swagger UI main page
                .requestMatchers("/swagger-ui/**").permitAll() // Default Swagger UI path
                .requestMatchers("/v3/api-docs/**").permitAll() // OpenAPI docs
                .requestMatchers("/swagger-resources/**").permitAll() // Swagger resources
                .requestMatchers("/webjars/**").permitAll() // Swagger UI assets
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
