package com.rudra.legalassistantbd.domain.model

data class LawSection(
    val id: Int,
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
    val compoundable: String? = null
)
