package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "law_sections",
    foreignKeys = [ForeignKey(
        entity = LawEntity::class,
        parentColumns = ["id"],
        childColumns = ["lawId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("lawId")]
)
data class LawSectionEntity(
    @PrimaryKey val id: Int,
    val lawId: Int,
    val sectionNumber: String,
    val titleEn: String,
    val titleBn: String,
    val contentEn: String,
    val contentBn: String,
    val courtType: String? = null,
    val bailStatus: String? = null,
    val punishment: String? = null,
    val isBailable: Boolean = false,
    val isCognizable: Boolean = false,
    val compoundable: String? = null,
    val orderIndex: Int = 0,
    val isCustom: Boolean = false
)
