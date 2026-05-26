package com.rudra.legalassistantbd.core.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(contentEntity = LawSectionEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "law_sections_fts")
data class LawSectionFtsEntity(
    val titleEn: String,
    val titleBn: String,
    val contentEn: String,
    val contentBn: String
)
