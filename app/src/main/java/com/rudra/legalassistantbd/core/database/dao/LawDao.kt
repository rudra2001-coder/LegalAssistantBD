package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.LawEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LawDao {
    @Query("SELECT * FROM laws ORDER BY year DESC")
    fun getAllLaws(): Flow<List<LawEntity>>

    @Query("SELECT * FROM laws WHERE id = :id")
    suspend fun getLawById(id: Int): LawEntity?

    @Query("SELECT * FROM laws WHERE titleEn LIKE '%' || :query || '%' OR titleBn LIKE '%' || :query || '%'")
    fun searchLaws(query: String): Flow<List<LawEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laws: List<LawEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(law: LawEntity)

    @Query("DELETE FROM laws")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM laws")
    suspend fun getCount(): Int

    @Query("SELECT id FROM laws ORDER BY id DESC LIMIT 1")
    suspend fun getLatestLawId(): Int?
}
