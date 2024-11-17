package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewDatetime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewDatetimeRepository: JpaRepository<ReviewDatetime, Long> {
    @Query("SELECT r FROM ReviewDatetime r WHERE r.start <= :datetime AND :datetime < r.end")
    fun findByDatetimeRange(datetime: OffsetDateTime): List<ReviewDatetime>
}