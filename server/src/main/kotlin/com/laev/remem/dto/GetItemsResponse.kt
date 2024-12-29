package com.laev.remem.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

class GetItemsResponse(
    val id: Long,

    @Schema(example = "Banana")
    val mainText: String,

    @Schema(example = "바나나", nullable = true)
    val subText: String?,

    @Schema(description = "Creation datetime in ISO format", example = "2024-10-05T22:09:23Z")
    val createdDatetime: OffsetDateTime,

    @Schema(description = "Flag indicating if the item is exposed repeatedly", example = "true")
    @get:JsonProperty("isRecurring")
    val isRecurring: Boolean,

    @Schema(description = "List of upcoming review datetimes in ISO format", example = "[\"2024-10-05T22:09:23Z\", \"2024-10-12T22:09:23Z\"]")
    val reviewDates: List<OffsetDateTime>,
)
