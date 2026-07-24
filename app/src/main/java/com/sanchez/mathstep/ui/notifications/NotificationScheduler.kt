package com.sanchez.mathstep.ui.notifications

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_NAME = "mathstep_daily_reminder"

    fun scheduleDaily(context: Context) {
        val now    = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        val delay = target.timeInMillis - now.timeInMillis

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun sendNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<ImmediateNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }

    /**
     * El canal ya existe (se crea una sola vez en MathStepApplication.onCreate).
     * Antes este método también lo creaba de nuevo — redundante, eliminado.
     */
    fun triggerResolutionNotification(context: Context, expression: String, result: String) {
        val data = workDataOf("expression" to expression, "result" to result, "is_resolution" to true)
        val request = OneTimeWorkRequestBuilder<ImmediateNotificationWorker>().setInputData(data).build()
        WorkManager.getInstance(context).enqueue(request)
    }
}