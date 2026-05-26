package com.rudra.legalassistantbd.data.repository

import com.rudra.legalassistantbd.core.database.dao.ProcedureDao
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcedureRepository @Inject constructor(
    private val procedureDao: ProcedureDao
) {
    fun getProceduresForSection(sectionId: Int): Flow<List<ProcedureEntity>> =
        procedureDao.getProceduresForSection(sectionId)

    suspend fun getProcedureById(id: Int): ProcedureEntity? = procedureDao.getProcedureById(id)

    suspend fun insertAll(procedures: List<ProcedureEntity>) = procedureDao.insertAll(procedures)
}
