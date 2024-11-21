package com.laev.reminder.entity

import jakarta.persistence.*

@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    val email: String,

    @Column
    val password: String,

    @Column
    val name: String,
)