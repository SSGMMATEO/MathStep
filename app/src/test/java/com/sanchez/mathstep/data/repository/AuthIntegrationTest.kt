package com.sanchez.mathstep.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sanchez.mathstep.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * AuthIntegrationTest — flujo completo de registro + login contra
 * Room en memoria (DAO real + hashing SHA-256 real de AuthRepository).
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class AuthIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        // SharedPreferences de PRUEBA, con nombre distinto al real
        // ("mathstep_prefs"), para no tocar la sesión real del dispositivo.
        val prefs = context.getSharedPreferences("test_auth_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        repository = AuthRepository(db.userDao(), prefs)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun login_credencialesCorrectas_retornaSuccess() = runBlocking {
        // ARRANGE
        repository.register("Mateo", "mateo@uce.edu.ec", "clave123")

        // ACT
        val resultado = repository.login("mateo@uce.edu.ec", "clave123")

        // ASSERT
        assertTrue("Con correo y contraseña correctos debe retornar Success", resultado is AuthResult.Success)
    }

    @Test
    fun login_contrasenaIncorrecta_retornaError() = runBlocking {
        // ARRANGE
        repository.register("Mateo", "mateo@uce.edu.ec", "clave123")

        // ACT
        val resultado = repository.login("mateo@uce.edu.ec", "claveMala")

        // ASSERT
        assertTrue("Con contraseña incorrecta debe retornar Error", resultado is AuthResult.Error)
    }

    @Test
    fun login_correoNoRegistrado_retornaError() = runBlocking {
        // ACT — nadie se registró con este correo
        val resultado = repository.login("noexiste@uce.edu.ec", "cualquiera")

        // ASSERT
        assertTrue("Un correo no registrado debe retornar Error", resultado is AuthResult.Error)
    }
}