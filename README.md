# MathStep Free 📐

> Aplicación Android para resolver ecuaciones algebraicas lineales paso a paso

---

## 🧩 Problema que resuelve

Muchos estudiantes de secundaria obtienen el resultado de una ecuación algebraica sin comprender el procedimiento. Las calculadoras tradicionales entregan solo la respuesta final, lo que impide el aprendizaje real del proceso matemático. **MathStep Free** cierra esa brecha mostrando cada paso intermedio de la resolución, explicado en lenguaje claro y accesible.

---

## 🎯 Objetivo de la aplicación

Permitir que estudiantes de secundaria resuelvan ecuaciones algebraicas lineales de forma autónoma, comprendiendo el procedimiento completo paso a paso, sin necesidad de conexión a internet.

---

## 👤 Historias de usuario del MVP

| ID | Título | Historia |
|----|--------|----------|
| HU-01 | Resolver ecuaciones lineales | Como **estudiante de secundaria**, quiero ingresar una ecuación algebraica lineal y resolverla, para comprender el procedimiento matemático paso a paso. |
| HU-02 | Visualizar pasos detallados | Como **usuario de la aplicación**, quiero ver cada paso intermedio de la resolución, para entender cómo se obtiene el resultado final. |
| HU-03 | Validar ecuaciones ingresadas | Como **usuario principiante**, quiero que la aplicación detecte errores en ecuaciones mal escritas, para evitar resultados incorrectos o confusión. |
| HU-04 | Guardar historial de cálculos | Como **estudiante universitario**, quiero guardar ecuaciones resueltas anteriormente, para revisarlas posteriormente sin volver a escribirlas. *(Opcional en v1.0)* |
| HU-05 | Cambiar apariencia visual | Como **usuario frecuente**, quiero activar un modo oscuro en la aplicación, para utilizarla cómodamente en ambientes con poca iluminación. *(Fuera del MVP)* |

---

## 🛠️ Tecnología usada

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| Plataforma | Android (SDK mínimo: API 24) |
| IDE | Android Studio |
| Arquitectura | MVVM (Model - View - ViewModel) |
| Base de datos | Room (SQLite) |
| Preferencias | SharedPreferences |
| Motor matemático | Symja / exp4j |
| Sistema de diseño | Material Design 3 |
| Navegación | Navigation Component + BottomNavigationBar |

---

## 📦 Instrucciones de instalación

### Requisitos previos
- Android Studio Hedgehog o superior
- JDK 17
- Android SDK API 24+

### Pasos

1. **Clona el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/mathstep-free.git
   cd mathstep-free
   ```

2. **Abre el proyecto en Android Studio**
   - Selecciona `File > Open` y navega a la carpeta del proyecto.

3. **Sincroniza las dependencias de Gradle**
   - Android Studio lo hará automáticamente. Si no, ejecuta:
   ```bash
   ./gradlew build
   ```

4. **Ejecuta la aplicación**
   - Conecta un dispositivo físico o inicia un emulador Android (API 24+).
   - Presiona el botón ▶ **Run** en Android Studio.

> **Nota:** La aplicación funciona completamente offline. No requiere claves de API ni configuración adicional.

---

## 📸 Capturas de pantalla

| Pantalla principal | Resolución paso a paso | Historial |
|---|---|---|
| ![Home](screenshots/home.png) | ![Results](screenshots/results.png) | ![History](screenshots/history.png) |

> 📁 *Coloca las capturas en la carpeta `/screenshots` en la raíz del proyecto.*

---

## 🗂️ Arquitectura del proyecto

```
app/
├── ui/                  # Capa de presentación (Activities, Fragments, ViewModels)
│   ├── home/
│   ├── results/
│   ├── history/
│   └── settings/
├── logic/               # Capa de lógica de negocio
│   ├── EquationValidator.kt
│   ├── StepSolver.kt
│   └── HistoryManager.kt
└── data/                # Capa de datos
    ├── db/              # Room Database (HistoryDAO, Entities)
    └── prefs/           # SharedPreferences
```

---

## 📊 Estado actual del proyecto

| Entregable | Estado |
|------------|--------|
| Historias de usuario y MVP | ✅ Completado |
| Bocetos de baja fidelidad (Wireframes) | ✅ Completado |
| Arquitectura y modelo de datos | ✅ Completado |
| Prototipo de alta fidelidad (Figma) | ✅ Completado |
| Implementación en Android Studio | 🔄 En desarrollo |
| Pruebas de usabilidad | 🔄 En curso |

---

## 👨‍💻 Autor

**Sánchez Mier Mateo Sebastián**  
Universidad Central del Ecuador — Facultad de Ciencias e Ingeniería  
Metodología de la Investigación · 2026

---

## 📄 Licencia

Este proyecto es de uso académico. Distribuido bajo licencia Open Source.

## 📄 Funcionalidades implementadas

### Autenticación local con Room Database
- Registro de usuario con nombre, correo y contraseña — la contraseña se almacena como hash SHA-256, nunca en texto plano
- Inicio de sesión verificando que el hash de la contraseña ingresada coincida con el hash guardado en Room
- Sesión persistente con SharedPreferences — al reabrir la app no vuelve a pedir login si ya había una sesión activa
- Cierre de sesión disponible desde la pantalla principal

### Validaciones de formulario
- Correo obligatorio con verificación de formato mediante `Patterns.EMAIL_ADDRESS` de Android
- Contraseña obligatoria con mínimo de 6 caracteres
- Confirmación de contraseña en el registro (ambas deben coincidir)
- Nombre de usuario obligatorio con mínimo de 2 caracteres
- Mensajes de error específicos por campo visibles debajo de cada campo

### Navegación
- Al abrir la app detecta automáticamente si hay sesión activa y salta el login
- Login exitoso navega a la pantalla principal sin posibilidad de regresar al login con el botón Atrás
- Registro exitoso regresa al login para iniciar sesión con la cuenta creada

### Arquitectura
- **MVVM**: `AuthViewModel` maneja el estado de la UI, `AuthRepository` maneja los datos
- **Room Database**: entidad `User` con tabla `users`, `UserDao` con operaciones de inserción y consulta
- **StateFlow**: comunicación reactiva entre ViewModel y UI
- **Jetpack Compose**: toda la interfaz de usuario sin layouts XML

### Tecnologías utilizadas
- Kotlin + Jetpack Compose
- Room Database 2.6.1
- ViewModel + StateFlow
- SHA-256 via `MessageDigest` (Java estándar, sin dependencias extra)
- SharedPreferences para persistencia de sesión

<img width="377" height="800" alt="image" src="https://github.com/user-attachments/assets/78be95de-c125-47b2-abd8-382fb1bd9569" />

# MathStep Free

Aplicación Android para estudiantes de secundaria que resuelve ecuaciones 
algebraicas lineales mostrando cada paso del procedimiento matemático.

Repositorio: https://github.com/SSGMMATEO/MathStep

---

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| UI | Jetpack Compose + Material Design 3 |
| Navegación | Single-Activity con estado `currentScreen` |
| ViewModel | AndroidViewModel + StateFlow + Coroutines |
| Base de datos | Room 2.7 (SQLite) |
| Red | Retrofit 2.11 + OkHttp + Gson |
| Background | WorkManager 2.9 |
| Seguridad | SHA-256 para hash de contraseñas |

---

## Funcionalidades implementadas

- Autenticación local: registro e inicio de sesión con hash SHA-256
- Sesión persistente con SharedPreferences
- Historial de ecuaciones con CRUD completo
- Verificación de expresiones matemáticas vía MathJS API REST
- Notificaciones locales diarias programadas con WorkManager

---

## Arquitectura

El proyecto sigue el patrón MVVM con separación en tres capas:

UI (Composable)
│  collectAsState()
▼
ViewModel (StateFlow)
│  viewModelScope.launch { }
▼
Repository
│
├──▶ DAO → Room / SQLite
└──▶ Retrofit → API externa

**UI**: observa el StateFlow del ViewModel con `collectAsState()` y redibuja 
solo los componentes afectados cuando el estado cambia.

**ViewModel**: expone el estado como StateFlow y funciones que la UI puede 
llamar. No accede a Room ni a Retrofit directamente.

**Repository**: capa intermedia que decide si los datos vienen de la base de 
datos local o de la red. El ViewModel no sabe cuál de los dos responde.

**DAO**: define las operaciones SQL con anotaciones de Room. Room genera el 
código de acceso a SQLite en tiempo de compilación.

---

## Base de datos

### Entidad User

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK autoincremental) | Identificador único |
| username | String | Nombre de usuario |
| email | String (índice único) | Correo electrónico |
| passwordHash | String | Hash SHA-256 de la contraseña |
| createdAt | Long | Timestamp de creación |

### Entidad HistoryRecord

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Int (PK autoincremental) | Identificador único |
| equation | String | Ecuación ingresada por el usuario |
| result | String | Resultado del cálculo |
| savedAt | Long | Timestamp de guardado |

### Relaciones

`User` y `HistoryRecord` son entidades independientes en esta versión. 
La conexión entre el solver y el guardado automático en historial 
(CA-04.1) está pendiente de implementar.

---

## CRUD — HistoryRecord

| Operación | Implementación | Criterio |
|-----------|---------------|---------|
| Create | Entrada manual desde el historial | CA-04.1 parcial |
| Read | `Flow<List<HistoryRecord>>` ordenado por `savedAt DESC` | CA-04.2 ✓ |
| Update | `AlertDialog` con campos precargados | ✓ |
| Delete | `AlertDialog` de confirmación + Snackbar con Deshacer | ✓ |

---

## API REST

**MathJS API** — evaluador de expresiones matemáticas externo.

- Endpoint: `GET https://api.mathjs.org/v4/?expr={expresión}&precision=4`
- Ejemplo: `https://api.mathjs.org/v4/?expr=2%2B3*4` devuelve `14`
- Documentación: https://api.mathjs.org

La llamada se maneja con un `sealed class` de tres estados:

```kotlin
sealed class ApiState {
    data object Idle    : ApiState()
    data object Loading : ApiState()
    data class  Success(val result: String) : ApiState()
    data class  Error(val message: String)  : ApiState()
}
```

La pantalla muestra un spinner en Loading, una card verde en Success 
y una card roja con el mensaje en Error.

---

## Notificaciones locales

Recordatorio diario programado con WorkManager a las 18:00 horas.

El Worker lee el conteo real de ecuaciones en Room y personaliza 
el mensaje:

- Sin registros: invita a resolver la primera ecuación
- Con registros: muestra el número de ecuaciones guardadas

En Android 13+ la app solicita el permiso `POST_NOTIFICATIONS` 
al iniciar. Si el usuario lo rechaza, las notificaciones no se 
programan.

El canal de notificación se crea en `MathStepApplication.onCreate()` 
para garantizar que existe antes de cualquier Worker o Activity.

---

## Estructura del proyecto

app/src/main/java/com/sanchez/mathstep/
├── MathStepApplication.kt
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── UserDao.kt
│   │   │   └── HistoryDao.kt
│   │   └── entity/
│   │       ├── User.kt
│   │       └── HistoryRecord.kt
│   ├── remote/
│   │   ├── ApiState.kt
│   │   ├── MathApiService.kt
│   │   └── RetrofitClient.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── HistoryRepository.kt
│       └── MathApiRepository.kt
└── ui/
├── auth/
│   ├── AuthViewModel.kt
│   ├── LoginScreen.kt
│   └── RegisterScreen.kt
├── history/
│   ├── HistoryUiState.kt
│   ├── HistoryViewModel.kt
│   └── HistoryScreen.kt
├── notifications/
│   ├── ReminderWorker.kt
│   ├── ImmediateNotificationWorker.kt
│   └── NotificationScheduler.kt
├── solver/
│   ├── SolverViewModel.kt
│   └── SolverScreen.kt
└── theme/
├── Color.kt
├── Theme.kt
└── Type.kt

---

## Cómo probar

**Requisitos**: Android Studio, emulador o dispositivo con Android 8.0+ 
(API 26), internet para la pantalla de API.

**Autenticación**
1. Abrir la app — aparece LoginScreen
2. Tocar "¿No tienes cuenta? Regístrate"
3. Ingresar nombre, correo válido y contraseña de mínimo 6 caracteres
4. Tocar "Crear cuenta" — regresa al login
5. Iniciar sesión con las credenciales registradas

**CRUD del historial**
1. Tocar "Ver historial" en la pantalla principal
2. CREATE: agregar un registro con ecuación y resultado
3. READ: la lista aparece ordenada del más reciente al más antiguo
4. UPDATE: tocar el ícono de lápiz, modificar y guardar
5. DELETE: tocar la papelera, confirmar en el diálogo, usar Deshacer 
   si se quiere restaurar

**API REST**
1. Tocar "Verificar con API"
2. Ingresar una expresión: `2 + 3 * 4`
3. Tocar "Verificar"
4. Observar los tres estados: Loading (spinner), Success (card verde), 
   Error (card roja si no hay internet)

**Notificación de prueba**
1. Tocar "Probar notificación" en la pantalla principal
2. Otorgar el permiso si el sistema lo solicita
3. La notificación aparece en el panel en pocos segundos

---

## Limitaciones conocidas

- CA-04.1 pendiente: el solver no guarda automáticamente en el historial; 
  el CREATE requiere entrada manual por ahora
- El solver paso a paso (HU-01, HU-02) está en desarrollo
- SHA-256 sin salt es aceptable para prototipo académico; 
  en producción se usaría bcrypt o Argon2

---

## Referencias

- Android Developers. (2024). *Room persistence library*.  
  https://developer.android.com/training/data-storage/room
- Android Developers. (2024). *WorkManager*.  
  https://developer.android.com/topic/libraries/architecture/workmanager
- Square. (2024). *Retrofit*. https://square.github.io/retrofit
- MathJS. (2024). *MathJS API*. https://api.mathjs.org
- Google. (2024). *Material Design 3*. https://m3.material.io


