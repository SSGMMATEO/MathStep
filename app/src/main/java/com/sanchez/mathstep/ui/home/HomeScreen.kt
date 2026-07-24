package com.sanchez.mathstep.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanchez.mathstep.ui.components.AppBottomBar
import com.sanchez.mathstep.ui.components.AppScreen

private val Primary = Color(0xFF3F51B5)
private val Secondary = Color(0xFF009688)
private val Gray = Color(0xFF757575)

/**
 * HomeScreen — panel de bienvenida con una sola llamada a la acción.
 * Se eliminó la caja "Nueva ecuación rápida": calculaba y guardaba
 * directo sin mostrar pasos, duplicando lo que ya hacía SolverScreen.
 * Ahora existe un solo camino de cálculo en toda la app.
 */
@Composable
fun HomeScreen(
    recentCount: Int,
    onNavigateToSolver: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    Scaffold(bottomBar = { AppBottomBar(current = AppScreen.HOME, onNavigate = onNavigate) }) { innerPadding ->
        Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = Color(0xFFFAFAFA)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("MathStep Free", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(Modifier.height(8.dp))
                Text("Tu asistente matemático inteligente", color = Gray, fontSize = 16.sp)

                if (recentCount > 0) {
                    Spacer(Modifier.height(12.dp))
                    Text("Tienes $recentCount ecuación(es) guardadas en tu historial", color = Secondary, fontSize = 14.sp)
                }

                Spacer(Modifier.height(40.dp))

                Button(
                    onClick = onNavigateToSolver,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Resolver ecuación", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}