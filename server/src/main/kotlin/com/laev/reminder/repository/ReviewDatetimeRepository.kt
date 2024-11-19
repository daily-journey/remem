package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewDatetime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ReviewDatetimeRepository: JpaRepository<ReviewDatetime, Long> {
    @Query("select r from ReviewDatetime r where r.start <= :datetime and :datetime < r.end")
    fun findByDatetimeRange(datetime: OffsetDateTime): List<ReviewDatetime>

    @Query("select r from ReviewDatetime r where r.start = :startDatetime and r.item.id = :itemId")
    fun findByStartAndItemId(startDatetime: OffsetDateTime, itemId: Long): List<ReviewDatetime>
}