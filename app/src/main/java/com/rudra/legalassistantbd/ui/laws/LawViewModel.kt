package com.rudra.legalassistantbd.ui.laws

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.data.repository.ProcedureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LawViewModel @Inject constructor(
    private val lawRepository: LawRepository,
    private val procedureRepository: ProcedureRepository
) : ViewModel() {

    val laws: StateFlow<List<LawEntity>> = lawRepository.getAllLaws()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _sections = MutableStateFlow<List<LawSectionEntity>>(emptyList())
    val sections: StateFlow<List<LawSectionEntity>> = _sections.asStateFlow()

    private val _selectedLaw = MutableStateFlow<LawEntity?>(null)
    val selectedLaw: StateFlow<LawEntity?> = _selectedLaw.asStateFlow()

    private val _selectedSection = MutableStateFlow<LawSectionEntity?>(null)
    val selectedSection: StateFlow<LawSectionEntity?> = _selectedSection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSections(lawId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            lawRepository.getLawById(lawId)?.let { _selectedLaw.value = it }
            lawRepository.getSectionsByLaw(lawId).collect { sectionList ->
                _sections.value = sectionList
                _isLoading.value = false
            }
        }
    }

    fun loadSectionById(sectionId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val section = lawRepository.getSectionById(sectionId)
            _selectedSection.value = section
            _isLoading.value = false
        }
    }

    fun searchSections(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            lawRepository.searchSections(query).collect { result ->
                _sections.value = result
                _isLoading.value = false
            }
        }
    }
}
