package com.dlapps.acahelp.utils

fun mapErrorCodeToMessage(code: String): String {
    return when (code) {
        "USER_ALREADY_EXISTS" -> "Usuario existente"
        "INVALID_CREDENTIALS" -> "Credenciales inválidas"
        else -> "Error desconocido: " + code
    }
}