package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bails",
    foreignKeys = [ForeignKey(
        entity = CaseEntity::class,
        parentColumns = ["id"],
        childColumns = ["caseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("caseId")]
)
data class BailEntity(
    @PrimaryKey val id: Int,
    val caseId: Int,
    val bailType: String = "Regular",
    val petitionDate: Long,
    val petitionNumber: String? = null,
    val bailStatus: String = "Pending",
    val courtName: String? = null,
    val hearingDate: Long? = null,
    val orderDate: Long? = null,
    val orderDetails: String? = null,
    val suretyDetails: String? = null,
    val bailAmount: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
