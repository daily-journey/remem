package com.laev.reminder.service

import com.laev.reminder.component.ReviewDatetimeComponent
import com.laev.reminder.exception.ItemNotFoundException
import com.laev.reminder.repository.ReviewDatetimeRepository
import com.laev.reminder.repository.ReviewItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class ReviewDatetimeService(
    private val reviewDatetimeComponent: ReviewDatetimeComponent,
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
    private val reviewItemRepository: ReviewItemRepository,
) {
    fun getReviewDatetimes(itemId: Long): List<OffsetDateTime> {
        return reviewDatetimeRepository.findByReviewItemId(itemId).map { it.start }
    }

    @Transactional
    fun createNewReviewCycle(itemId: Long, offset: ZoneOffset) {
        val reviewItem = reviewItemRepository.findById(itemId)
            .orElseThrow { ItemNotFoundException(itemId) }
        reviewDatetimeRepository.deleteUpcomingReviewDates(itemId, OffsetDateTime.now())
        reviewDatetimeComponent.createReviewDatetimeCycle(offset, reviewItem)
    }
}
