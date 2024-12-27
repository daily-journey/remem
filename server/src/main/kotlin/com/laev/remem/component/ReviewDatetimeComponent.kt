package com.laev.remem.component

import com.laev.remem.entity.ReviewDatetime
import com.laev.remem.entity.ReviewItem
import com.laev.remem.repository.ReviewDatetimeRepository
import com.laev.remem.utils.CycleCalculator
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class ReviewDatetimeComponent(
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
) {
    fun createReviewDatetimeCycle(offset: ZoneOffset, reviewItem: ReviewItem) {
        val cycles = listOf(1, 3, 7, 21)
        val zonedCreatedDatetime = OffsetDateTime.now(ZoneOffset.UTC).withOffsetSameInstant(offset) // Convert to local UTC time with the applied zoneOffset

        for (cycle in cycles) {
            val startDatetime = CycleCalculator.getUTCStartDatetime(zonedCreatedDatetime, cycle, offset)
            val endDatetime = startDatetime.plusHours(24)

            reviewDatetimeRepository.save(
                ReviewDatetime(
                    start = startDatetime,
                    end = endDatetime,
                    reviewItem = reviewItem,
                )
            )
        }
    }
}
