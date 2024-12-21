package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewItem
import com.laev.reminder.repository.dto.ReviewItemsToday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewItemRepository: JpaRepository<ReviewItem, Long> {

    fun findAllByMemberId(memberId: Long): List<ReviewItem>

    fun findByIdAndMemberId(itemId: Long, memberId: Long): ReviewItem?

    @Query("""
        SELECT new com.laev.reminder.repository.dto.ReviewItemsToday(
            ri.id, ri.mainText, m.isMemorized
        )
        FROM ReviewItem ri
            INNER JOIN ReviewDatetime rd ON rd.reviewItem.id = ri.id AND rd.start <= :datetime AND :datetime < rd.end
            LEFT JOIN MemorizationLog m ON m.reviewItem.id = ri.id
        WHERE ri.member.id = :memberId
    """)
    fun findReviewItemAndMemorizationLogByReviewDatesAndMemberId(datetime: OffsetDateTime, memberId: Long): List<ReviewItemsToday>

    @Query("""
        SELECT rd.start
        FROM ReviewItem ri
            INNER JOIN ReviewDatetime rd ON rd.reviewItem.id = ri.id AND :nowDatetime <= rd.end
        WHERE ri.id = :itemId
    """)
    fun findUpcomingReviewDatetimeByMemberIdAndReviewItemId(itemId: Long, nowDatetime: OffsetDateTime): List<OffsetDateTime>

    @Query("""
        SELECT m.createdDatetime
        FROM ReviewItem ri
            INNER JOIN MemorizationLog m ON m.reviewItem.id = ri.id AND m.isMemorized = false
        WHERE ri.id = :itemId
    """)
    fun findRemindLaterReviewDatetimeByMemberIdAndReviewItemId(itemId: Long): List<OffsetDateTime>

    @Query("""
        SELECT m.createdDatetime
        FROM ReviewItem ri
            INNER JOIN MemorizationLog m ON m.reviewItem.id = ri.id AND m.isMemorized = true
        WHERE ri.id = :itemId
    """)
    fun findMemorizedReviewDatetimeByMemberIdAndReviewItemId(itemId: Long): List<OffsetDateTime>

    @Query("""
        SELECT rd.start
        FROM ReviewDatetime rd
            LEFT JOIN MemorizationLog m
                ON m.reviewItem.id = rd.reviewItem.id
                AND rd.start <= m.createdDatetime
                AND m.createdDatetime < rd.end
        WHERE rd.reviewItem.id = :itemId AND m.id IS NULL AND rd.end <= :nowDatetime
    """)
    fun findSkippedReviewDatetimeByMemberIdAndReviewItemId(itemId: Long, nowDatetime: OffsetDateTime): List<OffsetDateTime>
}