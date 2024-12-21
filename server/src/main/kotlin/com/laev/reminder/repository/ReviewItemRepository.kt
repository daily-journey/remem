package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewItem
import com.laev.reminder.repository.dto.ReviewItemsToday
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewItemRepository: JpaRepository<ReviewItem, Long> {
    @Query("SELECT r FROM ReviewItem r WHERE r.member.id = :memberId")
    fun findAllByMemberId(memberId: Long): List<ReviewItem>

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
}