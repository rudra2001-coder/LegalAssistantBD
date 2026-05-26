package com.rudra.legalassistantbd.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.dao.CaseDao
import com.rudra.legalassistantbd.core.database.dao.LawDao
import com.rudra.legalassistantbd.core.database.dao.LawSectionDao
import com.rudra.legalassistantbd.core.database.dao.ProcedureDao
import com.rudra.legalassistantbd.core.database.dao.ReminderDao
import com.rudra.legalassistantbd.laws.LawDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val lawCount: Int = 0,
    val sectionCount: Int = 0,
    val caseCount: Int = 0,
    val reminderCount: Int = 0,
    val isResetting: Boolean = false,
    val statusMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val lawDao: LawDao,
    private val lawSectionDao: LawSectionDao,
    private val procedureDao: ProcedureDao,
    private val caseDao: CaseDao,
    private val reminderDao: ReminderDao
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val lawCount = lawDao.getCount()
            val sectionCount = lawSectionDao.getCount()
            val caseCount = caseDao.getCount()
            val reminderCount = reminderDao.getAllReminders().first().size
            _state.update {
                it.copy(
                    lawCount = lawCount,
                    sectionCount = sectionCount,
                    caseCount = caseCount,
                    reminderCount = reminderCount
                )
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _state.update { it.copy(isResetting = true, statusMessage = null) }
            try {
                lawDao.deleteAll()
                lawSectionDao.deleteAll()
                procedureDao.deleteAll()
                caseDao.deleteAll()
                val allReminders = reminderDao.getAllReminders().first()
                for (reminder in allReminders) {
                    reminderDao.delete(reminder.id)
                }

                val laws = LawDataProvider.getDefaultLaws()
                val sections = LawDataProvider.getDefaultSections()
                val procedures = LawDataProvider.getDefaultProcedures()

                lawDao.insertAll(laws)
                lawSectionDao.insertAll(sections)
                procedureDao.insertAll(procedures)

                _state.update {
                    it.copy(
                        isResetting = false,
                        statusMessage = "Reset complete. Default laws restored.",
                        lawCount = laws.size,
                        sectionCount = sections.size,
                        caseCount = 0,
                        reminderCount = 0
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isResetting = false,
                        statusMessage = "Reset failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearStatus() {
        _state.update { it.copy(statusMessage = null) }
    }

    fun getAppVersion(): String = "1.0.0"
}
