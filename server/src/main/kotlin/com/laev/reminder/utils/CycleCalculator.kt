package com.laev.reminder.utils

import java.time.OffsetDateTime

object CycleCalculator {
    fun getReviewDates(createDatetime: OffsetDateTime, cycles: List<Int>): List<OffsetDateTime> {
        return cycles.map { cycle -> createDatetime.plusDays(cycle.toLong()) }
    }
}