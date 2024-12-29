package com.laev.remem.repository

import com.laev.remem.entity.MemorizationLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemorizationLogRepository: JpaRepository<MemorizationLog, Long>
