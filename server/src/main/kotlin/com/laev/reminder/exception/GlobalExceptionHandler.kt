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
    fun handleInvalidFormatException(e: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val errorMessage = e.cause?.message ?: "Invalid request body"
        return createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(e: EmailAlreadyExistsException): ResponseEntity<Map<String, String>> {
        val errorMessage = "The email '${e.email}' is already registered."
        return createErrorResponse(errorMessage, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<Map<String, String>> {
        val errorMessage = e.reason ?: "Unexpected error occurred"
        val status = HttpStatus.valueOf(e.statusCode.value()) // Convert HttpStatusCode to HttpStatus
        return createErrorResponse(errorMessage, status)
    }

    @ExceptionHandler(ItemNotFoundException::class)
    fun handleItemNotFoundException(e: ItemNotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
    }

    @ExceptionHandler(ItemAlreadyDeletedException::class)
    fun handleItemAlreadyDeletedException(e: ItemAlreadyDeletedException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.GONE).body(e.message)
    }

    // Utility function for creating consistent error responses
    private fun createErrorResponse(message: String, status: HttpStatus): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(status).body(mapOf("error" to message))
    }
}