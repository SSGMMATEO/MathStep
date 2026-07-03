package com.sanchez.mathstep.ui.notifications

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * NotificationScheduler — programa y cancela el recordatorio diario.
 *
 * scheduleDaily(): calcula el tiempo hasta las 18:00 de hoy (o mañana
 * si ya pasó esa hora) y encola una tarea periódica de 24 horas.
 *
 * ExistingPeriodicWorkPolicy.KEEP: si ya hay una tarea programada
 * con el mismo nombre, no la reemplaza. Evita duplicados al llamar
 * scheduleDaily() varias veces (por ejemplo, al reiniciar la app).
 */
object NotificationScheduler {

    private const val WORK_NAME = "mathstep_daily_reminder"

    fun scheduleDaily(context: Context) {
        // Calcula cuántos milisegundos faltan para las 18:00
        val now    = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) {
            // Si las 18:00 de hoy ya pasó, programa para mañana
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        val delay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /**
     * sendNow(): encola una notificación inmediata para pruebas.
     */
    fun sendNow(context: Context) {
        val request = androidx.work.OneTimeWorkRequestBuilder<ImmediateNotificationWorker>()
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(request)
    }

    /**
     * triggerResolutionNotification(): se llama cuando el solver obtiene un resultado.
     */
    fun triggerResolutionNotification(context: Context, expression: String, result: String) {
        // Asegurar que el canal existe antes de notificar
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            val channel = android.app.NotificationChannel(
                ReminderWorker.CHANNEL_ID,
                ReminderWorker.CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de resolución de ecuaciones"
            }
            manager.createNotificationChannel(channel)
        }

        val data = androidx.work.workDataOf(
            "expression" to expression,
            "result" to result,
            "is_resolution" to true
        )
        val request = androidx.work.OneTimeWorkRequestBuilder<ImmediateNotificationWorker>()
            .setInputData(data)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(request)
    }
}