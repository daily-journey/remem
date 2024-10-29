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
    val createDatetime: Long,

    @Column
    val successCount: Int,

    @Column
    val failCount: Int,

    @Column
    val isRecurring: Boolean,

    @Column
    val periods: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,
)