package com.laev.remem.service.dto

import java.time.OffsetDateTime

data class ReviewItemDetails(
    val id: Long,
    val mainText: String,
    val subText: String?,
    val isRecurring: Boolean,
    val upcomingReviewDates: List<OffsetDateTime>,
    val remindTomorrowDates: List<OffsetDateTime>,
    val memorizedDates: List<OffsetDateTime>,
    val skippedDates: List<OffsetDateTime>,
)
