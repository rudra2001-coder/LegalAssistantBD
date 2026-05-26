package com.rudra.legalassistantbd.data.repository

import com.rudra.legalassistantbd.core.database.dao.ReminderDao
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {
    fun getAllReminders(): Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    fun getPendingReminders(): Flow<List<ReminderEntity>> = reminderDao.getPendingReminders()
    fun getRemindersForCase(caseId: Int): Flow<List<ReminderEntity>> = reminderDao.getRemindersForCase(caseId)
    suspend fun insertReminder(reminder: ReminderEntity): Long = reminderDao.insert(reminder)
    suspend fun markCompleted(id: Int) = reminderDao.markCompleted(id)
    suspend fun deleteReminder(id: Int) = reminderDao.delete(id)
    suspend fun getDueReminders(startTime: Long, endTime: Long): List<ReminderEntity> =
        reminderDao.getDueReminders(startTime, endTime)
}
