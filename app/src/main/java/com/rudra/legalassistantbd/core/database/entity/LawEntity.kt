package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "laws")
data class LawEntity(
    @PrimaryKey val id: Int,
    val titleEn: String,
    val titleBn: String,
    val shortTitle: String,
    val year: Int,
    val description: String,
    val isActive: Boolean = true
)
