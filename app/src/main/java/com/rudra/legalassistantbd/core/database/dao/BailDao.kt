package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.BailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BailDao {
    @Query("SELECT * FROM bails WHERE caseId = :caseId ORDER BY petitionDate DESC")
    fun getBailsForCase(caseId: Int): Flow<List<BailEntity>>

    @Query("SELECT * FROM bails WHERE id = :id")
    suspend fun getBailById(id: Int): BailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bail: BailEntity): Long

    @Query("UPDATE bails SET bailStatus = :status, orderDate = :orderDate, orderDetails = :orderDetails, suretyDetails = :suretyDetails, bailAmount = :bailAmount, updatedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateBail(id: Int, status: String, orderDate: Long?, orderDetails: String?, suretyDetails: String?, bailAmount: String?, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM bails WHERE id = :id")
    suspend fun delete(id: Int)
}
