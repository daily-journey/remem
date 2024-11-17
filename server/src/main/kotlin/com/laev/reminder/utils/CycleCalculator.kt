package com.laev.reminder.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset

object CycleCalculator {
    fun getReviewDates(createDatetime: OffsetDateTime, cycles: List<Int>): List<OffsetDateTime> {
        return cycles.map { cycle -> createDatetime.plusDays(cycle.toLong()) }
    }

    // Calculate the next day's 00:00
    fun getUTCStartDatetime(
        zonedCreatedDatetime: OffsetDateTime, cycle: Int, zoneOffset: ZoneOffset
    ): OffsetDateTime {
        return zonedCreatedDatetime
            .toLocalDate() // Extract only the date
            .plusDays(cycle.toLong())
            .atStartOfDay() // Set the time to 00:00
            .atOffset(zoneOffset) // Convert back to OffsetDateTime
    }
}