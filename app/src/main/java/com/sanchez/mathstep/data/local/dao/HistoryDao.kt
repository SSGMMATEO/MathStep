package com.sanchez.mathstep.data.local.dao

import androidx.room.*
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: HistoryRecord): Long

    @Query("SELECT * FROM history ORDER BY savedAt DESC")
    fun getAll(): Flow<List<HistoryRecord>>

    @Update
    suspend fun update(record: HistoryRecord)

    @Delete
    suspend fun delete(record: HistoryRecord)
}