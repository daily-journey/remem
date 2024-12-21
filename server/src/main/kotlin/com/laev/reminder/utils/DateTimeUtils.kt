package com.laev.reminder.utils

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object DateTimeUtils {
    fun getCurrentUtcTime(): OffsetDateTime {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
    }
}