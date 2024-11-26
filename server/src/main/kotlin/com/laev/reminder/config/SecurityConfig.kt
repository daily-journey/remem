package com.laev.reminder.config

import com.laev.reminder.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() } // Disable CSRF protection
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/auth/sign-up", "/auth/sign-in").permitAll() // Allow access to sign-up and sign-in endpoints without authentication
                    .anyRequest().authenticated() // Require authentication for all other requests
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter before UsernamePasswordAuthenticationFilter
        return http.build()
    }
}