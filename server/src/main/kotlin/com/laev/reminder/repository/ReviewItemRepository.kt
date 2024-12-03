package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewItemRepository: JpaRepository<ReviewItem, Long>