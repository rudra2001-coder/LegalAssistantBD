package com.rudra.legalassistantbd.data.repository

import com.rudra.legalassistantbd.core.database.dao.*
import com.rudra.legalassistantbd.core.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaseRepository @Inject constructor(
    private val caseDao: CaseDao,
    private val clientDao: ClientDao,
    private val evidenceDao: EvidenceDao,
    private val caseProcedureProgressDao: CaseProcedureProgressDao,
    private val hearingDao: HearingDao,
    private val bailDao: BailDao,
    private val caseDocumentDao: CaseDocumentDao,
    private val caseNoteDao: CaseNoteDao
) {
    fun getAllCases(): Flow<List<CaseEntity>> = caseDao.getAllCases()
    suspend fun getCaseById(id: Int): CaseEntity? = caseDao.getCaseById(id)
    fun getCasesByClient(clientId: Int): Flow<List<CaseEntity>> = caseDao.getCasesByClient(clientId)
    fun getCasesByStatus(status: String): Flow<List<CaseEntity>> = caseDao.getCasesByStatus(status)
    suspend fun insertCase(case: CaseEntity): Long = caseDao.insert(case)
    suspend fun updateCaseStatus(id: Int, status: String) = caseDao.updateStatus(id, status)
    suspend fun updateNextHearing(id: Int, hearingDate: Long) = caseDao.updateNextHearing(id, hearingDate)
    suspend fun updateCase(case: CaseEntity) = caseDao.insert(case)
    suspend fun deleteCase(id: Int) = caseDao.delete(id)
    fun getCaseCount(): Flow<Int> = caseDao.getCount()
    fun getCaseCountByStatus(status: String): Flow<Int> = caseDao.getCountByStatus(status)

    // Clients
    fun getAllClients(): Flow<List<ClientEntity>> = clientDao.getAllClients()
    suspend fun getClientById(id: Int): ClientEntity? = clientDao.getClientById(id)
    fun searchClients(query: String): Flow<List<ClientEntity>> = clientDao.searchClients(query)
    suspend fun insertClient(client: ClientEntity): Long = clientDao.insert(client)
    suspend fun deleteClient(id: Int) = clientDao.delete(id)
    suspend fun getClientCount(): Int = clientDao.getCount()

    // Evidence
    fun getEvidenceForCase(caseId: Int): Flow<List<EvidenceEntity>> = evidenceDao.getEvidenceForCase(caseId)
    suspend fun insertEvidence(evidence: EvidenceEntity): Long = evidenceDao.insert(evidence)
    suspend fun deleteEvidence(id: Int) = evidenceDao.delete(id)

    // Procedure Progress
    fun getProcedureProgress(caseId: Int): Flow<List<CaseProcedureProgressEntity>> =
        caseProcedureProgressDao.getProgressForCase(caseId)
    suspend fun getCompletedCount(caseId: Int): Int = caseProcedureProgressDao.getCompletedCount(caseId)
    suspend fun getTotalCount(caseId: Int): Int = caseProcedureProgressDao.getTotalCount(caseId)
    suspend fun markProcedureCompleted(caseId: Int, procedureId: Int) =
        caseProcedureProgressDao.markCompleted(caseId, procedureId)
    suspend fun markProcedureIncomplete(caseId: Int, procedureId: Int) =
        caseProcedureProgressDao.markIncomplete(caseId, procedureId)
    suspend fun initializeProcedureProgress(caseId: Int, procedureIds: List<Int>) {
        val entries = procedureIds.mapIndexed { index, procId ->
            CaseProcedureProgressEntity(
                id = "${caseId}_$index".hashCode(),
                caseId = caseId,
                procedureId = procId
            )
        }
        caseProcedureProgressDao.insertAll(entries)
    }

    // Hearings
    fun getHearingsForCase(caseId: Int): Flow<List<HearingEntity>> = hearingDao.getHearingsForCase(caseId)
    suspend fun getHearingById(id: Int): HearingEntity? = hearingDao.getHearingById(id)
    fun getHearingsBetween(startTime: Long, endTime: Long): Flow<List<HearingEntity>> =
        hearingDao.getHearingsBetween(startTime, endTime)
    suspend fun insertHearing(hearing: HearingEntity): Long = hearingDao.insert(hearing)
    suspend fun updateHearing(id: Int, outcome: String?, nextHearingDate: Long?, notes: String?) =
        hearingDao.updateHearing(id, outcome, nextHearingDate, notes)
    suspend fun deleteHearing(id: Int) = hearingDao.delete(id)

    // Bails
    fun getBailsForCase(caseId: Int): Flow<List<BailEntity>> = bailDao.getBailsForCase(caseId)
    suspend fun getBailById(id: Int): BailEntity? = bailDao.getBailById(id)
    suspend fun insertBail(bail: BailEntity): Long = bailDao.insert(bail)
    suspend fun updateBail(id: Int, status: String, orderDate: Long?, orderDetails: String?, suretyDetails: String?, bailAmount: String?) =
        bailDao.updateBail(id, status, orderDate, orderDetails, suretyDetails, bailAmount)
    suspend fun deleteBail(id: Int) = bailDao.delete(id)

    // Case Documents
    fun getDocumentsForCase(caseId: Int): Flow<List<CaseDocumentEntity>> = caseDocumentDao.getDocumentsForCase(caseId)
    suspend fun getDocumentById(id: Int): CaseDocumentEntity? = caseDocumentDao.getDocumentById(id)
    suspend fun insertDocument(document: CaseDocumentEntity): Long = caseDocumentDao.insert(document)
    suspend fun deleteDocument(id: Int) = caseDocumentDao.delete(id)

    // Case Notes
    fun getNotesForCase(caseId: Int): Flow<List<CaseNoteEntity>> = caseNoteDao.getNotesForCase(caseId)
    suspend fun insertNote(note: CaseNoteEntity): Long = caseNoteDao.insert(note)
    suspend fun deleteNote(id: Int) = caseNoteDao.delete(id)
    suspend fun deleteAllNotesForCase(caseId: Int) = caseNoteDao.deleteAllForCase(caseId)
}
