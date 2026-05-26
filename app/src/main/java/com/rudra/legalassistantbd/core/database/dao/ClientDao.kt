package com.rudra.legalassistantbd.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rudra.legalassistantbd.core.database.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Int): ClientEntity?

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%'")
    fun searchClients(query: String): Flow<List<ClientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity): Long

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM clients")
    suspend fun getCount(): Int
}
