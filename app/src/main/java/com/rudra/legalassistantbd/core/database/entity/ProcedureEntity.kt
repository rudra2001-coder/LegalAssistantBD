package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "procedures")
data class ProcedureEntity(
    @PrimaryKey val id: Int,
    val sectionId: Int,
    val stepNumber: Int,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val requiredDocuments: String? = null,
    val duration: String? = null,
    val courtType: String? = null
)
