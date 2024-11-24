package com.laev.reminder.exception

class EmailAlreadyExistsException(val email: String) : RuntimeException()