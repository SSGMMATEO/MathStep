package com.sanchez.mathstep.data.repository

import android.content.SharedPreferences
import com.sanchez.mathstep.data.local.dao.UserDao
import com.sanchez.mathstep.data.local.entity.User
import java.security.MessageDigest

/**
 * AuthRepository — lógica de autenticación.
 * ViewModel → Repository → DAO → Room
 * El ViewModel nunca toca Room directamente.
 */
class AuthRepository(
    private val userDao: UserDao,
    private val prefs: SharedPreferences
) {
    companion object {
        const val KEY_USER_ID = "logged_user_id"
    }

    // SHA-256: irreversible. Para verificar: hasheas lo ingresado y comparas.
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun register(username: String, email: String, password: String): AuthResult {
        return try {
            userDao.insertUser(
                User(
                    username = username,
                    email = email.lowercase().trim(),
                    passwordHash = hashPassword(password)
                )
            )
            AuthResult.Success("Cuenta creada exitosamente")
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            AuthResult.Error("Este correo ya está registrado")
        } catch (e: Exception) {
            AuthResult.Error("Error inesperado: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val user = userDao.getUserByEmail(email.lowercase().trim())
                ?: return AuthResult.Error("No existe una cuenta con ese correo")

            if (hashPassword(password) == user.passwordHash) {
                prefs.edit().putInt(KEY_USER_ID, user.id).apply()
                AuthResult.Success("Bienvenido, ${user.username}")
            } else {
                AuthResult.Error("Contraseña incorrecta")
            }
        } catch (e: Exception) {
            AuthResult.Error("Error al iniciar sesión: ${e.message}")
        }
    }

    fun logout() = prefs.edit().remove(KEY_USER_ID).apply()
    fun isLoggedIn(): Boolean = prefs.getInt(KEY_USER_ID, -1) != -1
}

sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}