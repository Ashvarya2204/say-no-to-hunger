package com.saynotohunger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig 
{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests
        (
            auth -> auth
            .requestMatchers
            (
                "/", 
                "/login",
                "/auth/login",
                "/auth/send-link",
                "/auth/status",
                "/css/**",
                "/js/**",
                "/ws/**",
                "/images/**",
                "/donor/create",
                "/volunteer/create",
                "/uploads/**",
                "/error",
                "/dashBoard"
            ).permitAll()
            .anyRequest().authenticated()
        )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout-success")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            .sessionManagement
            (   session ->
                session.sessionCreationPolicy
                (
                    org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED
                )
            )

            .securityContext
            (
                context ->
                context.requireExplicitSave(false)
            );
        
        return http.build();
    }
}
