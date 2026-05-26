package com.rudra.legalassistantbd.ui.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import com.rudra.legalassistantbd.core.database.entity.CaseProcedureProgressEntity
import com.rudra.legalassistantbd.core.database.entity.ClientEntity
import com.rudra.legalassistantbd.core.database.entity.EvidenceEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import com.rudra.legalassistantbd.data.repository.CaseRepository
import com.rudra.legalassistantbd.data.repository.LawRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaseViewModel @Inject constructor(
    private val caseRepository: CaseRepository,
    private val lawRepository: LawRepository
) : ViewModel() {

    val cases: StateFlow<List<CaseEntity>> = caseRepository.getAllCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCase = MutableStateFlow<CaseEntity?>(null)
    val selectedCase: StateFlow<CaseEntity?> = _selectedCase.asStateFlow()

    val clients: StateFlow<List<ClientEntity>> = caseRepository.getAllClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _evidence = MutableStateFlow<List<EvidenceEntity>>(emptyList())
    val evidence: StateFlow<List<EvidenceEntity>> = _evidence.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allSections: StateFlow<List<LawSectionEntity>> = lawRepository.getAllSectionsForSelector()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _linkedSection = MutableStateFlow<LawSectionEntity?>(null)
    val linkedSection: StateFlow<LawSectionEntity?> = _linkedSection.asStateFlow()

    private val _procedureProgress = MutableStateFlow<List<CaseProcedureProgressEntity>>(emptyList())
    val procedureProgress: StateFlow<List<CaseProcedureProgressEntity>> = _procedureProgress.asStateFlow()

    private val _sectionProcedures = MutableStateFlow<List<ProcedureEntity>>(emptyList())
    val sectionProcedures: StateFlow<List<ProcedureEntity>> = _sectionProcedures.asStateFlow()

    private val _progressSummary = MutableStateFlow<Pair<Int, Int>>(0 to 0)
    val progressSummary: StateFlow<Pair<Int, Int>> = _progressSummary.asStateFlow()

    fun loadCase(caseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val caseEntity = caseRepository.getCaseById(caseId)
            _selectedCase.value = caseEntity
            caseRepository.getEvidenceForCase(caseId).collect { ev ->
                _evidence.value = ev
            }

            caseEntity?.sectionId?.let { sectionId ->
                val section = lawRepository.getSectionById(sectionId)
                _linkedSection.value = section
                if (section != null) {
                    lawRepository.getProceduresForSection(sectionId).collect { procs ->
                        _sectionProcedures.value = procs
                    }
                }
                caseRepository.getProcedureProgress(caseId).collect { progress ->
                    _procedureProgress.value = progress
                    val completed = progress.count { it.isCompleted }
                    val total = progress.size
                    _progressSummary.value = completed to total
                }
            }
            _isLoading.value = false
        }
    }

    fun createCase(
        title: String,
        caseType: String,
        description: String,
        clientId: Int?,
        opponentName: String?,
        courtName: String?,
        sectionId: Int?
    ) {
        viewModelScope.launch {
            val caseId = System.currentTimeMillis().toInt()
            val caseNum = "CASE/${caseType.take(3).uppercase()}/$caseId"
            val caseEntity = CaseEntity(
                id = caseId,
                caseNumber = caseNum,
                title = title,
                caseType = caseType,
                clientId = clientId,
                opponentName = opponentName,
                courtName = courtName,
                filingDate = System.currentTimeMillis(),
                status = "Active",
                description = description,
                sectionId = sectionId
            )
            caseRepository.insertCase(caseEntity)

            sectionId?.let { sid ->
                val procedures = lawRepository.getProceduresForSectionOnce(sid)
                if (procedures.isNotEmpty()) {
                    caseRepository.initializeProcedureProgress(
                        caseId = caseId,
                        procedureIds = procedures.map { it.id }
                    )
                }
            }
        }
    }

    fun updateCaseStatus(caseId: Int, status: String) {
        viewModelScope.launch {
            caseRepository.updateCaseStatus(caseId, status)
        }
    }

    fun addEvidence(caseId: Int, title: String, description: String, filePath: String?) {
        viewModelScope.launch {
            val evidenceId = System.currentTimeMillis().toInt()
            val evidence = EvidenceEntity(
                id = evidenceId,
                caseId = caseId,
                title = title,
                description = description,
                filePath = filePath
            )
            caseRepository.insertEvidence(evidence)
        }
    }

    fun markProcedureCompleted(caseId: Int, procedureId: Int) {
        viewModelScope.launch {
            caseRepository.markProcedureCompleted(caseId, procedureId)
        }
    }

    fun markProcedureIncomplete(caseId: Int, procedureId: Int) {
        viewModelScope.launch {
            caseRepository.markProcedureIncomplete(caseId, procedureId)
        }
    }
}
