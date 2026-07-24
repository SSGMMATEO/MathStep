package com.sanchez.mathstep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.ui.auth.AuthViewModel
import com.sanchez.mathstep.ui.auth.LoginScreen
import com.sanchez.mathstep.ui.auth.RegisterScreen
import com.sanchez.mathstep.ui.components.AppScreen
import com.sanchez.mathstep.ui.history.HistoryScreen
import com.sanchez.mathstep.ui.history.HistoryViewModel
import com.sanchez.mathstep.ui.home.HomeScreen
import com.sanchez.mathstep.ui.notifications.NotificationScheduler
import com.sanchez.mathstep.ui.settings.SettingsScreen
import com.sanchez.mathstep.ui.settings.SettingsViewModel
import com.sanchez.mathstep.ui.solver.SolverScreen
import com.sanchez.mathstep.ui.theme.MathStepTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) NotificationScheduler.scheduleDaily(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MathStepApp() }
        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED -> NotificationScheduler.scheduleDaily(this)
                else -> notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationScheduler.scheduleDaily(this)
        }
    }
}

/**
 * MathStepApp — navegación raíz. Antes el menú inferior de los
 * wireframes no existía en código; ahora Home/Historial/Config
 * comparten AppBottomBar y navegan de forma consistente.
 */
@Composable
fun MathStepApp() {
    val authViewModel: AuthViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val historyState by historyViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    var currentScreen by remember { mutableStateOf(if (authViewModel.isLoggedIn()) "home" else "login") }

    fun navigate(screen: AppScreen) { currentScreen = screen.name.lowercase() }

    MathStepTheme(darkThemeOverride = settingsState.darkMode) {
        Scaffold { innerPadding ->
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
                        recentCount = historyState.records.size,
                        onNavigateToSolver = { currentScreen = "solver" },
                        onNavigate = ::navigate
                    )
                    "history" -> HistoryScreen(viewModel = historyViewModel, onNavigate = ::navigate)
                    "solver" -> SolverScreen(
                        historyViewModel = historyViewModel,
                        autoSave = settingsState.autoSave,
                        onNavigateBack = { currentScreen = "home" },
                        onNavigate = ::navigate
                    )
                    "settings" -> SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigate = ::navigate,
                        onLogout = { authViewModel.logout(); currentScreen = "login" }
                    )
                }
            }
        }
    }
}