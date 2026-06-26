package com.sanchez.mathstep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sanchez.mathstep.data.local.entity.User

/**
 * DAO: Room genera el SQL automáticamente a partir de estas anotaciones.
 * suspend: las funciones se ejecutan en background sin bloquear la UI.
 */
@Dao
interface UserDao {

    // OnConflictStrategy.ABORT: falla si el email ya existe (índice único)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    // Retorna null si no existe el email (no lanza excepción)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}
