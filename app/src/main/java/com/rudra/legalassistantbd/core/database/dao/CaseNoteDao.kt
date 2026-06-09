package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.CaseNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseNoteDao {
    @Query("SELECT * FROM case_notes WHERE caseId = :caseId ORDER BY addedTimestamp DESC")
    fun getNotesForCase(caseId: Int): Flow<List<CaseNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: CaseNoteEntity): Long

    @Query("DELETE FROM case_notes WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM case_notes WHERE caseId = :caseId")
    suspend fun deleteAllForCase(caseId: Int)
}
