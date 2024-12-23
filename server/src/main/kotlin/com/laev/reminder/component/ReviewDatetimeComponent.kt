package com.laev.reminder.component

import com.laev.reminder.entity.ReviewDatetime
import com.laev.reminder.entity.ReviewItem
import com.laev.reminder.repository.ReviewDatetimeRepository
import com.laev.reminder.utils.CycleCalculator
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class ReviewDatetimeComponent(
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
) {
    fun createReviewDatetimeCycle(offset: ZoneOffset, cycles: List<Int>, reviewItem: ReviewItem) {
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
