package com.sanchez.mathstep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "history". Se agregó "steps" (pasos unidos con "|||") para poder
 * volver a mostrarlos al abrir un registro del historial en modo lectura,
 * tal como se documentó en la tabla de navegación del Entregable 5 pero
 * nunca se implementó.
 */
@Entity(tableName = "history")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equation: String,
    val result: String,
    val steps: String = "",
    val savedAt: Long = System.currentTimeMillis()
) {
    fun stepsList(): List<String> =
        if (steps.isBlank()) emptyList() else steps.split("|||")

    companion object {
        fun joinSteps(steps: List<String>): String = steps.joinToString("|||")
    }
}