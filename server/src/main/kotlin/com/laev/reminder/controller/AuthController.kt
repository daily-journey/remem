package com.laev.reminder.controller

import com.laev.reminder.dto.SignUpRequest
import com.laev.reminder.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}