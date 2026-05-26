package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.CaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY updatedTimestamp DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getCaseById(id: Int): CaseEntity?

    @Query("SELECT * FROM cases WHERE clientId = :clientId ORDER BY updatedTimestamp DESC")
    fun getCasesByClient(clientId: Int): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE status = :status ORDER BY updatedTimestamp DESC")
    fun getCasesByStatus(status: String): Flow<List<CaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(case: CaseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CaseEntity>)

    @Query("UPDATE cases SET status = :status, updatedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE cases SET nextHearing = :hearingDate, updatedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateNextHearing(id: Int, hearingDate: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM cases WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM cases")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM cases")
    fun getCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cases WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>
}
