package com.laev.reminder.entity

import jakarta.persistence.*

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
    val createDatetime: Long = 0,

    @Column
    val successCount: Short = 0,

    @Column
    val failCount: Short = 0,

    @Column
    val isRecurring: Boolean = true,

    @Column
    val periods: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
)