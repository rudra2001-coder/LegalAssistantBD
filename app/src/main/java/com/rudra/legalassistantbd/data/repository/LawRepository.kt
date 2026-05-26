package com.rudra.legalassistantbd.data.repository

import com.rudra.legalassistantbd.core.database.dao.LawDao
import com.rudra.legalassistantbd.core.database.dao.LawSectionDao
import com.rudra.legalassistantbd.core.database.dao.PdfImportDao
import com.rudra.legalassistantbd.core.database.dao.ProcedureDao
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.PdfImportEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LawRepository @Inject constructor(
    private val lawDao: LawDao,
    private val sectionDao: LawSectionDao,
    private val procedureDao: ProcedureDao,
    private val pdfImportDao: PdfImportDao
) {
    fun getAllLaws(): Flow<List<LawEntity>> = lawDao.getAllLaws()
    suspend fun getLawById(id: Int): LawEntity? = lawDao.getLawById(id)
    fun searchLaws(query: String): Flow<List<LawEntity>> = lawDao.searchLaws(query)
    fun getSectionsByLaw(lawId: Int): Flow<List<LawSectionEntity>> = sectionDao.getSectionsByLaw(lawId)
    suspend fun getSectionById(id: Int): LawSectionEntity? = sectionDao.getSectionById(id)
    suspend fun getSectionByNumber(sectionNumber: String): LawSectionEntity? = sectionDao.getSectionByNumber(sectionNumber)
    fun searchSections(query: String): Flow<List<LawSectionEntity>> = sectionDao.searchSections(query)
    fun searchByKeyword(keyword: String): Flow<List<LawSectionEntity>> = sectionDao.searchByKeyword(keyword)
    suspend fun insertLaws(laws: List<LawEntity>) = lawDao.insertAll(laws)
    suspend fun insertSections(sections: List<LawSectionEntity>) = sectionDao.insertAll(sections)
    suspend fun insertSection(section: LawSectionEntity) = sectionDao.insert(section)
    fun getLawCount(): Flow<Int> = lawDao.getCount()
    fun getSectionCount(): Flow<Int> = sectionDao.getCount()
    suspend fun deleteCustomSection(id: Int) = sectionDao.deleteCustomSection(id)

    fun getCustomSections(): Flow<List<LawSectionEntity>> = sectionDao.getCustomSections()
    fun getAllSectionsForSelector(): Flow<List<LawSectionEntity>> = sectionDao.getAllSectionsForSelector()

    fun getProceduresForSection(sectionId: Int): Flow<List<ProcedureEntity>> = procedureDao.getProceduresForSection(sectionId)
    suspend fun getProceduresForSectionOnce(sectionId: Int): List<ProcedureEntity> = procedureDao.getProceduresForSectionOnce(sectionId)
    suspend fun getMaxStepNumber(sectionId: Int): Int = procedureDao.getMaxStepNumber(sectionId)
    suspend fun insertProcedure(procedure: ProcedureEntity) = procedureDao.insert(procedure)
    suspend fun deleteProceduresForSection(sectionId: Int) = procedureDao.deleteProceduresForSection(sectionId)
    suspend fun deleteProcedure(id: Int) = procedureDao.deleteById(id)

    suspend fun searchBySectionNumbers(numbers: List<String>, lawId: Int): List<LawSectionEntity> =
        sectionDao.searchBySectionNumbers(numbers, lawId)

    suspend fun insertProcedures(procedures: List<ProcedureEntity>) = procedureDao.insertAll(procedures)

    suspend fun getProceduresBySectionIds(sectionIds: List<Int>): List<ProcedureEntity> =
        procedureDao.getProceduresBySectionIds(sectionIds)

    suspend fun getLatestLawId(): Int? = lawDao.getLatestLawId()

    fun getAllImports(): Flow<List<PdfImportEntity>> = pdfImportDao.getAllImports()
    suspend fun insertImport(import: PdfImportEntity) = pdfImportDao.insert(import)
}
