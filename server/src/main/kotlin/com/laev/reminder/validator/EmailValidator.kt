package com.laev.reminder.validator

import com.laev.reminder.annotation.ValidEmail
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EmailValidator : ConstraintValidator<ValidEmail, String> {
    private val emailRegex =
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return false
        return emailRegex.matches(value)
    }
}