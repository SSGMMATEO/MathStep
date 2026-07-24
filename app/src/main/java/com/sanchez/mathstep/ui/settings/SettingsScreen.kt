package com.sanchez.mathstep.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sanchez.mathstep.ui.components.AppBottomBar
import com.sanchez.mathstep.ui.components.AppScreen

private val Primary = Color(0xFF3F51B5)
private val Gray = Color(0xFF757575)
private val ErrorColor = Color(0xFFB00020)

@Composable
fun SettingsScreen(
    onNavigate: (AppScreen) -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(bottomBar = { AppBottomBar(current = AppScreen.SETTINGS, onNavigate = onNavigate) }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp)) {
            Text("Configuración", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(24.dp))

            Text("APARIENCIA", fontSize = 12.sp, color = Gray, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo oscuro")
                Switch(checked = uiState.darkMode, onCheckedChange = viewModel::setDarkMode)
            }

            HorizontalDivider()

            Text(
                "HISTORIAL", fontSize = 12.sp, color = Gray, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Guardar automáticamente al resolver")
                Switch(checked = uiState.autoSave, onCheckedChange = viewModel::setAutoSave)
            }

            HorizontalDivider()

            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión", color = ErrorColor)
            }

            Spacer(Modifier.height(16.dp))
            Text("MathStep Free · v1.0.0", fontSize = 12.sp, color = Gray)
        }
    }
}