package com.sanchez.mathstep.util

import org.junit.Assert.*
import org.junit.Test

/**
 * ValidacionesTest — pruebas unitarias (JUnit 4, patrón AAA) de las
 * funciones puras en Validators.kt. Van en app/src/test/java porque
 * no dependen de Context, Activity ni android.util.Patterns: corren
 * directo en la JVM, sin emulador, en segundos.
 */
class ValidacionesTest {

    // ── PRUEBA 1 — Validación de correo (CA-01.2) ─────────────

    @Test
    fun validarCorreo_formatoValido_retornaTrue() {
        // ARRANGE
        val correoValido = "mateo@uce.edu.ec"
        // ACT
        val resultado = Validators.isValidEmail(correoValido)
        // ASSERT — assertTrue falla el test si "resultado" es false
        assertTrue("Un correo con formato correcto debe ser válido", resultado)
    }

    @Test
    fun validarCorreo_sinArroba_retornaFalse() {
        // ARRANGE
        val correoInvalido = "mateo.gmail.com"
        // ACT
        val resultado = Validators.isValidEmail(correoInvalido)
        // ASSERT — assertFalse falla el test si "resultado" es true.
        // Si fallara aquí, significaría que se acepta texto sin @ como
        // correo válido: un bug real de validación.
        assertFalse("Un correo sin @ no debe ser válido", resultado)
    }

    @Test
    fun validarCorreo_dosArrobas_retornaFalse() {
        // Caso de borde: dos símbolos @.
        val resultado = Validators.isValidEmail("mateo@@uce.edu.ec")
        assertFalse("Un correo con doble @ no debe ser válido", resultado)
    }

    @Test
    fun validarCorreo_empiezaConArroba_retornaFalse() {
        // Caso de borde: no hay usuario antes del @.
        val resultado = Validators.isValidEmail("@uce.edu.ec")
        assertFalse("Un correo que empieza con @ no debe ser válido", resultado)
    }

    @Test
    fun validarCorreo_sinExtension_retornaFalse() {
        // Caso de borde: dominio sin extensión (.com, .ec, etc.)
        val resultado = Validators.isValidEmail("mateo@uce")
        assertFalse("Un correo sin extensión no debe ser válido", resultado)
    }

    // ── PRUEBA 2 — Validación de contraseña (CA-01.2) ─────────

    @Test
    fun validarContrasena_menosDeSeisCaracteres_retornaFalse() {
        // ARRANGE
        val contrasenaCorta = "abc12" // 5 caracteres
        // ACT
        val resultado = Validators.isValidPassword(contrasenaCorta)
        // ASSERT
        assertFalse("Una contraseña de menos de 6 caracteres no debe ser válida", resultado)
    }

    @Test
    fun validarContrasena_seisCaracteresExactos_retornaTrue() {
        // Caso de borde: el límite exacto (6) debe aceptarse, no rechazarse.
        val resultado = Validators.isValidPassword("abc123")
        assertTrue("Una contraseña de exactamente 6 caracteres debe ser válida", resultado)
    }

    @Test
    fun validarContrasena_vacia_retornaFalse() {
        // Caso de borde: cadena vacía.
        val resultado = Validators.isValidPassword("")
        assertFalse("Una contraseña vacía no debe ser válida", resultado)
    }

    // ── PRUEBA 3 — Lógica de negocio del CRUD ─────────────────
    // Regla: una ecuación no puede guardarse vacía (CA-04.1, CA-01.2)

    @Test
    fun validarEcuacion_vacia_retornaFalse() {
        // ARRANGE
        val ecuacionVacia = ""
        // ACT
        val resultado = Validators.isValidEquation(ecuacionVacia)
        // ASSERT — si esto fallara (resultado = true), la app permitiría
        // crear registros de historial sin ecuación, rompiendo CA-04.1.
        assertFalse("Una ecuación vacía no se debe poder guardar", resultado)
    }

    @Test
    fun validarEcuacion_soloEspacios_retornaFalse() {
        // Caso de borde: espacios en blanco no cuentan como contenido real.
        val resultado = Validators.isValidEquation("   ")
        assertFalse("Una ecuación con solo espacios no debe ser válida", resultado)
    }

    @Test
    fun validarEcuacion_conContenido_retornaTrue() {
        // ARRANGE
        val ecuacionValida = "2x + 3 = 7"
        // ACT
        val resultado = Validators.isValidEquation(ecuacionValida)
        // ASSERT
        assertTrue("Una ecuación con contenido debe ser válida", resultado)
    }

    // ── BONUS — confirmar contraseña (CA-03.2 en RegisterScreen) ──

    @Test
    fun contrasenasCoinciden_iguales_retornaTrue() {
        assertTrue(Validators.passwordsMatch("abc123", "abc123"))
    }

    @Test
    fun contrasenasCoinciden_diferentes_retornaFalse() {
        assertFalse(Validators.passwordsMatch("abc123", "abc124"))
    }
}