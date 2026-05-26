package com.rudra.legalassistantbd.ui.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import com.rudra.legalassistantbd.core.database.entity.ReminderEntity
import com.rudra.legalassistantbd.data.repository.CaseRepository
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Green  = Color(0xFF52E8A0)
private val Blue   = Color(0xFF5B9CF6)
private val Orange = Color(0xFFFF8C42)

data class RecentItem(
    val title: String,
    val subtitle: String,
    val dotColor: Color,
    val badgeLabel: String
)

data class DashboardState(
    val lawCount: Int = 0,
    val sectionCount: Int = 0,
    val activeCases: Int = 0,
    val totalCases: Int = 0,
    val pendingReminders: Int = 0,
    val recentItems: List<RecentItem> = emptyList(),
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
        viewModelScope.launch {
            combine(
                lawRepository.getLawCount(),
                lawRepository.getSectionCount(),
                caseRepository.getCaseCountByStatus("Active"),
                caseRepository.getCaseCount(),
                reminderRepository.getPendingReminders()
            ) { lawCount, sectionCount, activeCases, totalCases, pendingReminders ->

                val now = System.currentTimeMillis()
                val allCases = caseRepository.getAllCases().first()
                val allReminders = reminderRepository.getAllReminders().first()

                val items = mutableListOf<RecentItem>()

                allCases.take(2).forEach { c ->
                    items.add(
                        RecentItem(
                            title = c.title,
                            subtitle = if (c.status == "Active") "Active case" else c.status,
                            dotColor = if (c.status == "Active") Green else Blue,
                            badgeLabel = "CASE"
                        )
                    )
                }

                allReminders
                    .filter { !it.isCompleted && it.dueTimestamp > now }
                    .take(2)
                    .forEach { r ->
                        items.add(
                            RecentItem(
                                title = r.title,
                                subtitle = "Reminder set",
                                dotColor = Orange,
                                badgeLabel = "REMINDER"
                            )
                        )
                    }

                DashboardState(
                    lawCount = lawCount,
                    sectionCount = sectionCount,
                    activeCases = activeCases,
                    totalCases = totalCases,
                    pendingReminders = pendingReminders.size,
                    recentItems = items,
                    isLoading = false
                )
            }.catch { e ->
                _state.update { it.copy(isLoading = false) }
            }.collect { state ->
                _state.value = state
            }
        }
    }
}
