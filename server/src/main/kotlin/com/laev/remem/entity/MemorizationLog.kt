package com.laev.remem.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
class MemorizationLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    val isMemorized: Boolean,

    @Column(nullable = false)
    val createdDatetime: OffsetDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val reviewItem: ReviewItem,
)
