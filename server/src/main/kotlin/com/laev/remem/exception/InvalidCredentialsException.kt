package com.laev.remem.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentialsException : ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password")
