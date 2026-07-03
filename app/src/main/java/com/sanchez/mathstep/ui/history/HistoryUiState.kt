package com.sanchez.mathstep.ui.history

import com.sanchez.mathstep.data.local.entity.HistoryRecord

/**
 * Estado completo de la pantalla de historial.
 * recordToDelete: el ítem pendiente de confirmación para borrar.
 * recordToEdit: el ítem que se está editando (null = modo creación).
 * showEditDialog: controla si el diálogo de edición está visible.
 * undoRecord: el último ítem eliminado, disponible para deshacer.
 */
data class HistoryUiState(
    val records: List<HistoryRecord> = emptyList(),
    val recordToDelete: HistoryRecord? = null,
    val recordToEdit: HistoryRecord? = null,
    val showEditDialog: Boolean = false,
    val showCreateDialog: Boolean = false,
    val undoRecord: HistoryRecord? = null
)