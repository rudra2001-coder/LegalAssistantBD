package com.rudra.legalassistantbd.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_imports")
data class PdfImportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "law_id")
    val lawId: Int,
    @ColumnInfo(name = "sections_count")
    val sectionsCount: Int,
    @ColumnInfo(name = "imported_at")
    val importedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "status")
    val status: String = "completed"
)
