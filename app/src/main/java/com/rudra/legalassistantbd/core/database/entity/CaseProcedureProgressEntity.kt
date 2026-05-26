package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "case_procedure_progress",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["caseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProcedureEntity::class,
            parentColumns = ["id"],
            childColumns = ["procedureId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("caseId"), Index("procedureId")]
)
data class CaseProcedureProgressEntity(
    @PrimaryKey val id: Int,
    val caseId: Int,
    val procedureId: Int,
    val isCompleted: Boolean = false,
    val completedTimestamp: Long? = null,
    val notes: String? = null
)
