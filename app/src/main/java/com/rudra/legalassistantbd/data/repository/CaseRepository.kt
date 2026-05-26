package com.rudra.legalassistantbd.data.repository

import com.rudra.legalassistantbd.core.database.dao.CaseDao
import com.rudra.legalassistantbd.core.database.dao.CaseProcedureProgressDao
import com.rudra.legalassistantbd.core.database.dao.ClientDao
import com.rudra.legalassistantbd.core.database.dao.EvidenceDao
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import com.rudra.legalassistantbd.core.database.entity.CaseProcedureProgressEntity
import com.rudra.legalassistantbd.core.database.entity.ClientEntity
import com.rudra.legalassistantbd.core.database.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaseRepository @Inject constructor(
    private val caseDao: CaseDao,
    private val clientDao: ClientDao,
    private val evidenceDao: EvidenceDao,
    private val caseProcedureProgressDao: CaseProcedureProgressDao
) {
    fun getAllCases(): Flow<List<CaseEntity>> = caseDao.getAllCases()
    suspend fun getCaseById(id: Int): CaseEntity? = caseDao.getCaseById(id)
    fun getCasesByClient(clientId: Int): Flow<List<CaseEntity>> = caseDao.getCasesByClient(clientId)
    fun getCasesByStatus(status: String): Flow<List<CaseEntity>> = caseDao.getCasesByStatus(status)
    suspend fun insertCase(case: CaseEntity): Long = caseDao.insert(case)
    suspend fun updateCaseStatus(id: Int, status: String) = caseDao.updateStatus(id, status)
    suspend fun updateNextHearing(id: Int, hearingDate: Long) = caseDao.updateNextHearing(id, hearingDate)
    suspend fun deleteCase(id: Int) = caseDao.delete(id)
    suspend fun getCaseCount(): Int = caseDao.getCount()
    suspend fun getCaseCountByStatus(status: String): Int = caseDao.getCountByStatus(status)

    fun getAllClients(): Flow<List<ClientEntity>> = clientDao.getAllClients()
    suspend fun getClientById(id: Int): ClientEntity? = clientDao.getClientById(id)
    fun searchClients(query: String): Flow<List<ClientEntity>> = clientDao.searchClients(query)
    suspend fun insertClient(client: ClientEntity): Long = clientDao.insert(client)
    suspend fun deleteClient(id: Int) = clientDao.delete(id)
    suspend fun getClientCount(): Int = clientDao.getCount()

    fun getEvidenceForCase(caseId: Int): Flow<List<EvidenceEntity>> = evidenceDao.getEvidenceForCase(caseId)
    suspend fun insertEvidence(evidence: EvidenceEntity): Long = evidenceDao.insert(evidence)
    suspend fun deleteEvidence(id: Int) = evidenceDao.delete(id)

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
}
