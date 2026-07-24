package com.sanchez.mathstep.data.remote

sealed class ApiState {
    data object Idle    : ApiState()
    data object Loading : ApiState()
    data class  Success(
        val result: String,
        val steps: List<String> = emptyList(),
        val graph: GraphData? = null
    ) : ApiState()
    data class  Error(val message: String)  : ApiState()
}

/**
 * GraphData — coeficientes de una ecuación lineal (y = slope·x + intercept)
 * junto con el valor del lado derecho (rightSide), usados para construir
 * la URL de la gráfica con QuickChart.io. Solo se llena cuando la
 * ecuación resuelta es lineal; para expresiones sin "x" queda null.
 */
data class GraphData(
    val slope: Double,
    val intercept: Double,
    val rightSide: Double
)