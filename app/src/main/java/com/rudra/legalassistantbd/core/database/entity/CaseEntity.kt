package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val id: Int,
    val caseNumber: String,
    val title: String,
    val caseType: String,
    val clientId: Int? = null,
    val opponentName: String? = null,
    val opponentAdvocate: String? = null,
    val advocateName: String? = null,
    val advocatePhone: String? = null,
    val courtName: String? = null,
    val judgeName: String? = null,
    val policeStation: String? = null,
    val firNumber: String? = null,
    val firDate: Long? = null,
    val filingNumber: String? = null,
    val caseYear: String? = null,
    val filingDate: Long,
    val nextHearing: Long? = null,
    val status: String = "Active",
    val description: String? = null,
    val sectionId: Int? = null,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)
