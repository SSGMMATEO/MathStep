package com.sanchez.mathstep.ui.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanchez.mathstep.R
import com.sanchez.mathstep.data.local.AppDatabase
import kotlinx.coroutines.flow.first

/**
 * ImmediateNotificationWorker — Worker de prueba para captura de evidencia.
 *
 * A diferencia de ReminderWorker (periódico, cada 24h), este Worker
 * se encola con OneTimeWorkRequest y se ejecuta en los próximos segundos.
 * Útil para verificar que el canal, el permiso y el ícono funcionan
 * sin esperar al día siguiente.
 *
 * En producción este Worker no existiría; solo sirve para pruebas académicas.
 */
class ImmediateNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val isResolution = inputData.getBoolean("is_resolution", false)
        val expression = inputData.getString("expression") ?: ""
        val result = inputData.getString("result") ?: ""

        val db    = AppDatabase.getInstance(context)
        val count = db.historyDao().getAll().first().size

        val (titulo, mensaje) = if (isResolution && expression.isNotEmpty()) {
            "¡Ecuación resuelta!" to "$expression = $result"
        } else {
            val msg = when {
                count == 0 -> "¡Resuelve tu primera ecuación y guárdala en el historial!"
                count == 1 -> "Tienes 1 ecuación guardada. ¿La repasas hoy?"
                else       -> "Tienes $count ecuaciones guardadas. ¿Las repasas hoy?"
            }
            "MathStep Free" to msg
        }

        val notification = NotificationCompat.Builder(context, ReminderWorker.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(ReminderWorker.NOTIFICATION_ID + 1, notification)

        return Result.success()
    }
}