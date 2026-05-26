package com.rudra.legalassistantbd.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.data.repository.CaseRepository
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val lawCount: Int = 0,
    val sectionCount: Int = 0,
    val activeCases: Int = 0,
    val totalCases: Int = 0,
    val pendingReminders: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val lawRepository: LawRepository,
    private val caseRepository: CaseRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                val lawCount = lawRepository.getLawCount()
                val sectionCount = lawRepository.getSectionCount()
                val activeCases = caseRepository.getCaseCountByStatus("Active")
                val totalCases = caseRepository.getCaseCount()
                val pendingReminders = reminderRepository.getPendingReminders().first().size

                _state.update {
                    it.copy(
                        lawCount = lawCount,
                        sectionCount = sectionCount,
                        activeCases = activeCases,
                        totalCases = totalCases,
                        pendingReminders = pendingReminders,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
