package com.sanchez.mathstep.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanchez.mathstep.data.local.AppDatabase
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import com.sanchez.mathstep.data.remote.ApiState
import com.sanchez.mathstep.data.repository.HistoryRepository
import com.sanchez.mathstep.data.repository.MathApiRepository
import com.sanchez.mathstep.ui.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * HistoryViewModel actualizado con CRUD completo.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository by lazy {
        val dao = AppDatabase.getInstance(application).historyDao()
        HistoryRepository(dao)
    }

    private val apiRepository = MathApiRepository()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        repository.allRecords
            .onEach { list -> _uiState.update { it.copy(records = list) } }
            .launchIn(viewModelScope)
    }

    // ── CREATE / UPDATE ──────────────────────────────────────

    fun requestCreate() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun dismissCreate() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun requestEdit(record: HistoryRecord) {
        _uiState.update { it.copy(recordToEdit = record, showEditDialog = true) }
    }

    fun confirmEdit(equation: String) {
        val original = _uiState.value.recordToEdit
        viewModelScope.launch {
            val apiResult = apiRepository.evaluate(equation)
            val resultText = if (apiResult is ApiState.Success) {
                // Notificar al editar si es exitoso
                NotificationScheduler.triggerResolutionNotification(
                    getApplication(),
                    equation,
                    apiResult.result
                )
                apiResult.result
            } else "Error en cálculo"

            if (original != null) {
                // UPDATE
                repository.update(original.copy(equation = equation, result = resultText))
            } else {
                // CREATE
                repository.insert(HistoryRecord(equation = equation, result = resultText))
            }
        }
        _uiState.update { it.copy(recordToEdit = null, showEditDialog = false) }
    }

    fun dismissEdit() {
        _uiState.update { it.copy(recordToEdit = null, showEditDialog = false) }
    }

    // Método directo usado por SolverScreen y HomeScreen
    fun insert(equation: String, result: String? = null) {
        viewModelScope.launch {
            val finalResult = if (result != null) {
                result
            } else {
                val apiResult = apiRepository.evaluate(equation)
                if (apiResult is ApiState.Success) {
                    // Notificar cuando se calcula desde el Home o Historial
                    NotificationScheduler.triggerResolutionNotification(
                        getApplication(),
                        equation,
                        apiResult.result
                    )
                    apiResult.result
                } else "Error en cálculo"
            }
            repository.insert(HistoryRecord(equation = equation, result = finalResult))
        }
    }

    // ── DELETE ────────────────────────────────────────────────
    fun requestDelete(record: HistoryRecord) {
        _uiState.update { it.copy(recordToDelete = record) }
    }

    fun confirmDelete() {
        val record = _uiState.value.recordToDelete ?: return
        viewModelScope.launch {
            repository.delete(record)
        }
        _uiState.update { it.copy(recordToDelete = null, undoRecord = record) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(recordToDelete = null) }
    }

    fun undoDelete() {
        val record = _uiState.value.undoRecord ?: return
        viewModelScope.launch {
            repository.insert(record.copy(id = 0))
        }
        _uiState.update { it.copy(undoRecord = null) }
    }

    fun clearUndo() {
        _uiState.update { it.copy(undoRecord = null) }
    }
}
