package com.sanchez.mathstep.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanchez.mathstep.R
import com.sanchez.mathstep.data.local.AppDatabase
import kotlinx.coroutines.flow.first

/**
 * ReminderWorker — tarea en background programada con WorkManager.
 *
 * CoroutineWorker: versión suspendible del Worker, permite llamar
 * funciones suspend de Room directamente sin callbacks.
 *
 * doWork(): WorkManager la llama automáticamente en el momento
 * programado. Retorna Result.success() para indicar que terminó bien.
 */
class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID   = "mathstep_reminders"
        const val CHANNEL_NAME = "Recordatorios MathStep"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        // Cuenta cuántas ecuaciones hay en el historial
        val db    = AppDatabase.getInstance(context)
        val count = db.historyDao().getAll().first().size

        // Solo notifica si hay al menos un registro guardado
        if (count > 0) {
            showNotification(count)
        }

        return Result.success()
    }

    private fun showNotification(count: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // Crea el canal (obligatorio en Android 8+, ignorado en versiones anteriores)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Recordatorio diario de repaso de ecuaciones"
        }
        manager.createNotificationChannel(channel)

        val mensaje = if (count == 1)
            "Tienes 1 ecuación guardada. ¿La repasas hoy?"
        else
            "Tienes $count ecuaciones guardadas. ¿Las repasas hoy?"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("MathStep Free")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}