package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "case_documents",
    foreignKeys = [ForeignKey(
        entity = CaseEntity::class,
        parentColumns = ["id"],
        childColumns = ["caseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("caseId")]
)
data class CaseDocumentEntity(
    @PrimaryKey val id: Int,
    val caseId: Int,
    val title: String,
    val description: String? = null,
    val filePath: String,
    val documentType: String = "Document",
    val mimeType: String? = null,
    val fileSize: Long? = null,
    val addedTimestamp: Long = System.currentTimeMillis()
)
