package com.laev.reminder.repository

import com.laev.reminder.entity.MemorizationLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemorizationLogRepository: JpaRepository<MemorizationLog, Long>