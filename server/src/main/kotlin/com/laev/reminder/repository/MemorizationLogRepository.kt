package com.laev.reminder.repository

import com.laev.reminder.entity.MemorizationLog
import com.laev.reminder.service.dto.ItemMemorizationCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemorizationLogRepository: JpaRepository<MemorizationLog, Long> {
    @Query("""
        select new com.laev.reminder.service.dto.ItemMemorizationCount(
                cast(coalesce(sum(case when m.isMemorized = true then 1 else 0 end), 0) as int),
                cast(coalesce(sum(case when m.isMemorized = false then 1 else 0 end), 0) as int)
            )
        from MemorizationLog m 
        where m.item.id = :itemId
    """)
    fun findMemorizationCountsByItemId(itemId: Long): ItemMemorizationCount
}