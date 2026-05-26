package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String? = null,
    val dueTimestamp: Long,
    val reminderType: String = "Hearing",
    val relatedCaseId: Int? = null,
    val isCompleted: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)
