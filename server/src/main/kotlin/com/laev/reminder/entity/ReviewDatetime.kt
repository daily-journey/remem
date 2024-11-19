package com.laev.reminder.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
class ReviewDatetime(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val start: OffsetDateTime,

    @Column(nullable = false)
    val end: OffsetDateTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val item: Item,
)