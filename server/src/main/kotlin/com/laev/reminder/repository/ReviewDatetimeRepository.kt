package com.laev.reminder.repository

import com.laev.reminder.entity.ReviewDatetime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewDatetimeRepository: JpaRepository<ReviewDatetime, Long>