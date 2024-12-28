package com.laev.remem.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
class ReviewDatetime(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    val start: OffsetDateTime,

    @Column(nullable = false)
    val end: OffsetDateTime = start.plusDays(1),

    @Column(nullable = true)
    val isMemorized: Boolean? = null,

    @Column(nullable = true)
    val isSkipped: Boolean? = null,

    @Column(nullable = false)
    val isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val reviewItem: ReviewItem,
)
