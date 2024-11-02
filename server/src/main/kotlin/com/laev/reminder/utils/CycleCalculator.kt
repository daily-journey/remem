package com.laev.reminder.utils

import java.time.LocalDateTime

object CycleCalculator {
    fun getReviewDates(createDatetime: LocalDateTime, cycles: List<Int>): List<LocalDateTime> {
        return cycles.map { cycle -> createDatetime.plusDays(cycle.toLong()) }
    }
}