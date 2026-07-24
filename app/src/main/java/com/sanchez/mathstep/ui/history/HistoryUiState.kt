package com.sanchez.mathstep.ui.history

import com.sanchez.mathstep.data.local.entity.HistoryRecord

sealed class HistoryDialog {
    data object Create : HistoryDialog()
    data class Edit(val record: HistoryRecord) : HistoryDialog()
}

data class HistoryUiState(
    val records: List<HistoryRecord> = emptyList(),
    val recordToDelete: HistoryRecord? = null,
    val dialog: HistoryDialog? = null,
    val recordToView: HistoryRecord? = null,
    val undoRecord: HistoryRecord? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null
)