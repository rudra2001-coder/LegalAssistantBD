package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hearings",
    foreignKeys = [ForeignKey(
        entity = CaseEntity::class,
        parentColumns = ["id"],
        childColumns = ["caseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("caseId")]
)
data class HearingEntity(
    @PrimaryKey val id: Int,
    val caseId: Int,
    val hearingDate: Long,
    val hearingType: String = "Regular Hearing",
    val courtName: String? = null,
    val judgeName: String? = null,
    val description: String? = null,
    val outcome: String? = null,
    val nextHearingDate: Long? = null,
    val notes: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
