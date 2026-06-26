package com.sanchez.mathstep.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanchez.mathstep.data.local.AppDatabase
import com.sanchez.mathstep.data.repository.AuthRepository
import com.sanchez.mathstep.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel — versión Compose usa StateFlow en lugar de LiveData.
 * StateFlow: flujo de datos que Compose observa con collectAsState().
 * AndroidViewModel: recibe Application para acceder a Context (Room, SharedPrefs).
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // ── Inicialización del Repository ─────────────────────────
    // Se hace aquí porque AndroidViewModel tiene acceso a Application.
    private val repository: AuthRepository by lazy {
        val db = AppDatabase.getInstance(application)
        val prefs = application.getSharedPreferences("mathstep_prefs", 0)
        AuthRepository(db.userDao(), prefs)
    }

    // ── Estado de la UI ───────────────────────────────────────
    // UiState agrupa todo lo que la pantalla necesita saber.
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── Funciones llamadas desde los Composables ──────────────

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.register(username, email, password)) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    registerSuccess = true
                )
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState() // Reset state
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}

/**
 * AuthUiState — estado completo de la pantalla de auth.
 * data class con copy(): Compose detecta cambios y redibuja solo
 * los componentes que usan el valor que cambió.
 *
 * isLoading     → muestra el spinner, deshabilita el botón
 * isLoggedIn    → login exitoso → navegar a HomeScreen
 * registerSuccess → registro exitoso → volver al login
 * error         → mensaje de error a mostrar (null = sin error)
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val registerSuccess: Boolean = false,
    val error: String? = null
)
