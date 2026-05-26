package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evidence",
    foreignKeys = [ForeignKey(
        entity = CaseEntity::class,
        parentColumns = ["id"],
        childColumns = ["caseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("caseId")]
)
data class EvidenceEntity(
    @PrimaryKey val id: Int,
    val caseId: Int,
    val title: String,
    val description: String? = null,
    val filePath: String? = null,
    val evidenceType: String = "Document",
    val uploadedTimestamp: Long = System.currentTimeMillis()
)
