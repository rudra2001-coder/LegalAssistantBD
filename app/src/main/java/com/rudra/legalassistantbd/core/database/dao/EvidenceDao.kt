package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence WHERE caseId = :caseId ORDER BY uploadedTimestamp DESC")
    fun getEvidenceForCase(caseId: Int): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evidence: EvidenceEntity): Long

    @Query("DELETE FROM evidence WHERE id = :id")
    suspend fun delete(id: Int)
}
