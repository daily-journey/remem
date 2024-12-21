package com.laev.reminder.dto

import com.laev.reminder.enum.ReviewItemStatus

class GetReviewItemsTodayResponse(
    val id: Long,
    val mainText: String,
    val status: ReviewItemStatus,
)