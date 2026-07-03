package com.sanchez.mathstep.data.repository

import com.sanchez.mathstep.data.remote.ApiState
import com.sanchez.mathstep.data.remote.RetrofitClient
import com.sanchez.mathstep.util.MathStepGenerator
import java.util.Locale

/**
 * MathApiRepository — maneja la lógica de cálculo (local para ecuaciones, API para expresiones).
 */
class MathApiRepository {

    suspend fun evaluate(expression: String): ApiState {
        val clean = expression.replace(" ", "").lowercase()
        
        // Soporte para ecuaciones lineales simples (ax + b = c)
        if (clean.contains("=") && clean.contains("x")) {
            val parts = clean.split("=")
            if (parts.size == 2) {
                val leftRaw = parts[0]
                val rightRaw = parts[1]
                
                // Evaluar lado derecho si es una expresión numérica
                val rightVal = rightRaw.toDoubleOrNull() ?: try {
                    val eval = RetrofitClient.mathApi.evaluate(rightRaw)
                    eval.trim().toDoubleOrNull()
                } catch (e: Exception) { null }

                if (rightVal != null) {
                    val solvedValue = solveLinear(leftRaw, rightVal)
                    if (solvedValue != null) {
                        val steps = MathStepGenerator.generateSteps(expression, solvedValue, rightVal)
                        return ApiState.Success(solvedValue, steps)
                    }
                }
            }
        }

        return try {
            // Usar la API para expresiones matemáticas estándar (ej: 2 + 2)
            val result = RetrofitClient.mathApi.evaluate(expression)
            val cleanResult = result.trim()
            val steps = MathStepGenerator.generateSteps(expression, cleanResult)
            ApiState.Success(cleanResult, steps)
        } catch (e: Exception) {
            ApiState.Error("Error al calcular. Verifica la expresión.")
        }
    }

    private fun solveLinear(leftSide: String, rightVal: Double): String? {
        val left = leftSide.replace("*", "").lowercase()
        
        // Caso 1: ax + b = c
        val pattern1 = Regex("([-+]?\\d*\\.?\\d*)x([-+]\\d*\\.?\\d*)?")
        val match1 = pattern1.matchEntire(left)
        if (match1 != null) {
            val a = parseA(match1.groupValues[1])
            val b = match1.groupValues[2].toDoubleOrNull() ?: 0.0
            if (a == 0.0) return null
            return formatX((rightVal - b) / a)
        }

        // Caso 2: b + ax = c
        val pattern2 = Regex("([-+]?\\d*\\.?\\d*)([-+]\\d*\\.?\\d*)x")
        val match2 = pattern2.matchEntire(left)
        if (match2 != null) {
            val b = match2.groupValues[1].toDoubleOrNull() ?: 0.0
            val a = parseA(match2.groupValues[2])
            if (a == 0.0) return null
            return formatX((rightVal - b) / a)
        }

        return null
    }

    private fun parseA(s: String): Double = when {
        s.isEmpty() || s == "+" -> 1.0
        s == "-" -> -1.0
        else -> s.toDoubleOrNull() ?: 1.0
    }

    private fun formatX(v: Double): String {
        val res = if (v % 1.0 == 0.0) {
            v.toLong().toString()
        } else {
            val s = String.format(Locale.US, "%.2f", v)
            s.replace(Regex("0+$"), "").replace(Regex("\\.$"), "")
        }
        return "x = $res"
    }
}
