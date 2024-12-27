package com.laev.remem.repository

import com.laev.remem.entity.MemorizationLog
import com.laev.remem.service.dto.ReviewItemMemorizationCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemorizationLogRepository: JpaRepository<MemorizationLog, Long> {
    @Query(
        """
        select new com.laev.remem.service.dto.ReviewItemMemorizationCount(
                cast(coalesce(sum(case when m.isMemorized = true then 1 else 0 end), 0) as int),
                cast(coalesce(sum(case when m.isMemorized = false then 1 else 0 end), 0) as int)
            )
        from MemorizationLog m 
        where m.reviewItem.id = :itemId
    """
    )
    fun findMemorizationCountsByItemId(itemId: Long): ReviewItemMemorizationCount
}
