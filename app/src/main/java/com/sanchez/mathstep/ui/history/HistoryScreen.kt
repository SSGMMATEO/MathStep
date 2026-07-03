package com.sanchez.mathstep.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val Primary    = Color(0xFF3F51B5)
private val Secondary  = Color(0xFF009688)
private val Background = Color(0xFFFAFAFA)
private val ErrorColor = Color(0xFFB00020)
private val Gray       = Color(0xFF757575)

/**
 * HistoryScreen — CRUD completo con confirmación y Snackbar de Undo.
 *
 * snackbarHostState: canal entre la lógica y el Snackbar visual.
 * LaunchedEffect(uiState.undoRecord): se ejecuta cada vez que hay
 *   un ítem eliminado disponible para deshacer, muestra el Snackbar
 *   y espera la acción del usuario.
 */
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = coroutineScope()

    // Snackbar de Deshacer — se activa cuando undoRecord tiene valor
    LaunchedEffect(uiState.undoRecord) {
        if (uiState.undoRecord != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Ecuación eliminada",
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearUndo()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Encabezado ────────────────────────────────────
            Surface(color = Primary) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Historial",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // ── Lista o estado vacío ──────────────────────────
            if (uiState.records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                            onEditClick  = { viewModel.requestEdit(record) },
                            onDeleteClick = { viewModel.requestDelete(record) }
                        )
                    }
                }
            }
        }

        // ── Diálogo de confirmación de eliminación ────────────
        if (uiState.recordToDelete != null) {
            DeleteConfirmDialog(
                equation = uiState.recordToDelete!!.equation,
                onConfirm = { viewModel.confirmDelete() },
                onDismiss = { viewModel.dismissDelete() }
            )
        }

        // ── Diálogo de edición ────────────────────
        if (uiState.showEditDialog) {
            EditDialog(
                initial = uiState.recordToEdit,
                onConfirm = { eq -> viewModel.confirmEdit(eq) },
                onDismiss = { viewModel.dismissEdit() }
            )
        }

        // ── Diálogo de creación ────────────────────
        if (uiState.showCreateDialog) {
            CreateDialog(
                onConfirm = { eq ->
                    viewModel.insert(eq)
                    viewModel.dismissCreate()
                },
                onDismiss = { viewModel.dismissCreate() }
            )
        }
    }
}

// ── Composable auxiliar para el scope de coroutine ────────────────
@Composable
private fun coroutineScope() = rememberCoroutineScope()

// ─────────────────────────────────────────────────────────────────
// HistoryItem
// ─────────────────────────────────────────────────────────────────
/**
 * Equivalente al ViewHolder del Adapter de RecyclerView.
 * Dos acciones: editar (lápiz) y eliminar (papelera).
 */
@Composable
private fun HistoryItem(
    record: HistoryRecord,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(record.savedAt))

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.equation,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Resultado: ${record.result}",
                    fontSize = 14.sp,
                    color = Secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dateStr, fontSize = 12.sp, color = Gray)
            }
            // Botón editar
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Primary)
            }
            // Botón eliminar — abre diálogo de confirmación
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = ErrorColor)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// DeleteConfirmDialog — AlertDialog de Material Design 3
// ─────────────────────────────────────────────────────────────────
/**
 * Patrón UX: el AlertDialog es el estándar de Material Design para
 * acciones destructivas irreversibles. Le da al usuario una segunda
 * oportunidad antes de perder datos.
 */
@Composable
private fun DeleteConfirmDialog(
    equation: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Eliminar ecuación", fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text("¿Eliminar \"$equation\" del historial? Esta acción se puede deshacer.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = ErrorColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Primary)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────
// EditDialog — formulario de edición reutilizado
// ─────────────────────────────────────────────────────────────────
/**
 * Patrón UX: reutilizar el mismo formulario para Create y Update
 * evita que el usuario aprenda dos interfaces distintas.
 * El diálogo recibe los valores iniciales del ítem a editar.
 */
@Composable
private fun EditDialog(
    initial: HistoryRecord?,
    onConfirm: (equation: String) -> Unit,
    onDismiss: () -> Unit
) {
    var equation by remember { mutableStateOf(initial?.equation ?: "") }
    var eqError  by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initial == null) "Nueva ecuación" else "Editar ecuación",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = equation,
                    onValueChange = { equation = it; eqError = false },
                    label = { Text("Ecuación") },
                    isError = eqError,
                    supportingText = {
                        if (eqError) Text("Campo obligatorio", color = ErrorColor, fontSize = 12.sp)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )
                if (initial != null) {
                    Text(
                        text = "El resultado se recalculará automáticamente.",
                        fontSize = 12.sp,
                        color = Gray
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                eqError  = equation.isBlank()
                if (!eqError) onConfirm(equation.trim())
            }) {
                Text("Guardar", color = Primary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gray)
            }
        }
    )
}

@Composable
private fun CreateDialog(
    onConfirm: (equation: String) -> Unit,
    onDismiss: () -> Unit
) {
    var equation by remember { mutableStateOf("") }
    var eqError  by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nueva ecuación", fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = equation,
                    onValueChange = { equation = it; eqError = false },
                    label = { Text("Ecuación (ej: 2x + 3 = 7)") },
                    isError = eqError,
                    supportingText = {
                        if (eqError) Text("Campo obligatorio", color = ErrorColor, fontSize = 12.sp)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = editFieldColors()
                )
                Text(
                    text = "El resultado se calculará usando la API.",
                    fontSize = 12.sp,
                    color = Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                eqError  = equation.isBlank()
                if (!eqError) onConfirm(equation.trim())
            }) {
                Text("Guardar", color = Primary, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gray)
            }
        }
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