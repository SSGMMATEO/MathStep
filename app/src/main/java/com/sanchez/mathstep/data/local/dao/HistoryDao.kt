package com.sanchez.mathstep.data.local.dao

import androidx.room.*
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import kotlinx.coroutines.flow.Flow

/**
 * DAO de historial — las 4 operaciones CRUD.
 *
 * Flow<List<HistoryRecord>>: Room emite la lista actualizada automáticamente
 * cada vez que hay un INSERT, UPDATE o DELETE en la tabla. El ViewModel
 * no necesita volver a pedir los datos; llegan solos.
 *
 * suspend: INSERT, UPDATE y DELETE son operaciones puntuales que se
 * ejecutan una vez y terminan, por eso usan suspend + coroutine.
 * Flow no necesita suspend porque es un stream continuo.
 */
@Dao
interface HistoryDao {

    // CREATE — inserta un registro; ignora si ya existe el mismo id
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: HistoryRecord): Long

    // READ — emite la lista completa ordenada del más reciente al más antiguo
    @Query("SELECT * FROM history ORDER BY savedAt DESC")
    fun getAll(): Flow<List<HistoryRecord>>

    // UPDATE — actualiza todos los campos del registro que coincida por id
    @Update
    suspend fun update(record: HistoryRecord)

    // DELETE — elimina el registro que coincida por id
    @Delete
    suspend fun delete(record: HistoryRecord)
}