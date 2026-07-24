package com.sanchez.mathstep.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * LoginScreenUITest — prueba de interfaz sobre el Composable LoginScreen.
 * A diferencia de Espresso clásico (que busca por R.id.xxx en un XML),
 * aquí Compose Testing busca elementos por el TEXTO visible en pantalla,
 * porque un Composable no genera IDs de recurso Android.
 *
 * createComposeRule(): monta el Composable en una Activity vacía y
 * mínima que crea internamente, sin necesitar tu MainActivity completa
 * ni depender del flujo de navegación/sesión guardada.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class LoginScreenUITest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginConCamposVacios_muestraMensajeError() {
        // ARRANGE — monta la pantalla de login, campos vacíos por defecto
        composeRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {}
            )
        }

        // ACT — toca el botón sin escribir nada
        composeRule.onNodeWithText("Iniciar sesión").performClick()

        // ASSERT — el mensaje de validación debe aparecer en pantalla
        composeRule.onNodeWithText("El correo es obligatorio").assertIsDisplayed()
    }
}