package com.laev.reminder.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class Item (
    @Id
    val id: Long = 0,

    @Column(nullable = false)
    val mainText: String,

    @Column
    val subText: String,
)