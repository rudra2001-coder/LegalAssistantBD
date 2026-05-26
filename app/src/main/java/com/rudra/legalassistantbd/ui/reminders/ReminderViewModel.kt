package com.rudra.legalassistantbd.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import com.rudra.legalassistantbd.data.repository.ReminderRepository
import com.rudra.legalassistantbd.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> = reminderRepository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, description: String, dueTimestamp: Long, reminderType: String, caseId: Int?) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toInt()
            val reminder = ReminderEntity(
                id = id,
                title = title,
                description = description,
                dueTimestamp = dueTimestamp,
                reminderType = reminderType,
                relatedCaseId = caseId
            )
            val insertedId = reminderRepository.insertReminder(reminder)
            reminderScheduler.scheduleReminder(reminder)
        }
    }

    fun markCompleted(id: Int) {
        viewModelScope.launch {
            reminderRepository.markCompleted(id)
            reminderScheduler.cancelReminder(id)
        }
    }

    fun deleteReminder(id: Int) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(id)
            reminderScheduler.cancelReminder(id)
        }
    }
}
