package com.laev.reminder.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Entity
class ReviewItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val mainText: String,

    @Column
    val subText: String?,

    @Column
    val createdDatetime: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC),

    @Column
    val isRecurring: Boolean = true,

    @Column
    val reviewDates: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
)