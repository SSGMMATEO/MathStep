package com.sanchez.mathstep.data.repository

import android.content.Context
import com.sanchez.mathstep.data.remote.ApiState
import com.sanchez.mathstep.data.remote.GraphData
import com.sanchez.mathstep.data.remote.RetrofitClient
import com.sanchez.mathstep.util.MathStepGenerator
import com.sanchez.mathstep.util.NetworkUtils
import java.util.Locale

/**
 * MathApiRepository — soporte offline real + datos para graficar.
 * Orden de resolución:
 *   1. Ecuación lineal (ax+b=c) → SIEMPRE local, y expone (a, b) para
 *      poder graficar la recta con QuickChart.io si hay internet.
 *   2. Expresión aritmética simple → evaluador local (sin internet).
 *   3. Expresión más compleja → API MathJS, solo si hay conexión.
 */
class MathApiRepository(private val context: Context) {

    suspend fun evaluate(expression: String): ApiState {
        val clean = expression.replace(" ", "").lowercase()

        if (clean.contains("=") && clean.contains("x")) {
            val parts = clean.split("=")
            if (parts.size == 2) {
                val leftRaw = parts[0]
                val rightRaw = parts[1]

                val rightVal = rightRaw.toDoubleOrNull()
                    ?: evaluateExpressionOffline(rightRaw)
                    ?: if (NetworkUtils.isOnline(context)) safeApiCall(rightRaw) else null

                if (rightVal != null) {
                    val solved = solveLinear(leftRaw, rightVal)
                    if (solved != null) {
                        val steps = MathStepGenerator.generateSteps(expression, solved.text, rightVal)
                        val graph = GraphData(solved.a, solved.b, rightVal)
                        return ApiState.Success(solved.text, steps, graph)
                    }
                }
                return ApiState.Error("No se pudo interpretar la ecuación. Revisa el formato (ej: 2x + 3 = 7).")
            }
        }

        // FIX: si queda un "=" sin resolver (ecuación sin "x", o con otra
        // variable como "y"), o cualquier letra distinta de "x", NUNCA debe
        // pasar al evaluador aritmético. Antes se "limpiaba" silenciosamente
        // (se borraban el "=" y las letras) y se evaluaba lo que quedaba,
        // devolviendo un número incorrecto sin avisar del error.
        if (clean.contains("=") || clean.any { it.isLetter() && it != 'x' }) {
            return ApiState.Error("No se pudo interpretar la ecuación. Revisa el formato (ej: 2x + 3 = 7).")
        }

        val localResult = evaluateExpressionOffline(clean)
        if (localResult != null) {
            val formatted = formatNumber(localResult)
            val steps = MathStepGenerator.generateSteps(expression, formatted)
            return ApiState.Success(formatted, steps) // sin gráfica: no es una recta
        }

        if (!NetworkUtils.isOnline(context)) {
            return ApiState.Error("Sin conexión a internet. Esta expresión requiere la API en línea.")
        }

        return try {
            val result = RetrofitClient.mathApi.evaluate(expression)
            val cleanResult = result.trim()
            val steps = MathStepGenerator.generateSteps(expression, cleanResult)
            ApiState.Success(cleanResult, steps)
        } catch (e: Exception) {
            ApiState.Error("Error al calcular. Verifica la expresión o tu conexión.")
        }
    }

    private suspend fun safeApiCall(expr: String): Double? = try {
        RetrofitClient.mathApi.evaluate(expr).trim().toDoubleOrNull()
    } catch (e: Exception) { null }

    private data class LinearSolution(val text: String, val a: Double, val b: Double)

    private fun solveLinear(leftSide: String, rightVal: Double): LinearSolution? {
        val left = leftSide.replace("*", "").lowercase()

        val pattern1 = Regex("([-+]?\\d*\\.?\\d*)x([-+]\\d*\\.?\\d*)?")
        val match1 = pattern1.matchEntire(left)
        if (match1 != null) {
            val a = parseA(match1.groupValues[1])
            val b = match1.groupValues[2].toDoubleOrNull() ?: 0.0
            if (a == 0.0) return null
            return LinearSolution(formatX((rightVal - b) / a), a, b)
        }

        val pattern2 = Regex("([-+]?\\d*\\.?\\d*)([-+]\\d*\\.?\\d*)x")
        val match2 = pattern2.matchEntire(left)
        if (match2 != null) {
            val b = match2.groupValues[1].toDoubleOrNull() ?: 0.0
            val a = parseA(match2.groupValues[2])
            if (a == 0.0) return null
            return LinearSolution(formatX((rightVal - b) / a), a, b)
        }

        return null
    }

    private fun parseA(s: String): Double = when {
        s.isEmpty() || s == "+" -> 1.0
        s == "-" -> -1.0
        else -> s.toDoubleOrNull() ?: 1.0
    }

    private fun formatX(v: Double): String = "x = ${formatNumber(v)}"

    private fun formatNumber(v: Double): String {
        return if (v % 1.0 == 0.0) {
            v.toLong().toString()
        } else {
            val s = String.format(Locale.US, "%.2f", v)
            s.replace(Regex("0+$"), "").replace(Regex("\\.$"), "")
        }
    }

    private fun evaluateExpressionOffline(expr: String): Double? {
        return try {
            val sanitized = expr.replace(Regex("[^0-9+\\-*/().]"), "")
            if (sanitized.isEmpty()) return null
            SimpleExpressionParser(sanitized).parse()
        } catch (e: Exception) {
            null
        }
    }
}

private class SimpleExpressionParser(private val text: String) {
    private var pos = 0

    fun parse(): Double {
        val result = parseExpr()
        if (pos != text.length) throw IllegalArgumentException("Expresión inválida")
        return result
    }

    private fun parseExpr(): Double {
        var value = parseTerm()
        while (pos < text.length && (text[pos] == '+' || text[pos] == '-')) {
            val op = text[pos]; pos++
            val next = parseTerm()
            value = if (op == '+') value + next else value - next
        }
        return value
    }

    private fun parseTerm(): Double {
        var value = parseFactor()
        while (pos < text.length && (text[pos] == '*' || text[pos] == '/')) {
            val op = text[pos]; pos++
            val next = parseFactor()
            value = if (op == '*') value * next else value / next
        }
        return value
    }

    private fun parseFactor(): Double {
        if (pos < text.length && text[pos] == '-') {
            pos++
            return -parseFactor()
        }
        if (pos < text.length && text[pos] == '(') {
            pos++
            val value = parseExpr()
            if (pos >= text.length || text[pos] != ')') throw IllegalArgumentException("Paréntesis sin cerrar")
            pos++
            return value
        }
        val start = pos
        while (pos < text.length && (text[pos].isDigit() || text[pos] == '.')) pos++
        if (start == pos) throw IllegalArgumentException("Número esperado")
        return text.substring(start, pos).toDouble()
    }
}