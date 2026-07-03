package com.sanchez.mathstep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.sanchez.mathstep.ui.notifications.ReminderWorker

/**
 * MathStepApplication — clase Application personalizada.
 *
 * Por qué aquí y no en MainActivity:
 *   El canal de notificación debe existir ANTES de que cualquier
 *   notificación se muestre. Application.onCreate() se ejecuta
 *   antes que cualquier Activity, Service o Worker, garantizando
 *   que el canal siempre está creado cuando se necesita.
 *
 * ¿Qué es un canal de notificación?
 *   Desde Android 8 (API 26), cada notificación pertenece a un canal.
 *   El usuario puede silenciar o bloquear canales individualmente
 *   desde Ajustes del sistema sin desinstalar la app. Google introdujo
 *   los canales para dar control al usuario sobre qué tipos de
 *   notificaciones acepta de cada app.
 */
class MathStepApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ReminderWorker.CHANNEL_ID,
            ReminderWorker.CHANNEL_NAME,
            // IMPORTANCE_DEFAULT: aparece en la barra de estado y hace sonido.
            // IMPORTANCE_LOW: sin sonido. IMPORTANCE_HIGH: heads-up (flota sobre la app).
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recordatorio diario para repasar ecuaciones guardadas en el historial"
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}