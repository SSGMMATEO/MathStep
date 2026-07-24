package com.sanchez.mathstep.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val darkMode: Boolean = false,
    val autoSave: Boolean = true
)

/**
 * SettingsViewModel — persiste HU-05 (modo oscuro) y el auto-guardado
 * en SharedPreferences, como se documentó en el Entregable 6. Antes
 * esta pantalla no existía y los switches del wireframe no hacían nada.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mathstep_prefs", 0)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            darkMode = prefs.getBoolean(KEY_DARK_MODE, false),
            autoSave = prefs.getBoolean(KEY_AUTO_SAVE, true)
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setDarkMode(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
        _uiState.value = _uiState.value.copy(darkMode = value)
    }

    fun setAutoSave(value: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SAVE, value).apply()
        _uiState.value = _uiState.value.copy(autoSave = value)
    }

    companion object {
        const val KEY_DARK_MODE = "pref_dark_mode"
        const val KEY_AUTO_SAVE = "pref_auto_save"
    }
}