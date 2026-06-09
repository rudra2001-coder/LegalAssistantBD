package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.CaseDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDocumentDao {
    @Query("SELECT * FROM case_documents WHERE caseId = :caseId ORDER BY addedTimestamp DESC")
    fun getDocumentsForCase(caseId: Int): Flow<List<CaseDocumentEntity>>

    @Query("SELECT * FROM case_documents WHERE id = :id")
    suspend fun getDocumentById(id: Int): CaseDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: CaseDocumentEntity): Long

    @Query("DELETE FROM case_documents WHERE id = :id")
    suspend fun delete(id: Int)
}
