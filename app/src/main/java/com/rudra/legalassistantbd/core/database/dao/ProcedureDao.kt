package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.ProcedureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcedureDao {
    @Query("SELECT * FROM procedures WHERE sectionId = :sectionId ORDER BY stepNumber")
    fun getProceduresForSection(sectionId: Int): Flow<List<ProcedureEntity>>

    @Query("SELECT * FROM procedures WHERE id = :id")
    suspend fun getProcedureById(id: Int): ProcedureEntity?

    @Query("SELECT * FROM procedures WHERE sectionId = :sectionId ORDER BY stepNumber")
    suspend fun getProceduresForSectionOnce(sectionId: Int): List<ProcedureEntity>

    @Query("SELECT COALESCE(MAX(stepNumber), 0) FROM procedures WHERE sectionId = :sectionId")
    suspend fun getMaxStepNumber(sectionId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(procedures: List<ProcedureEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(procedure: ProcedureEntity): Long

    @Query("SELECT * FROM procedures WHERE sectionId IN (:sectionIds) ORDER BY sectionId, stepNumber")
    suspend fun getProceduresBySectionIds(sectionIds: List<Int>): List<ProcedureEntity>

    @Query("DELETE FROM procedures WHERE sectionId = :sectionId")
    suspend fun deleteProceduresForSection(sectionId: Int)

    @Query("DELETE FROM procedures")
    suspend fun deleteAll()

    @Query("DELETE FROM procedures WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM procedures")
    suspend fun getCount(): Int
}
