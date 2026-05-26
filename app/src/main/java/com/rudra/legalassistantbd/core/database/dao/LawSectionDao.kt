package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LawSectionDao {
    @Query("SELECT * FROM law_sections WHERE lawId = :lawId ORDER BY orderIndex")
    fun getSectionsByLaw(lawId: Int): Flow<List<LawSectionEntity>>

    @Query("SELECT * FROM law_sections WHERE id = :id")
    suspend fun getSectionById(id: Int): LawSectionEntity?

    @Query("SELECT * FROM law_sections WHERE sectionNumber = :sectionNumber")
    suspend fun getSectionByNumber(sectionNumber: String): LawSectionEntity?

    @Query("SELECT * FROM law_sections WHERE sectionNumber = :sectionNumber AND lawId = :lawId")
    suspend fun getSectionByNumberAndLaw(sectionNumber: String, lawId: Int): LawSectionEntity?

    @Query("""
        SELECT ls.* FROM law_sections ls
        INNER JOIN law_sections_fts fts ON ls.id = fts.rowid
        WHERE law_sections_fts MATCH :query
    """)
    fun searchSections(query: String): Flow<List<LawSectionEntity>>

    @Query("""
        SELECT ls.* FROM law_sections ls
        INNER JOIN law_keywords kw ON ls.id = kw.sectionId
        WHERE kw.keywordEn = :keyword OR kw.keywordBn = :keyword
        GROUP BY ls.id
    """)
    fun searchByKeyword(keyword: String): Flow<List<LawSectionEntity>>

    @Query("SELECT * FROM law_sections WHERE isCustom = 1 ORDER BY titleEn ASC")
    fun getCustomSections(): Flow<List<LawSectionEntity>>

    @Query("SELECT * FROM law_sections WHERE isCustom = 1 OR lawId IN (SELECT id FROM laws) ORDER BY CASE WHEN isCustom = 1 THEN 0 ELSE 1 END, titleEn ASC")
    fun getAllSectionsForSelector(): Flow<List<LawSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sections: List<LawSectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(section: LawSectionEntity)

    @Query("DELETE FROM law_sections")
    suspend fun deleteAll()

    @Query("DELETE FROM law_sections WHERE isCustom = 1 AND id = :id")
    suspend fun deleteCustomSection(id: Int)

    @Query("SELECT COUNT(*) FROM law_sections")
    suspend fun getCount(): Int
}
