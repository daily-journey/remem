package com.laev.remem.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.ZoneOffset

class AddItemRequest(
    @field:NotBlank(message = "Main text must not be blank")
    @Schema(example = "Banana")
    val mainText: String,

    @Schema(example = "바나나", nullable = true)
    val subText: String?,

    @Schema(example = "-05:00", nullable = false)
    val offset: ZoneOffset,
)
