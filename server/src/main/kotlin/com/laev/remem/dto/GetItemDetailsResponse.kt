package com.laev.remem.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

class GetItemDetailsResponse(
    val id: Long,

    @Schema(example = "Banana")
    val mainText: String,

    @Schema(example = "바나나", nullable = true)
    val subText: String?,

    @Schema(description = "Flag indicating if the item is exposed repeatedly", example = "true")
    @get:JsonProperty("isRecurring")
    val isRecurring: Boolean,

    @Schema(description = "Dates to review, including today")
    val upcomingReviewDates: List<OffsetDateTime>,

    @Schema(description = "Dates requested by the user to be reminded later")
    val notMemorizedDates: List<OffsetDateTime>,

    @Schema(description = "Dates that the user memorized")
    val memorizedDates: List<OffsetDateTime>,

    @Schema(description = "Dates that were skipped by the user")
    val skippedDates: List<OffsetDateTime>,
)
