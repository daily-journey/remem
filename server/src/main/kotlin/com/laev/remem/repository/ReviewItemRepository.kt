package com.laev.remem.repository

import com.laev.remem.entity.ReviewItem
import com.laev.remem.repository.dto.ReviewItemsToday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewItemRepository: JpaRepository<ReviewItem, Long> {

    fun findByIdAndMemberId(itemId: Long, memberId: Long): ReviewItem?

    fun findByMemberIdAndIsDeletedFalse(memberId: Long): List<ReviewItem>

    @Query(
        """
        SELECT new com.laev.remem.repository.dto.ReviewItemsToday(
            ri.id, ri.mainText, rd.isMemorized
        )
        FROM ReviewItem ri
            INNER JOIN ReviewDatetime rd ON rd.reviewItem.id = ri.id AND rd.start <= :now AND :now < rd.end
        WHERE ri.member.id = :memberId AND ri.isDeleted = false
    """
    )
    fun findReviewItemOfToday(now: OffsetDateTime, memberId: Long): List<ReviewItemsToday>

    @Query("""
        SELECT rd.start
        FROM ReviewItem ri
            INNER JOIN ReviewDatetime rd ON rd.reviewItem.id = ri.id AND :nowDatetime <= rd.end
        WHERE ri.id = :itemId
    """)
    fun findUpcomingReviewDatetime(itemId: Long, nowDatetime: OffsetDateTime): List<OffsetDateTime>

    @Query("""
        SELECT r.start
        FROM ReviewDatetime r
        WHERE r.reviewItem.id = :itemId AND r.isMemorized = false
    """)
    fun findNotMemorizedDates(itemId: Long): List<OffsetDateTime>

    @Query("""
        SELECT r.start
        FROM ReviewDatetime r
        WHERE r.reviewItem.id = :itemId AND r.isMemorized = true
    """)
    fun findMemorizedDates(itemId: Long): List<OffsetDateTime>

    @Query("""
        SELECT r.start
        FROM ReviewDatetime r
        WHERE r.reviewItem.id = :itemId AND r.isSkipped = true
    """)
    fun findSkippedDates(itemId: Long): List<OffsetDateTime>
}
