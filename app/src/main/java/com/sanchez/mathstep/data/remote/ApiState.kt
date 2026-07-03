package com.sanchez.mathstep.data.remote

/**
 * ApiState — sealed class para los 3 estados de una llamada a API.
 *
 * sealed class: el compilador conoce todos los subtipos posibles.
 * Esto permite usar when(state) sin else, y el compilador avisa
 * si falta manejar algún caso.
 *
 * Idle:    estado inicial, no se ha hecho ninguna llamada.
 * Loading: la llamada está en curso, mostrar spinner.
 * Success: la llamada terminó bien, data contiene el resultado.
 * Error:   la llamada falló, message contiene el motivo.
 */
sealed class ApiState {
    data object Idle    : ApiState()
    data object Loading : ApiState()
    data class  Success(
        val result: String,
        val steps: List<String> = emptyList()
    ) : ApiState()
    data class  Error(val message: String)  : ApiState()
}
