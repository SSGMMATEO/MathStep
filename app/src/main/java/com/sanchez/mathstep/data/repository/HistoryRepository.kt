package com.sanchez.mathstep.data.repository

import com.sanchez.mathstep.data.local.dao.HistoryDao
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import kotlinx.coroutines.flow.Flow

/**
 * HistoryRepository — capa intermedia entre ViewModel y DAO.
 *
 * Por qué existe esta capa:
 *   El ViewModel no sabe de dónde vienen los datos (Room, API, caché).
 *   Si mañana agregas sincronización en la nube, solo cambias el Repository;
 *   el ViewModel y la UI no se tocan.
 */
class HistoryRepository(private val dao: HistoryDao) {

    val allRecords: Flow<List<HistoryRecord>> = dao.getAll()

    suspend fun insert(record: HistoryRecord) = dao.insert(record)

    suspend fun update(record: HistoryRecord) = dao.update(record)

    suspend fun delete(record: HistoryRecord) = dao.delete(record)
}