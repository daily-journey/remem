package com.laev.remem.entity

import jakarta.persistence.*

@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column
    val email: String,

    @Column
    val password: String,

    @Column
    val name: String,
)
