package com.sanchez.mathstep.ui.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sanchez.mathstep.R
import com.sanchez.mathstep.data.local.AppDatabase
import kotlinx.coroutines.flow.first

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
        val db    = AppDatabase.getInstance(context)
        val count = db.historyDao().getAll().first().size
        if (count > 0) showNotification(count)
        return Result.success()
    }

    // El canal se crea una sola vez en MathStepApplication.onCreate();
    // aquí solo se construye y dispara la notificación.
    private fun showNotification(count: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mensaje = if (count == 1) "Tienes 1 ecuación guardada. ¿La repasas hoy?"
        else "Tienes $count ecuaciones guardadas. ¿Las repasas hoy?"

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