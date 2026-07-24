package com.sanchez.mathstep.ui.solver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanchez.mathstep.data.remote.ApiState
import com.sanchez.mathstep.data.repository.MathApiRepository
import com.sanchez.mathstep.ui.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MathApiRepository(application)

    private val _apiState = MutableStateFlow<ApiState>(ApiState.Idle)
    val apiState: StateFlow<ApiState> = _apiState.asStateFlow()

    fun verify(expression: String) {
        if (expression.isBlank()) return
        viewModelScope.launch {
            _apiState.value = ApiState.Loading
            val resultState = repository.evaluate(expression)
            _apiState.value = resultState

            if (resultState is ApiState.Success) {
                NotificationScheduler.triggerResolutionNotification(getApplication(), expression, resultState.result)
            }
        }
    }

    fun resetState() { _apiState.value = ApiState.Idle }
}