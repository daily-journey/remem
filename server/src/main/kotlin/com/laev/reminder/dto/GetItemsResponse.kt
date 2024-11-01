package com.laev.reminder.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

class GetItemsResponse(
    val id: Int,

    @Schema(example = "Banana")
    val mainText: String,

    @Schema(example = "바나나", nullable = true)
    val subText: String?,

    @Schema(description = "Creation datetime in ISO format", example = "2024-10-05T22:09:23")
    val createDatetime: String,

    @Schema(description = "Number of successful attempts", example = "0")
    val successCount: Short,

    @Schema(description = "Number of failed attempts", example = "0")
    val failCount: Short,

    @Schema(description = "Flag indicating if the item is exposed repeatedly", example = "true")
    @get:JsonProperty("isRecurring")
    val isRecurring: Boolean,

    @Schema(description = "List of upcoming reminder datetimes in ISO format", example = "[\"2024-10-05T22:09:23\", \"2024-10-12T22:09:23\"]")
    val nextRemindDatetimes: List<String>
)