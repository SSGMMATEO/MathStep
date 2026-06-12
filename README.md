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
