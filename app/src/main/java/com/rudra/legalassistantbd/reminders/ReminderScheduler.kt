package com.rudra.legalassistantbd.reminders

import android.content.Context
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleReminder(reminder: ReminderEntity) {
        ReminderWorker.scheduleReminder(
            context = context,
            reminderId = reminder.id,
            title = reminder.title,
            description = reminder.description ?: "",
            dueTimestamp = reminder.dueTimestamp
        )
    }

    fun cancelReminder(reminderId: Int) {
        ReminderWorker.cancelReminder(context, reminderId)
    }

    fun rescheduleReminder(reminder: ReminderEntity) {
        cancelReminder(reminder.id)
        scheduleReminder(reminder)
    }
}
