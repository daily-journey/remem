package com.laev.reminder.controller

import com.laev.reminder.dto.SignInRequest
import com.laev.reminder.dto.SignUpRequest
import com.laev.reminder.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody @Valid request: SignUpRequest,
    ): ResponseEntity<String> {
        authService.signUp(request.email, request.password, request.name)

        return ResponseEntity.status(HttpStatus.CREATED).body("Sign-up successful")
    }

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody @Valid request: SignInRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<String> {
        val token = authService.signIn(request.email, request.password)
        val requestOrigin = httpRequest.getHeader("origin") ?: "http://localhost:5173"
        val domain = URI(requestOrigin).host
        val cookie = ResponseCookie.from("Authorization", token)
            .domain(domain)
            .httpOnly(true)
            .path("/")
            .maxAge(86400)
            .sameSite("None")
            .secure(true)
            .build()

        return ResponseEntity.ok()
            .header("Set-Cookie", cookie.toString())
            .body("Sign-in successful")
    }
}