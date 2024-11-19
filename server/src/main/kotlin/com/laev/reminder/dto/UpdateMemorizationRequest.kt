package com.laev.reminder.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.ZoneOffset

class UpdateMemorizationRequest(
    @field:NotNull(message = "'isMemorized' field is required")
    @Schema(nullable = false)
    val isMemorized: Boolean?, // If declared as non-nullable, it defaults to false during deserialization instead of triggering validation for null values.

    @field:NotNull(message = "'offset' field is required")
    @Schema(example = "-05:00", nullable = false)
    val offset: ZoneOffset,
)