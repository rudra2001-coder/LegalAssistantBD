package com.rudra.legalassistantbd.domain.model

data class Reminder(
    val id: Int,
    val title: String,
    val description: String? = null,
    val dueTimestamp: Long,
    val reminderType: String = "Hearing",
    val relatedCaseId: Int? = null,
    val isCompleted: Boolean = false
)
