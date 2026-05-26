package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "law_keywords",
    foreignKeys = [ForeignKey(
        entity = LawSectionEntity::class,
        parentColumns = ["id"],
        childColumns = ["sectionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sectionId")]
)
data class LawKeywordEntity(
    @PrimaryKey val id: Int,
    val sectionId: Int,
    val keywordEn: String,
    val keywordBn: String,
    val synonymGroup: String? = null
)
