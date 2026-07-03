package com.sanchez.mathstep.util

import java.util.Locale

/**
 * MathStepGenerator — Utilidad para generar una explicación paso a paso
 * de expresiones matemáticas y ecuaciones lineales.
 */
object MathStepGenerator {

    fun generateSteps(expression: String, finalResult: String, evaluatedRightSide: Double? = null): List<String> {
        val steps = mutableListOf<String>()
        val cleanExpr = expression.replace(" ", "").lowercase()

        // Caso: Ecuación Lineal (contiene '=' y 'x')
        if (cleanExpr.contains("=") && cleanExpr.contains("x")) {
            return generateAlgebraSteps(expression, finalResult, evaluatedRightSide)
        }

        // Caso 1: Suma simple
        if (cleanExpr.contains("+") && !cleanExpr.contains("-") && !cleanExpr.contains("*") && !cleanExpr.contains("/")) {
            val parts = cleanExpr.split("+").filter { it.isNotEmpty() }
            steps.add("Operación: Adición")
            steps.add("Paso 1: Identificar los sumandos: ${parts.joinToString(", ")}")
            steps.add("Paso 2: Sumar todos los valores para obtener el total.")
            steps.add("Resultado: $finalResult")
            return steps
        }

        // Caso 2: Resta simple
        if (cleanExpr.contains("-") && !cleanExpr.contains("+") && !cleanExpr.contains("*") && !cleanExpr.contains("/")) {
            val parts = cleanExpr.split("-").filter { it.isNotEmpty() }
            if (parts.size >= 2) {
                steps.add("Operación: Substracción")
                steps.add("Paso 1: Restar sucesivamente de ${parts[0]}.")
                steps.add("Resultado: $finalResult")
                return steps
            }
        }

        // Caso 3: Multiplicación simple
        if ((cleanExpr.contains("*") || cleanExpr.contains("x")) && !cleanExpr.contains("=") && !cleanExpr.contains("+") && !cleanExpr.contains("-") && !cleanExpr.contains("/")) {
            steps.add("Operación: Multiplicación")
            steps.add("Paso 1: Multiplicar los factores.")
            steps.add("Resultado: $finalResult")
            return steps
        }

        // Caso 4: División simple
        if (cleanExpr.contains("/") && !cleanExpr.contains("+") && !cleanExpr.contains("-") && !cleanExpr.contains("*")) {
            val parts = cleanExpr.split("/")
            if (parts.size == 2) {
                steps.add("Operación: División")
                steps.add("Paso 1: Dividir el dividendo (${parts[0]}) por el divisor (${parts[1]}).")
                steps.add("Resultado: $finalResult")
                return steps
            }
        }

        // Caso genérico
        steps.add("Paso 1: Analizar la jerarquía de operaciones (PEMDAS).")
        steps.add("Paso 2: Evaluar la expresión paso a paso.")
        steps.add("Resultado final: $finalResult")
        
        return steps
    }

    private fun generateAlgebraSteps(expression: String, result: String, evaluatedRightSide: Double?): List<String> {
        val steps = mutableListOf<String>()
        val cleanNoSpace = expression.replace(" ", "").lowercase()
        val parts = cleanNoSpace.split("=")
        
        steps.add("Objetivo: Hallar el valor de 'x'.")
        steps.add("Ecuación inicial: $expression")

        try {
            if (parts.size == 2) {
                val left = parts[0].replace("*", "")
                val rightRaw = parts[1]
                val c = evaluatedRightSide ?: rightRaw.toDoubleOrNull() ?: 0.0
                
                val pattern1 = Regex("([-+]?\\d*\\.?\\d*)x([-+]\\d*\\.?\\d*)?")
                val pattern2 = Regex("([-+]?\\d*\\.?\\d*)([-+]\\d*\\.?\\d*)x")
                
                val match1 = pattern1.matchEntire(left)
                val match2 = pattern2.matchEntire(left)

                var a = 1.0
                var b = 0.0

                if (match1 != null) {
                    val aStr = match1.groupValues[1]
                    val bStr = match1.groupValues[2]
                    a = parseA(aStr)
                    b = bStr.toDoubleOrNull() ?: 0.0
                } else if (match2 != null) {
                    val bStr = match2.groupValues[1]
                    val aStr = match2.groupValues[2]
                    b = bStr.toDoubleOrNull() ?: 0.0
                    a = parseA(aStr)
                }

                var stepNum = 1

                // Paso opcional: Evaluar lado derecho si es una expresión
                if (rightRaw.toDoubleOrNull() == null && evaluatedRightSide != null) {
                    steps.add("Paso $stepNum: Evaluar la expresión del lado derecho.")
                    steps.add("$rightRaw = ${fmt(c)}")
                    stepNum++
                }

                if (b != 0.0) {
                    val opDesc = if (b > 0) "Restar ${fmt(b)}" else "Sumar ${fmt(Math.abs(b))}"
                    steps.add("Paso $stepNum: $opDesc de ambos lados.")
                    val aDisp = when(a) { 1.0 -> ""; -1.0 -> "-"; else -> fmt(a) }
                    steps.add("${aDisp}x = ${fmt(c)} - (${fmt(b)})")
                    steps.add("${aDisp}x = ${fmt(c - b)}")
                    stepNum++
                }
                
                if (a != 1.0) {
                    steps.add("Paso $stepNum: Dividir por el coeficiente ${fmt(a)}.")
                    steps.add("x = ${fmt(c - b)} / ${fmt(a)}")
                } else if (b == 0.0 && stepNum == 1) {
                    steps.add("La variable 'x' ya está despejada.")
                }
            }
        } catch (e: Exception) {
            steps.add("Paso 1: Simplificar términos.")
            steps.add("Paso 2: Despejar 'x'.")
        }
        
        steps.add("Resultado final: $result")
        return steps
    }

    private fun parseA(s: String): Double = when {
        s.isEmpty() || s == "+" -> 1.0
        s == "-" -> -1.0
        else -> s.toDoubleOrNull() ?: 1.0
    }

    private fun fmt(v: Double): String {
        return if (v % 1.0 == 0.0) {
            v.toLong().toString()
        } else {
            val s = String.format(Locale.US, "%.2f", v)
            s.replace(Regex("0+$"), "").replace(Regex("\\.$"), "")
        }
    }
}
