package com.laev.reminder.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

object ObjectMapperUtil {
    fun createObjectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerModule(JavaTimeModule()) // register Java 8 date/time type module
        }
    }
}