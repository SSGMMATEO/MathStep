package com.sanchez.mathstep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "history" en SQLite.
 * Campos según el modelo ER del Entregable 6:
 *   equation  → lo que el usuario escribió (raw_input)
 *   result    → resultado final (final_result)
 *   savedAt   → timestamp en milisegundos (saved_at)
 */
@Entity(tableName = "history")
data class HistoryRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val equation: String,
    val result: String,
    val savedAt: Long = System.currentTimeMillis()
)