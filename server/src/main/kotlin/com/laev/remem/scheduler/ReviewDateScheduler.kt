package com.laev.remem.scheduler

import com.laev.remem.entity.ReviewDatetime
import com.laev.remem.repository.ReviewDatetimeRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Component
class ReviewDateScheduler(
    private val reviewDatetimeRepository: ReviewDatetimeRepository,
) {
    @Transactional
    @Scheduled(cron = "0 0,30 0 * * *", zone = "America/New_York")
    fun updateSkippedReviewDatetimes() {
        val now = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS)
        // find past skipped review datetimes
        val skippedReviewDatetimes = reviewDatetimeRepository.findPastSkippedReviewDatetimes(now)

        skippedReviewDatetimes.forEach { reviewDatetime ->
            // mark as skipped
            reviewDatetimeRepository.updateSkippedStatus(reviewDatetime.id)
            // create a new review datetime that starts today
            reviewDatetimeRepository.save(
                ReviewDatetime(start = now, reviewItem = reviewDatetime.reviewItem)
            )
            // delay the same item's upcoming review dates by one day
            reviewDatetimeRepository.delayUpcomingReviewDatesByOneDay(reviewDatetime.reviewItem.id, OffsetDateTime.now(ZoneOffset.UTC))
        }
    }
}
