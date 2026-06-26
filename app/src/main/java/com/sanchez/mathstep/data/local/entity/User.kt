package com.sanchez.mathstep.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "users" en Room/SQLite.
 * indices unique en email: dos usuarios no pueden tener el mismo correo.
 * Si intentas insertar un email duplicado, Room lanza excepción.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,       // SHA-256, nunca texto plano
    val createdAt: Long = System.currentTimeMillis()
)
