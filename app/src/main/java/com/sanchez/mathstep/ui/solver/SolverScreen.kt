package com.sanchez.mathstep.ui.solver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.data.remote.ApiState
import com.sanchez.mathstep.ui.history.HistoryViewModel
import kotlinx.coroutines.launch

private val Primary    = Color(0xFF3F51B5)
private val Secondary  = Color(0xFF009688)
private val Background = Color(0xFFFAFAFA)
private val ErrorColor = Color(0xFFB00020)
private val Gray       = Color(0xFF757575)

@Composable
fun SolverScreen(
    onNavigateBack: () -> Unit,
    viewModel: SolverViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    val apiState by viewModel.apiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var expression by remember { mutableStateOf("") }
    var expressionError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
        topBar = {
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
                        text = "Resolver Ecuación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ingresa una ecuación completa (ej: 5x + 2 = 10) o una expresión para obtener el resultado y los pasos.",
                fontSize = 14.sp,
                color = Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = expression,
                onValueChange = {
                    expression = it
                    expressionError = null
                    viewModel.resetState()
                },
                label = { Text("Ecuación o expresión (ej: 5x + 2 = 12)") },
                isError = expressionError != null,
                supportingText = {
                    expressionError?.let {
                        Text(it, color = ErrorColor, fontSize = 12.sp)
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Primary,
                    unfocusedBorderColor = Gray,
                    focusedLabelColor    = Primary,
                    cursorColor          = Primary,
                    errorBorderColor     = ErrorColor
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (expression.isBlank()) {
                        expressionError = "Ingresa una ecuación o expresión"
                    } else {
                        focusManager.clearFocus()
                        viewModel.verify(expression.trim())
                    }
                },
                enabled = apiState !is ApiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor   = Color.White,
                    disabledContainerColor = Primary.copy(alpha = 0.6f)
                )
            ) {
                if (apiState is ApiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Resolver", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (val state = apiState) {
                is ApiState.Idle -> Unit
                is ApiState.Loading -> {
                    CircularProgressIndicator(color = Primary)
                }
                is ApiState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = Color(0xFFE8F5E9)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Resultado verificado",
                                        fontSize = 13.sp,
                                        color = Secondary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.result,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Secondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val isEquation = state.result.startsWith("x =")
                                    Text(
                                        text = if (isEquation) "Resolución local" else "Fuente: api.mathjs.org",
                                        fontSize = 11.sp,
                                        color = Gray
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        historyViewModel.insert(expression, state.result)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Guardado en el historial")
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Secondary,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = "Guardar")
                                }
                            }
                        }

                        if (state.steps.isNotEmpty()) {
                            Text(
                                text = "Pasos de resolución",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            state.steps.forEachIndexed { index, step ->
                                ElevatedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = step,
                                        modifier = Modifier.padding(16.dp),
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                is ApiState.Error -> {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Error de conexión",
                                fontSize = 13.sp,
                                color = ErrorColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.message,
                                fontSize = 14.sp,
                                color = ErrorColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "El solver local sigue disponible sin conexión.",
                                fontSize = 12.sp,
                                color = Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
