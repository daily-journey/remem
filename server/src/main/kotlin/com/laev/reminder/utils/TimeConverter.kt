package com.laev.reminder.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimeConverter {
    companion object {
        // Converts epoch seconds to ISO string in EDT
        fun epochToISO(epochSeconds: Long): String {
            return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
}