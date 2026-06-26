package com.sanchez.mathstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.ui.auth.AuthViewModel
import com.sanchez.mathstep.ui.auth.LoginScreen
import com.sanchez.mathstep.ui.auth.RegisterScreen
import com.sanchez.mathstep.ui.theme.MathStepTheme

/**
 * MainActivity — único Activity de la app (arquitectura single-Activity).
 * En lugar de múltiples Activities, Compose usa una sola Activity
 * con múltiples "pantallas" (Composables) que se intercambian.
 *
 * currentScreen: variable de estado que controla qué pantalla se muestra.
 * when(currentScreen): equivalente al sistema de navegación, simple y directo.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathStepTheme {
                MathStepApp()
            }
        }
    }
}

/**
 * MathStepApp — raíz de la navegación de la app.
 *
 * currentScreen: estado que decide qué pantalla mostrar.
 *   "login"    → LoginScreen
 *   "register" → RegisterScreen
 *   "home"     → HomeScreen (tu pantalla principal existente)
 *
 * El ViewModel se crea aquí y se comparte con LoginScreen y RegisterScreen
 * para que ambas usen el mismo estado de autenticación.
 *
 * Chequeo de sesión: si isLoggedIn() es true al abrir la app,
 * arranca directamente en "home" sin pasar por login.
 */
@Composable
fun MathStepApp() {
    val authViewModel: AuthViewModel = viewModel()

    // Decide la pantalla inicial según si hay sesión activa
    var currentScreen by remember {
        mutableStateOf(
            if (authViewModel.isLoggedIn()) "home" else "login"
        )
    }

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
        "home" -> {
            // Aquí va tu HomeScreen existente o la pantalla principal de MathStep.
            // Por ahora un placeholder que puedes reemplazar:
            HomeScreen(onLogout = {
                authViewModel.logout()
                currentScreen = "login"
            })
        }
    }
}

/**
 * HomeScreen — placeholder de la pantalla principal.
 * REEMPLAZA este Composable con tu pantalla de ingreso de ecuaciones.
 * Mantén el parámetro onLogout para el botón de cerrar sesión.
 */
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color(0xFFFAFAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = "MathStep Free",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF3F51B5)
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            androidx.compose.material3.Text(
                text = "Aquí va tu pantalla principal de ecuaciones",
                color = androidx.compose.ui.graphics.Color(0xFF757575)
            )
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            androidx.compose.material3.TextButton(onClick = onLogout) {
                androidx.compose.material3.Text(
                    "Cerrar sesión",
                    color = androidx.compose.ui.graphics.Color(0xFF3F51B5)
                )
            }
        }
    }
}
