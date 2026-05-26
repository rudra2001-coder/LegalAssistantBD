package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY dueTimestamp ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND dueTimestamp > :currentTime ORDER BY dueTimestamp ASC")
    fun getPendingReminders(currentTime: Long = System.currentTimeMillis()): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE relatedCaseId = :caseId ORDER BY dueTimestamp ASC")
    fun getRemindersForCase(caseId: Int): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Int)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND dueTimestamp BETWEEN :startTime AND :endTime")
    suspend fun getDueReminders(startTime: Long, endTime: Long): List<ReminderEntity>
}
