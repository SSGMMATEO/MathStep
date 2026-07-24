package com.sanchez.mathstep.util

import java.util.regex.Pattern

/**
 * Validators — funciones puras de validación, sin dependencias de Android.
 * Se extrajeron de LoginScreen/RegisterScreen porque android.util.Patterns
 * requiere el framework de Android en tiempo de ejecución y NO funciona
 * en pruebas unitarias locales (app/src/test). Estas funciones sí se
 * pueden probar directo con JUnit, sin emulador.
 */
object Validators {

    // usuario@dominio.extensión (mínimo 2 letras de extensión)
    private val EMAIL_PATTERN: Pattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidUsername(username: String): Boolean {
        return username.trim().length >= 2
    }

    fun passwordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    /**
     * isValidEquation — regla de negocio del CRUD de HistoryRecord:
     * una ecuación no puede guardarse vacía (CA-01.2 / CA-04.1).
     */
    fun isValidEquation(equation: String): Boolean {
        return equation.isNotBlank()
    }
}