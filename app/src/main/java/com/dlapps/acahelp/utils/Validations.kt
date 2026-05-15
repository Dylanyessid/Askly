package com.dlapps.acahelp.utils

data class ValidationResult (
    val isValid: Boolean,
    val errorMessage: String? = null
)
fun validateEmail(email: String): ValidationResult {
    return when{
        email.isBlank() -> {
            ValidationResult(false, "El email no puede estar vacío")
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            ValidationResult(false, "El email no es válido")
        }
        else -> {
            ValidationResult(true)
        }

    }
}