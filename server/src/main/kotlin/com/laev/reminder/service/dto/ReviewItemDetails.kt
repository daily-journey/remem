package com.laev.reminder.service.dto

import java.time.OffsetDateTime

data class ReviewItemDetails(
    val upcomingReviewDates: List<OffsetDateTime>,
    val remindTomorrowDates: List<OffsetDateTime>,
    val memorizedDates: List<OffsetDateTime>,
    val skippedDates: List<OffsetDateTime>,
)
