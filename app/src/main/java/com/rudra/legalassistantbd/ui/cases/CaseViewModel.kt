package com.rudra.legalassistantbd.ui.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.*
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

    private val _hearings = MutableStateFlow<List<HearingEntity>>(emptyList())
    val hearings: StateFlow<List<HearingEntity>> = _hearings.asStateFlow()

    private val _bails = MutableStateFlow<List<BailEntity>>(emptyList())
    val bails: StateFlow<List<BailEntity>> = _bails.asStateFlow()

    private val _documents = MutableStateFlow<List<CaseDocumentEntity>>(emptyList())
    val documents: StateFlow<List<CaseDocumentEntity>> = _documents.asStateFlow()

    private val _notes = MutableStateFlow<List<CaseNoteEntity>>(emptyList())
    val notes: StateFlow<List<CaseNoteEntity>> = _notes.asStateFlow()

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
            if (caseEntity == null) {
                _isLoading.value = false
                return@launch
            }
            _selectedCase.value = caseEntity

            launch { caseRepository.getEvidenceForCase(caseId).collect { _evidence.value = it } }
            launch { caseRepository.getHearingsForCase(caseId).collect { _hearings.value = it } }
            launch { caseRepository.getBailsForCase(caseId).collect { _bails.value = it } }
            launch { caseRepository.getDocumentsForCase(caseId).collect { _documents.value = it } }
            launch { caseRepository.getNotesForCase(caseId).collect { _notes.value = it } }

            caseEntity.sectionId?.let { sectionId ->
                val section = lawRepository.getSectionById(sectionId)
                _linkedSection.value = section
                if (section != null) {
                    launch { lawRepository.getProceduresForSection(sectionId).collect { _sectionProcedures.value = it } }
                }
                launch {
                    caseRepository.getProcedureProgress(caseId).collect { progress ->
                        _procedureProgress.value = progress
                        _progressSummary.value = progress.count { it.isCompleted } to progress.size
                    }
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
        opponentAdvocate: String?,
        advocateName: String?,
        advocatePhone: String?,
        courtName: String?,
        judgeName: String?,
        policeStation: String?,
        firNumber: String?,
        firDate: Long?,
        filingNumber: String?,
        caseYear: String?,
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
                opponentAdvocate = opponentAdvocate,
                advocateName = advocateName,
                advocatePhone = advocatePhone,
                courtName = courtName,
                judgeName = judgeName,
                policeStation = policeStation,
                firNumber = firNumber,
                firDate = firDate,
                filingNumber = filingNumber,
                caseYear = caseYear,
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

    fun updateCase(caseEntity: CaseEntity) {
        viewModelScope.launch {
            caseRepository.updateCase(caseEntity)
        }
    }

    fun updateCaseDetails(
        caseId: Int,
        title: String,
        caseType: String,
        description: String,
        opponentName: String?,
        opponentAdvocate: String?,
        advocateName: String?,
        advocatePhone: String?,
        courtName: String?,
        judgeName: String?,
        policeStation: String?,
        firNumber: String?,
        firDate: Long?,
        filingNumber: String?,
        caseYear: String?,
        sectionId: Int?
    ) {
        viewModelScope.launch {
            val existing = caseRepository.getCaseById(caseId) ?: return@launch
            val updated = existing.copy(
                title = title,
                caseType = caseType,
                description = description,
                opponentName = opponentName,
                opponentAdvocate = opponentAdvocate,
                advocateName = advocateName,
                advocatePhone = advocatePhone,
                courtName = courtName,
                judgeName = judgeName,
                policeStation = policeStation,
                firNumber = firNumber,
                firDate = firDate,
                filingNumber = filingNumber,
                caseYear = caseYear,
                sectionId = sectionId,
                updatedTimestamp = System.currentTimeMillis()
            )
            caseRepository.updateCase(updated)
            loadCase(caseId)
        }
    }

    fun updateCaseStatus(caseId: Int, status: String) {
        viewModelScope.launch {
            caseRepository.updateCaseStatus(caseId, status)
            loadCase(caseId)
        }
    }

    fun deleteCase(caseId: Int) {
        viewModelScope.launch {
            caseRepository.deleteCase(caseId)
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

    // Hearing Management
    fun addHearing(
        caseId: Int,
        hearingDate: Long,
        hearingType: String,
        courtName: String?,
        judgeName: String?,
        description: String?
    ) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toInt()
            val hearing = HearingEntity(
                id = id,
                caseId = caseId,
                hearingDate = hearingDate,
                hearingType = hearingType,
                courtName = courtName,
                judgeName = judgeName,
                description = description
            )
            caseRepository.insertHearing(hearing)
            caseRepository.updateNextHearing(caseId, hearingDate)
            loadCase(caseId)
        }
    }

    fun updateHearingOutcome(id: Int, outcome: String?, nextHearingDate: Long?, notes: String?) {
        viewModelScope.launch {
            caseRepository.updateHearing(id, outcome, nextHearingDate, notes)
        }
    }

    fun deleteHearing(id: Int) {
        viewModelScope.launch {
            caseRepository.deleteHearing(id)
        }
    }

    // Bail Management
    fun addBail(
        caseId: Int,
        bailType: String,
        petitionDate: Long,
        petitionNumber: String?,
        courtName: String?,
        hearingDate: Long?
    ) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toInt()
            val bail = BailEntity(
                id = id,
                caseId = caseId,
                bailType = bailType,
                petitionDate = petitionDate,
                petitionNumber = petitionNumber,
                courtName = courtName,
                hearingDate = hearingDate
            )
            caseRepository.insertBail(bail)
            loadCase(caseId)
        }
    }

    fun updateBailStatus(
        id: Int,
        status: String,
        orderDate: Long?,
        orderDetails: String?,
        suretyDetails: String?,
        bailAmount: String?
    ) {
        viewModelScope.launch {
            caseRepository.updateBail(id, status, orderDate, orderDetails, suretyDetails, bailAmount)
        }
    }

    fun deleteBail(id: Int) {
        viewModelScope.launch {
            caseRepository.deleteBail(id)
        }
    }

    // Documents
    fun addDocument(
        caseId: Int,
        title: String,
        description: String?,
        filePath: String,
        documentType: String,
        mimeType: String?,
        fileSize: Long?
    ) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toInt()
            val document = CaseDocumentEntity(
                id = id,
                caseId = caseId,
                title = title,
                description = description,
                filePath = filePath,
                documentType = documentType,
                mimeType = mimeType,
                fileSize = fileSize
            )
            caseRepository.insertDocument(document)
            loadCase(caseId)
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            caseRepository.deleteDocument(id)
        }
    }

    // Notes
    fun addNote(caseId: Int, content: String) {
        viewModelScope.launch {
            val id = System.currentTimeMillis().toInt()
            val note = CaseNoteEntity(
                id = id,
                caseId = caseId,
                content = content
            )
            caseRepository.insertNote(note)
            loadCase(caseId)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            caseRepository.deleteNote(id)
        }
    }

    // Client Management
    fun createClient(name: String, phone: String?, email: String?, address: String?, fatherName: String?, occupation: String?): Int {
        val id = System.currentTimeMillis().toInt()
        val client = ClientEntity(
            id = id,
            name = name,
            fatherName = fatherName,
            phone = phone,
            email = email,
            address = address,
            occupation = occupation
        )
        viewModelScope.launch {
            caseRepository.insertClient(client)
        }
        return id
    }
}
