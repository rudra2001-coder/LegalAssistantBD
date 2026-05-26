package com.rudra.legalassistantbd.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rudra.legalassistantbd.MainActivity
import com.rudra.legalassistantbd.core.database.dao.ReminderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderDao: ReminderDao
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        createNotificationChannel()

        val reminderId = inputData.getInt(KEY_REMINDER_ID, -1)
        val reminderTitle = inputData.getString(KEY_REMINDER_TITLE) ?: "Legal Reminder"
        val reminderDesc = inputData.getString(KEY_REMINDER_DESC) ?: ""

        showNotification(reminderId, reminderTitle, reminderDesc)

        return Result.success()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Legal Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for court hearings, deadlines, and submissions"
        }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun showNotification(id: Int, title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminder_id", id)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(id, notification)
    }

    companion object {
        const val CHANNEL_ID = "legal_reminders_channel"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_REMINDER_TITLE = "reminder_title"
        const val KEY_REMINDER_DESC = "reminder_desc"
        const val KEY_REMINDER_TIME = "reminder_time"
        const val WORK_NAME_PREFIX = "reminder_"

        fun scheduleReminder(
            context: Context,
            reminderId: Int,
            title: String,
            description: String,
            dueTimestamp: Long
        ) {
            val inputData = Data.Builder()
                .putInt(KEY_REMINDER_ID, reminderId)
                .putString(KEY_REMINDER_TITLE, title)
                .putString(KEY_REMINDER_DESC, description)
                .putLong(KEY_REMINDER_TIME, dueTimestamp)
                .build()

            val delay = dueTimestamp - System.currentTimeMillis()
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(maxOf(delay, 0), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("$WORK_NAME_PREFIX$reminderId")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "$WORK_NAME_PREFIX$reminderId",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        fun cancelReminder(context: Context, reminderId: Int) {
            WorkManager.getInstance(context)
                .cancelUniqueWork("$WORK_NAME_PREFIX$reminderId")
        }
    }
}
