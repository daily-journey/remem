package com.laev.reminder.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(nullable = false)
    val mainText: String,

    @Column
    val subText: String?,

    @Column
    val createDatetime: LocalDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC),

    @Column
    val successCount: Short = 0,

    @Column
    val failCount: Short = 0,

    @Column
    val isRecurring: Boolean = true,

    @Column
    val reviewDates: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
)