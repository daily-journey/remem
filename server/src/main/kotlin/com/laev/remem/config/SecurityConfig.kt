package com.laev.remem.config

import com.laev.remem.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173", "https://daily-journey.github.io")
        configuration.allowedOriginPatterns = listOf("http://10.88.111.*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true // Allow credentials (cookies)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Apply CORS configuration to all paths
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) } // Use centralized CORS config
            .csrf { it.disable() } // Disable CSRF protection
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(
                        "/auth/sign-up", "/auth/sign-in",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                        "/error",
                    ).permitAll() // Allow access to sign-up and sign-in and Swagger endpoints without authentication
                    .anyRequest().authenticated() // Require authentication for all other requests
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter before UsernamePasswordAuthenticationFilter
        return http.build()
    }
}
