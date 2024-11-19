package com.laev.reminder.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidFormatException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val errorMessage = ex.cause?.message ?: "Invalid request body"

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to errorMessage))
    }
}