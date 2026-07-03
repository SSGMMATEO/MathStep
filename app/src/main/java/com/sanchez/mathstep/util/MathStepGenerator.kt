package com.sanchez.mathstep.util

/**
 * MathStepGenerator — Utilidad para generar una explicación paso a paso
 * de expresiones matemáticas simples (suma, resta, multiplicación, división).
 */
object MathStepGenerator {

    /**
     * Genera una lista de strings que explican el proceso.
     * Nota: Es una implementación básica para fines didácticos.
     */
    fun generateSteps(expression: String, finalResult: String): List<String> {
        val steps = mutableListOf<String>()
        val cleanExpr = expression.replace(" ", "")

        // Caso 1: Suma simple
        if (cleanExpr.contains("+")) {
            val parts = cleanExpr.split("+")
            if (parts.size == 2) {
                steps.add("Paso 1: Identificar los sumandos.")
                steps.add("Sumando A: ${parts[0]}")
                steps.add("Sumando B: ${parts[1]}")
                steps.add("Paso 2: Realizar la operación aritmética.")
                steps.add("${parts[0]} + ${parts[1]} = $finalResult")
                return steps
            }
        }

        // Caso 2: Multiplicación simple
        if (cleanExpr.contains("*")) {
            val parts = cleanExpr.split("*")
            if (parts.size == 2) {
                steps.add("Paso 1: Identificar los factores.")
                steps.add("Factor 1: ${parts[0]}")
                steps.add("Factor 2: ${parts[1]}")
                steps.add("Paso 2: Multiplicar los valores.")
                steps.add("${parts[0]} * ${parts[1]} = $finalResult")
                return steps
            }
        }

        // Caso genérico si no se detecta patrón simple
        steps.add("Paso 1: Evaluar la expresión completa.")
        steps.add("Expresión: $expression")
        steps.add("Resultado final obtenido: $finalResult")
        
        return steps
    }
}
