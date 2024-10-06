package com.laev.reminder.dto

import io.swagger.v3.oas.annotations.media.Schema

class GetItemsResponse(
    val id: String,

    @Schema(example = "Banana")
    val mainText: String,

    @Schema(example = "바나나", nullable = true)
    val subText: String?,

    @Schema(description = "Creation datetime in ISO format", example = "2024-10-05T22:09:23.648Z")
    val createDatetime: String,

    @Schema(description = "Number of successful attempts", example = "0")
    val successCount: Int,

    @Schema(description = "Number of failed attempts", example = "0")
    val failCount: Int,

    @Schema(description = "Flag indicating if the item is repeated", example = "true")
    val isRepeated: Boolean,

    @Schema(description = "List of upcoming reminder datetimes in ISO format", example = "[\"2024-10-05T22:09:23.648Z\", \"2024-10-12T22:09:23.648Z\"]")
    val nextRemindDatetimes: List<String>
)