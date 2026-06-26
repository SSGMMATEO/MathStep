package com.sanchez.mathstep.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ── Colores del prototipo Figma ───────────────────────────────────
private val Primary = Color(0xFF3F51B5)      // azul índigo
private val Secondary = Color(0xFF009688)    // verde teal
private val Background = Color(0xFFFAFAFA)   // blanco roto
private val ErrorColor = Color(0xFFB00020)   // rojo error
private val Gray = Color(0xFF757575)         // gris subtítulo

/**
 * LoginScreen — pantalla de inicio de sesión en Compose.
 *
 * Parámetros de navegación:
 *   onLoginSuccess: lambda que se llama cuando el login es exitoso.
 *     La Activity o NavHost decide a dónde navegar.
 *   onNavigateToRegister: lambda para ir a RegisterScreen.
 *
 * viewModel(): crea o recupera el ViewModel ligado a esta pantalla.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // ── Estado local de los campos ────────────────────────────
    // remember: el valor sobrevive a recomposiciones (redibujos).
    // mutableStateOf: cuando cambia, Compose redibuja lo que lo usa.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Errores de validación por campo ───────────────────────
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // ── Estado del ViewModel ──────────────────────────────────
    // collectAsState(): observa el StateFlow y redibuja cuando cambia.
    val uiState by viewModel.uiState.collectAsState()

    // ── FocusManager: mueve el cursor entre campos ────────────
    val focusManager = LocalFocusManager.current

    // ── Efecto: navegar cuando el login es exitoso ────────────
    // LaunchedEffect: se ejecuta cuando cambia la clave (uiState.isLoggedIn).
    // Equivalente al observer de LiveData en la versión XML.
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    // ── Función de validación (tabla del entregable) ──────────
    fun validateAndLogin() {
        emailError = null
        passwordError = null
        var isValid = true

        when {
            email.isBlank() -> {
                emailError = "El correo es obligatorio"
                isValid = false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Ingresa un correo válido"
                isValid = false
            }
        }
        when {
            password.isBlank() -> {
                passwordError = "La contraseña es obligatoria"
                isValid = false
            }
            password.length < 6 -> {
                passwordError = "La contraseña debe tener al menos 6 caracteres"
                isValid = false
            }
        }
        if (isValid) {
            focusManager.clearFocus()
            viewModel.login(email.trim(), password)
        }
    }

    // ── UI ────────────────────────────────────────────────────
    // Surface: contenedor con color de fondo.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        // verticalScroll: el contenido hace scroll si el teclado sube
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── [1] ÍCONO Y NOMBRE ────────────────────────────
            // Σ como texto grande simula el logo del prototipo
            Text(
                text = "Σ",
                fontSize = 64.sp,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MathStep Free",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
            Text(
                text = "Ingresa para continuar",
                fontSize = 14.sp,
                color = Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── [2] CAMPO DE CORREO ───────────────────────────
            // OutlinedTextField: equivalente Compose del OutlinedTextField de Figma.
            // isError: pone el borde y el label en rojo cuando hay error.
            // supportingText: texto debajo del campo (mensaje de error).
            // leadingIcon: ícono a la izquierda.
            // keyboardOptions: configura el tipo de teclado e ImeAction.
            // keyboardActions: qué hace el botón Enter del teclado.
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null   // limpia error al escribir
                },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Primary)
                },
                isError = emailError != null,
                supportingText = {
                    emailError?.let { Text(it, color = ErrorColor, fontSize = 12.sp) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next   // botón Enter mueve al siguiente campo
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = authFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── [3] CAMPO DE CONTRASEÑA ───────────────────────
            // visualTransformation: oculta los caracteres con puntos.
            // trailingIcon: ícono ojo para mostrar/ocultar contraseña.
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Primary)
                },
                trailingIcon = {
                    // Cambia entre ojo abierto y cerrado según passwordVisible
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                            tint = Primary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { Text(it, color = ErrorColor, fontSize = 12.sp) }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { validateAndLogin() }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = authFieldColors()
            )

            // Error del ViewModel (error de BD, no de validación)
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = ErrorColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── [4] BOTÓN INICIAR SESIÓN ──────────────────────
            // enabled = !isLoading: deshabilita el botón mientras carga.
            Button(
                onClick = { validateAndLogin() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Color.White,
                    disabledContainerColor = Primary.copy(alpha = 0.6f)
                )
            ) {
                if (uiState.isLoading) {
                    // Spinner dentro del botón mientras procesa
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── [5] ENLACE A REGISTRO ─────────────────────────
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = Primary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * authFieldColors(): colores compartidos para todos los campos de auth.
 * Extrae la configuración repetida para no duplicar código.
 * focusedBorderColor: borde azul cuando el campo está activo.
 * unfocusedBorderColor: borde gris cuando no tiene foco.
 * focusedLabelColor: label azul cuando el campo está activo.
 */
@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF3F51B5),
    unfocusedBorderColor = Color(0xFF757575),
    focusedLabelColor = Color(0xFF3F51B5),
    unfocusedLabelColor = Color(0xFF757575),
    cursorColor = Color(0xFF3F51B5),
    errorBorderColor = Color(0xFFB00020),
    errorLabelColor = Color(0xFFB00020)
)
