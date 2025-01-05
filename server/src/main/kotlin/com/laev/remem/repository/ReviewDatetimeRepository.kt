package com.laev.remem.repository

import com.laev.remem.entity.ReviewDatetime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewDatetimeRepository: JpaRepository<ReviewDatetime, Long> {
    fun findByReviewItemId(itemId: Long): List<ReviewDatetime>

    @Query("select r from ReviewDatetime r where r.start <= :datetime and :datetime < r.end")
    fun findByDatetimeRange(datetime: OffsetDateTime): List<ReviewDatetime>

    @Query("select r from ReviewDatetime r where r.start = :startDatetime and r.reviewItem.id = :itemId")
    fun findByStartAndItemId(startDatetime: OffsetDateTime, itemId: Long): List<ReviewDatetime>

    @Modifying
    @Query("DELETE FROM ReviewDatetime r WHERE r.reviewItem.id = :itemId AND :now < r.start")
    fun deleteUpcomingReviewDates(itemId: Long, now: OffsetDateTime)

    @Modifying
    @Query("UPDATE ReviewDatetime r SET r.isDeleted = true WHERE r.reviewItem.id = :itemId")
    fun updateAsDeleted(itemId: Long)

    @Modifying
    @Query("""
        UPDATE ReviewDatetime r
        SET r.isMemorized = :isMemorized, r.isSkipped = false
        WHERE r.start <= :now AND :now < r.end AND r.reviewItem.id = :itemId
    """)
    fun updateMemorizationAndMarkNotSkipped(isMemorized: Boolean, now: OffsetDateTime, itemId: Long)

    @Query("""
        SELECT r
        FROM ReviewDatetime r
        WHERE r.isDeleted = false
            AND r.isSkipped is null
            AND r.end <= :now
    """)
    fun findPastSkippedReviewDatetimes(now: OffsetDateTime): List<ReviewDatetime>

    @Modifying
    @Query("UPDATE ReviewDatetime r SET r.isSkipped = true WHERE r.id = :reviewDatetimeId")
    fun updateSkippedStatus(reviewDatetimeId: Long)

    @Modifying
    @Query(
        value = """
            UPDATE review_datetime r
            SET r.start = DATE_ADD(r.start, INTERVAL 1 DAY), r.end = DATE_ADD(r.start, INTERVAL 1 DAY)
            WHERE r.item_id = :itemId AND :now < r.start
        """,
        nativeQuery = true,
    )
    fun delayUpcomingReviewDatesByOneDay(itemId: Long, now: OffsetDateTime)
}
