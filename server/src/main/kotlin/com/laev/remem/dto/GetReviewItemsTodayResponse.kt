package com.laev.remem.dto

import com.laev.remem.enum.ReviewItemStatus

class GetReviewItemsTodayResponse(
    val id: Long,
    val mainText: String,
    val status: ReviewItemStatus,
)
