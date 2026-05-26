package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.PdfImportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfImportDao {
    @Query("SELECT * FROM pdf_imports ORDER BY imported_at DESC")
    fun getAllImports(): Flow<List<PdfImportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(import: PdfImportEntity): Long

    @Query("DELETE FROM pdf_imports WHERE id = :id")
    suspend fun deleteById(id: Int)
}
