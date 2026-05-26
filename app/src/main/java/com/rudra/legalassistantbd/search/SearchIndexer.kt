package com.rudra.legalassistantbd.search

import com.rudra.legalassistantbd.core.database.dao.LawSectionDao
import com.rudra.legalassistantbd.core.database.dao.LawDao
import com.rudra.legalassistantbd.core.database.entity.LawKeywordEntity
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchIndexer @Inject constructor(
    private val sectionDao: LawSectionDao,
    private val lawDao: LawDao,
    private val bengaliSearchEngine: BengaliSearchEngine
) {
    fun search(query: String): Flow<List<LawSectionEntity>> {
        val ftsQuery = bengaliSearchEngine.buildFtsQuery(query)
        return sectionDao.searchSections(ftsQuery)
    }

    fun searchByKeyword(keyword: String): Flow<List<LawSectionEntity>> {
        return sectionDao.searchByKeyword(keyword)
    }

    fun generateKeywordEntities(section: LawSectionEntity, keywords: List<Pair<String, String>>): List<LawKeywordEntity> {
        return keywords.mapIndexed { index, (en, bn) ->
            LawKeywordEntity(
                id = "${section.id}_kw_$index".hashCode(),
                sectionId = section.id,
                keywordEn = en,
                keywordBn = bn
            )
        }
    }
}
