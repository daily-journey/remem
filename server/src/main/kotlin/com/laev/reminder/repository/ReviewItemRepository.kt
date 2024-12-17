package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReviewItemRepository: JpaRepository<ReviewItem, Long> {
    @Query("SELECT r FROM ReviewItem r WHERE r.member.id = :memberId")
    fun findAllByMemberId(memberId: Long): List<ReviewItem>
}