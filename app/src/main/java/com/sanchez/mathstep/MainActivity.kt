package com.sanchez.mathstep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.ui.auth.AuthViewModel
import com.sanchez.mathstep.ui.auth.LoginScreen
import com.sanchez.mathstep.ui.auth.RegisterScreen
import com.sanchez.mathstep.ui.history.HistoryScreen
import com.sanchez.mathstep.ui.history.HistoryViewModel
import com.sanchez.mathstep.ui.notifications.NotificationScheduler
import com.sanchez.mathstep.ui.solver.SolverScreen
import com.sanchez.mathstep.ui.theme.MathStepTheme
import kotlinx.coroutines.launch

/**
 * MainActivity — único Activity de la app (arquitectura single-Activity).
 */
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) NotificationScheduler.scheduleDaily(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathStepTheme {
                MathStepApp()
            }
        }
        requestNotificationPermissionIfNeeded()
    }

    /**
     * En Android 13+ (API 33) el permiso de notificaciones debe pedirse
     * en tiempo de ejecución. En versiones anteriores se otorga automáticamente.
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    NotificationScheduler.scheduleDaily(this)
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 o menor: no necesita pedir permiso en runtime
            NotificationScheduler.scheduleDaily(this)
        }
    }
}

@Composable
fun MathStepApp() {
    val authViewModel: AuthViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember {
        mutableStateOf(
            if (authViewModel.isLoggedIn()) "home" else "login"
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "login" -> LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { currentScreen = "home" },
                    onNavigateToRegister = { currentScreen = "register" }
                )

                "register" -> RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { currentScreen = "login" },
                    onNavigateBack = { currentScreen = "login" }
                )

                "home" -> HomeScreen(
                    historyViewModel = historyViewModel,
                    snackbarHostState = snackbarHostState,
                    onLogout = {
                        authViewModel.logout()
                        currentScreen = "login"
                    },
                    onNavigateToHistory = {
                        currentScreen = "history"
                    },
                    onNavigateToSolver = {
                        currentScreen = "solver"
                    }
                )

                "history" -> HistoryScreen(
                    viewModel = historyViewModel,
                    onNavigateBack = {
                        currentScreen = "home"
                    }
                )

                "solver" -> SolverScreen(
                    historyViewModel = historyViewModel,
                    onNavigateBack = { currentScreen = "home" }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    historyViewModel: HistoryViewModel,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var equation by remember { mutableStateOf("") }
    var isCalculating by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MathStep Free",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu asistente matemático inteligente",
                color = Color(0xFF757575),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── NUEVA ECUACIÓN RÁPIDA ──────────────────────────
            OutlinedTextField(
                value = equation,
                onValueChange = { equation = it },
                label = { Text("Nueva ecuación rápida") },
                placeholder = { Text("ej: 5 * 25") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    focusedLabelColor = Color(0xFF3F51B5),
                    cursorColor = Color(0xFF3F51B5)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (equation.isNotBlank()) {
                        isCalculating = true
                        focusManager.clearFocus()
                        scope.launch {
                            historyViewModel.insert(equation.trim())
                            snackbarHostState.showSnackbar("Ecuación enviada al historial")
                            equation = ""
                            isCalculating = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                enabled = !isCalculating
            ) {
                if (isCalculating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Calcular y Guardar")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── NAVEGACIÓN ────────────────────────────────────
            TextButton(onClick = onNavigateToHistory) {
                Text("Ver historial completo", color = Color(0xFF3F51B5), fontWeight = FontWeight.SemiBold)
            }

            TextButton(onClick = onNavigateToSolver) {
                Text("Verificar con API detallada", color = Color(0xFF3F51B5))
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onLogout) {
                Text("Cerrar sesión", color = Color(0xFF757575))
            }
        }
    }
}
