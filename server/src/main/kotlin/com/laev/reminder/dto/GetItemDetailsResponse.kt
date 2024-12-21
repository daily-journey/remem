package com.laev.reminder.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

class GetItemDetailsResponse(
    @Schema(description = "Dates to review, including today")
    val upcomingReviewDates: List<OffsetDateTime>,

    @Schema(description = "Dates requested by the user to be reminded later")
    val remindTomorrowDates: List<OffsetDateTime>,

    @Schema(description = "Dates that the user memorized")
    val memorizedDates: List<OffsetDateTime>,

    @Schema(description = "Dates that were skipped by the user")
    val skippedDates: List<OffsetDateTime>,
)
