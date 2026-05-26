package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.CaseProcedureProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseProcedureProgressDao {
    @Query("SELECT * FROM case_procedure_progress WHERE caseId = :caseId ORDER BY id ASC")
    fun getProgressForCase(caseId: Int): Flow<List<CaseProcedureProgressEntity>>

    @Query("SELECT * FROM case_procedure_progress WHERE caseId = :caseId AND procedureId = :procedureId")
    suspend fun getProgress(caseId: Int, procedureId: Int): CaseProcedureProgressEntity?

    @Query("SELECT COUNT(*) FROM case_procedure_progress WHERE caseId = :caseId AND isCompleted = 1")
    suspend fun getCompletedCount(caseId: Int): Int

    @Query("SELECT COUNT(*) FROM case_procedure_progress WHERE caseId = :caseId")
    suspend fun getTotalCount(caseId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: CaseProcedureProgressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progressList: List<CaseProcedureProgressEntity>)

    @Query("UPDATE case_procedure_progress SET isCompleted = 1, completedTimestamp = :timestamp WHERE caseId = :caseId AND procedureId = :procedureId")
    suspend fun markCompleted(caseId: Int, procedureId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE case_procedure_progress SET isCompleted = 0, completedTimestamp = NULL WHERE caseId = :caseId AND procedureId = :procedureId")
    suspend fun markIncomplete(caseId: Int, procedureId: Int)

    @Query("UPDATE case_procedure_progress SET notes = :notes WHERE caseId = :caseId AND procedureId = :procedureId")
    suspend fun updateNotes(caseId: Int, procedureId: Int, notes: String)

    @Query("DELETE FROM case_procedure_progress WHERE caseId = :caseId")
    suspend fun deleteAllForCase(caseId: Int)
}
