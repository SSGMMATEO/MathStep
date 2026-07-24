# Manual Técnico — MathStep Free v1.0

## 1. Descripción del sistema
MathStep Free resuelve ecuaciones lineales y expresiones aritméticas paso a paso,
funcionando completamente offline para los casos más comunes. El usuario objetivo
es un estudiante que necesita verificar sus ejercicios de álgebra básica sin depender
de conexión a internet.

## 2. Arquitectura de la aplicación
Patrón MVVM (Model-View-ViewModel) con Jetpack Compose.
- **UI (Compose):** LoginScreen, RegisterScreen, HomeScreen, SolverScreen, HistoryScreen, SettingsScreen.
- **Lógica (ViewModel):** AuthViewModel, HistoryViewModel, SolverViewModel, SettingsViewModel — exponen StateFlow, consumido con collectAsState().
- **Datos (Repository + Room):** AuthRepository, HistoryRepository, MathApiRepository → DAO (UserDao, HistoryDao) → Room (AppDatabase).

## 3. Modelo de datos
Entidades: `User` (id, username, email, passwordHash) y `HistoryRecord` (id, equation,
result, steps, savedAt). Sin relaciones entre ambas tablas en esta versión.

## 4. Tecnologías y librerías
- Kotlin + Jetpack Compose + Material 3
- Room 2.7.0-alpha11 (persistencia local)
- Retrofit 2.11.0 + ScalarsConverterFactory (API MathJS, texto plano)
- WorkManager 2.9.0 (notificaciones diarias y de resolución)
- Coil 2.7.0 (carga de gráficas desde QuickChart.io)

## 5. Instrucciones para compilar
Requisitos: Android Studio (versión estable más reciente), JDK 11, compileSdk 36, minSdk 24.
1. Clonar: `git clone https://github.com/SSGMMATEO/MathStep.git`
2. Abrir en Android Studio, esperar sincronización de Gradle.
3. Run ▶ sobre un emulador o dispositivo con Android 7.0 (API 24) o superior.
No requiere API keys ni google-services.json.

## 6. Estructura del repositorio
- `app/src/main/java/.../data/` — repositorios, Room, Retrofit
- `app/src/main/java/.../ui/` — pantallas Compose por feature (auth, home, solver, history, settings)
- `app/src/test/` — pruebas unitarias (JVM)
- `app/src/androidTest/` — pruebas instrumentadas (Room, Compose UI)

## 7. Historial de versiones
- **v1.0** — julio 2026 — MVP completo: autenticación, resolución de ecuaciones
  lineales offline, historial CRUD con deshacer, notificaciones, gráfica de rectas.
