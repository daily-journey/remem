package com.laev.reminder.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidFormatException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val errorMessage = ex.cause?.message ?: "Invalid request body"
        return createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(ex: EmailAlreadyExistsException): ResponseEntity<Map<String, String>> {
        val errorMessage = "The email '${ex.email}' is already registered."
        return createErrorResponse(errorMessage, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, String>> {
        val errorMessage = ex.reason ?: "Unexpected error occurred"
        val status = HttpStatus.valueOf(ex.statusCode.value()) // Convert HttpStatusCode to HttpStatus
        return createErrorResponse(errorMessage, status)
    }

    // Utility function for creating consistent error responses
    private fun createErrorResponse(message: String, status: HttpStatus): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(status).body(mapOf("error" to message))
    }
}