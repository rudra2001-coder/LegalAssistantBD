package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
