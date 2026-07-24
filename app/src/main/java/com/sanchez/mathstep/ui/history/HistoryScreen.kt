package com.sanchez.mathstep.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import com.sanchez.mathstep.ui.components.AppBottomBar
import com.sanchez.mathstep.ui.components.AppScreen
import java.text.SimpleDateFormat
import java.util.*

private val Primary    = Color(0xFF3F51B5)
private val Secondary  = Color(0xFF009688)
private val Background = Color(0xFFFAFAFA)
private val ErrorColor = Color(0xFFB00020)
private val Gray       = Color(0xFF757575)

@Composable
fun HistoryScreen(
    onNavigate: (AppScreen) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.undoRecord) {
        if (uiState.undoRecord != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Ecuación eliminada",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete() else viewModel.clearUndo()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
        bottomBar = { AppBottomBar(current = AppScreen.HISTORY, onNavigate = onNavigate) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.requestCreate() },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ecuación")
            }
        }
    ) { innerPadding ->

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Surface(color = Primary) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Historial", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            if (uiState.records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay registros aún", color = Gray, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.records, key = { it.id }) { record ->
                        HistoryItem(
                            record = record,
                            onOpen = { viewModel.requestView(record) },
                            onEditClick = { viewModel.requestEdit(record) },
                            onDeleteClick = { viewModel.requestDelete(record) }
                        )
                    }
                }
            }
        }

        if (uiState.recordToDelete != null) {
            DeleteConfirmDialog(
                equation = uiState.recordToDelete!!.equation,
                onConfirm = { viewModel.confirmDelete() },
                onDismiss = { viewModel.dismissDelete() }
            )
        }

        uiState.dialog?.let { dialog ->
            EquationDialog(
                dialog = dialog,
                isSaving = uiState.isSaving,
                saveError = uiState.saveError,
                onConfirm = { equation ->
                    val existing = (dialog as? HistoryDialog.Edit)?.record
                    viewModel.saveEquation(equation, existing)
                },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        uiState.recordToView?.let { record ->
            StepsViewDialog(
                record = record,
                onEdit = {
                    viewModel.dismissView()
                    viewModel.requestEdit(record)
                },
                onDismiss = { viewModel.dismissView() }
            )
        }
    }
}

@Composable
private fun HistoryItem(
    record: HistoryRecord,
    onOpen: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(record.savedAt))

    ElevatedCard(modifier = Modifier.fillMaxWidth(), onClick = onOpen) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.equation, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Primary)
                Spacer(Modifier.height(4.dp))
                Text("Resultado: ${record.result}", fontSize = 14.sp, color = Secondary)
                Spacer(Modifier.height(4.dp))
                Text(dateStr, fontSize = 12.sp, color = Gray)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Primary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorColor)
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(equation: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar ecuación", fontWeight = FontWeight.SemiBold) },
        text = { Text("¿Eliminar \"$equation\" del historial? Esta acción se puede deshacer.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Eliminar", color = ErrorColor, fontWeight = FontWeight.SemiBold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Primary) } }
    )
}

/**
 * EquationDialog — reemplaza los antiguos CreateDialog y EditDialog
 * (código casi duplicado). El resultado NUNCA se edita a mano: se
 * recalcula al guardar.
 */
@Composable
private fun EquationDialog(
    dialog: HistoryDialog,
    isSaving: Boolean,
    saveError: String?,
    onConfirm: (equation: String) -> Unit,
    onDismiss: () -> Unit
) {
    val initial = (dialog as? HistoryDialog.Edit)?.record
    var equation by remember { mutableStateOf(initial?.equation ?: "") }
    var eqError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nueva ecuación" else "Editar ecuación", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = equation,
                    onValueChange = { equation = it; eqError = false },
                    label = { Text("Ecuación (ej: 2x + 3 = 7)") },
                    isError = eqError,
                    supportingText = { if (eqError) Text("Campo obligatorio", color = ErrorColor, fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )
                Text("El resultado se calcula automáticamente y no se puede editar directamente.", fontSize = 12.sp, color = Gray)
                saveError?.let { Text(it, color = ErrorColor, fontSize = 12.sp) }
                if (isSaving) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Calculando...", fontSize = 12.sp, color = Gray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { eqError = equation.isBlank(); if (!eqError) onConfirm(equation.trim()) },
                enabled = !isSaving
            ) { Text("Guardar", color = Primary, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Gray) } }
    )
}

/**
 * StepsViewDialog — modo lectura al tocar un ítem del historial
 * (Entregable 5: "Historial → toca ítem → Resultados en modo lectura").
 */
@Composable
private fun StepsViewDialog(record: HistoryRecord, onEdit: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(record.equation, fontWeight = FontWeight.SemiBold, color = Primary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val steps = record.stepsList()
                if (steps.isEmpty()) {
                    Text("No hay pasos guardados para este registro.", fontSize = 13.sp, color = Gray)
                } else {
                    steps.forEach { step -> Text("• $step", fontSize = 13.sp) }
                }
                Spacer(Modifier.height(4.dp))
                Text("Resultado final: ${record.result}", fontWeight = FontWeight.Bold, color = Secondary)
            }
        },
        confirmButton = { TextButton(onClick = onEdit) { Text("Editar", color = Primary) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = Gray) } }
    )
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Primary,
    unfocusedBorderColor = Gray,
    focusedLabelColor    = Primary,
    unfocusedLabelColor  = Gray,
    cursorColor          = Primary,
    errorBorderColor     = ErrorColor,
    errorLabelColor      = ErrorColor
)