package com.laev.reminder.controller

import com.laev.reminder.dto.SignInRequest
import com.laev.reminder.dto.SignUpRequest
import com.laev.reminder.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

        return ResponseEntity.ok()
            .header("Authorization", "Bearer $token")
            .header("Access-Control-Expose-Headers", "Authorization")
            .body("Sign-in successful")
    }
}