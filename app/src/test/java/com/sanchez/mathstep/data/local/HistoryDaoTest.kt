package com.sanchez.mathstep.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sanchez.mathstep.data.local.dao.HistoryDao
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * HistoryDaoTest — prueba de integración del CRUD sobre HistoryDao.
 * Usa Room en memoria: se crea y destruye por cada método de prueba,
 * así ninguna prueba afecta el resultado de otra.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class HistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: HistoryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // solo en pruebas: permite consultar justo después de insertar sin cambiar de hilo
            .build()
        dao = db.historyDao()
    }

    @After
    fun tearDown() {
        // Cierra la base de datos en memoria: libera los recursos y garantiza
        // que la siguiente prueba arranque desde cero, sin datos residuales.
        db.close()
    }

    @Test
    fun insertarYLeerHistoryRecord_datosCorrectos() = runBlocking {
        // ARRANGE
        val record = HistoryRecord(
            equation = "2x + 3 = 7",
            result = "x = 2",
            steps = "Paso 1|||Paso 2"
        )

        // ACT
        dao.insert(record)
        val registros = dao.getAll().first()

        // ASSERT
        assertEquals(1, registros.size)
        assertEquals("2x + 3 = 7", registros[0].equation)
        assertEquals("x = 2", registros[0].result)
        assertEquals("Paso 1|||Paso 2", registros[0].steps)
    }

    @Test
    fun eliminarHistoryRecord_yaNoAparece() = runBlocking {
        // ARRANGE
        val record = HistoryRecord(equation = "5x = 20", result = "x = 4")
        dao.insert(record)
        val insertado = dao.getAll().first().first()

        // ACT
        dao.delete(insertado)
        val registros = dao.getAll().first()

        // ASSERT
        assertTrue("Después de eliminar, la lista debe quedar vacía", registros.isEmpty())
    }
}