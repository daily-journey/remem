package com.laev.reminder.service

import com.laev.reminder.repository.ReviewDatetimeRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class ReviewDatetimeService(
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
) {
    fun getReviewDatetimes(itemId: Long): List<OffsetDateTime> {
        return reviewDatetimeRepository.findByReviewItemId(itemId).map { it.start }
    }
}
