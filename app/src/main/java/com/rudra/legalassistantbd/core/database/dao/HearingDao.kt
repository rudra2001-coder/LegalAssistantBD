package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.HearingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HearingDao {
    @Query("SELECT * FROM hearings WHERE caseId = :caseId ORDER BY hearingDate DESC")
    fun getHearingsForCase(caseId: Int): Flow<List<HearingEntity>>

    @Query("SELECT * FROM hearings WHERE id = :id")
    suspend fun getHearingById(id: Int): HearingEntity?

    @Query("SELECT * FROM hearings WHERE hearingDate BETWEEN :startTime AND :endTime ORDER BY hearingDate ASC")
    fun getHearingsBetween(startTime: Long, endTime: Long): Flow<List<HearingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hearing: HearingEntity): Long

    @Query("UPDATE hearings SET outcome = :outcome, nextHearingDate = :nextHearingDate, notes = :notes WHERE id = :id")
    suspend fun updateHearing(id: Int, outcome: String?, nextHearingDate: Long?, notes: String?)

    @Query("DELETE FROM hearings WHERE id = :id")
    suspend fun delete(id: Int)
}
