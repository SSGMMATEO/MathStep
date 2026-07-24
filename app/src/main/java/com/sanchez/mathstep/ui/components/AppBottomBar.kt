package com.sanchez.mathstep.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * AppBottomBar — menú inferior compartido por Home/Historial/Config.
 * Antes esta barra solo existía en los wireframes; nunca en código.
 */
enum class AppScreen { HOME, HISTORY, SETTINGS }

@Composable
fun AppBottomBar(current: AppScreen, onNavigate: (AppScreen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = current == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = current == AppScreen.HISTORY,
            onClick = { onNavigate(AppScreen.HISTORY) },
            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
            label = { Text("Historial") }
        )
        NavigationBarItem(
            selected = current == AppScreen.SETTINGS,
            onClick = { onNavigate(AppScreen.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
            label = { Text("Config") }
        )
    }
}