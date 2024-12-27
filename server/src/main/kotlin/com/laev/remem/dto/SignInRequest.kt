package com.laev.remem.dto

import com.laev.remem.annotation.ValidEmail
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

class SignInRequest(
    @field:ValidEmail
    @Schema(nullable = false, example = "test@example.com")
    val email: String,

    @field:Length(min = 36, message = "password should be at least 36 characters long")
    @field:NotNull
    @Schema(nullable = false)
    val password: String,
)
