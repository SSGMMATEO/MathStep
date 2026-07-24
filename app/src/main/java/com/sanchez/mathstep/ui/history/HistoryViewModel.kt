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
 * HistoryViewModel — CRUD unificado.
 * saveEquation() es la ÚNICA función que calcula y persiste una ecuación,
 * usada tanto por crear como por editar. Antes insert() y confirmEdit()
 * tenían el mismo código de cálculo duplicado dos veces.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository by lazy {
        val dao = AppDatabase.getInstance(application).historyDao()
        HistoryRepository(dao)
    }

    private val apiRepository = MathApiRepository(application)

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        repository.allRecords
            .onEach { list -> _uiState.update { it.copy(records = list) } }
            .launchIn(viewModelScope)
    }

    fun requestCreate() = _uiState.update { it.copy(dialog = HistoryDialog.Create) }
    fun requestEdit(record: HistoryRecord) = _uiState.update { it.copy(dialog = HistoryDialog.Edit(record)) }
    fun dismissDialog() = _uiState.update { it.copy(dialog = null, saveError = null) }

    fun requestView(record: HistoryRecord) = _uiState.update { it.copy(recordToView = record) }
    fun dismissView() = _uiState.update { it.copy(recordToView = null) }

    /**
     * Único punto de cálculo + guardado del CRUD manual. La ecuación
     * siempre es editable; el resultado se recalcula automáticamente
     * y NUNCA se edita a mano.
     */
    fun saveEquation(equation: String, existing: HistoryRecord? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }

            when (val apiResult = apiRepository.evaluate(equation)) {
                is ApiState.Success -> {
                    val stepsJoined = HistoryRecord.joinSteps(apiResult.steps)
                    if (existing != null) {
                        repository.update(existing.copy(equation = equation, result = apiResult.result, steps = stepsJoined))
                    } else {
                        repository.insert(HistoryRecord(equation = equation, result = apiResult.result, steps = stepsJoined))
                        // Solo se notifica al crear desde el historial directamente;
                        // si viene del Solver, saveFromSolver() no vuelve a notificar
                        // (el Solver ya notificó al calcular).
                        NotificationScheduler.triggerResolutionNotification(getApplication(), equation, apiResult.result)
                    }
                    _uiState.update { it.copy(isSaving = false, dialog = null) }
                }
                is ApiState.Error -> {
                    _uiState.update { it.copy(isSaving = false, saveError = apiResult.message) }
                }
                else -> Unit
            }
        }
    }

    /**
     * Guarda un resultado que YA fue calculado en SolverScreen (con sus
     * pasos), sin volver a llamar la API ni disparar otra notificación.
     */
    fun saveFromSolver(equation: String, result: String, steps: List<String>) {
        viewModelScope.launch {
            repository.insert(HistoryRecord(equation = equation, result = result, steps = HistoryRecord.joinSteps(steps)))
        }
    }

    fun requestDelete(record: HistoryRecord) = _uiState.update { it.copy(recordToDelete = record) }
    fun dismissDelete() = _uiState.update { it.copy(recordToDelete = null) }

    fun confirmDelete() {
        val record = _uiState.value.recordToDelete ?: return
        viewModelScope.launch { repository.delete(record) }
        _uiState.update { it.copy(recordToDelete = null, undoRecord = record) }
    }

    fun undoDelete() {
        val record = _uiState.value.undoRecord ?: return
        viewModelScope.launch { repository.insert(record.copy(id = 0)) }
        _uiState.update { it.copy(undoRecord = null) }
    }

    fun clearUndo() = _uiState.update { it.copy(undoRecord = null) }
}