package com.rudra.legalassistantbd.core.database

import com.rudra.legalassistantbd.data.repository.LawRepository
import com.rudra.legalassistantbd.data.repository.ProcedureRepository
import com.rudra.legalassistantbd.laws.LawDataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val lawRepository: LawRepository,
    private val procedureRepository: ProcedureRepository
) {
    suspend fun initializeIfNeeded() {
        if (lawRepository.getLawCount() == 0) {
            lawRepository.insertLaws(LawDataProvider.getDefaultLaws())
            lawRepository.insertSections(LawDataProvider.getDefaultSections())
            procedureRepository.insertAll(LawDataProvider.getDefaultProcedures())
        }
    }
}
