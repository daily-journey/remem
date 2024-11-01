package com.laev.reminder.utils

object CycleCaculator {
    fun getReviewDates(firstDatetime: Long, cycles: List<Int>): List<Long> {
        val oneDayEpoch = 24 * 60 * 60

        return cycles.map { cycle -> firstDatetime + oneDayEpoch * cycle }
    }
}