package com.rudra.legalassistantbd.domain.model

data class Procedure(
    val id: Int,
    val sectionId: Int,
    val stepNumber: Int,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val requiredDocuments: String? = null,
    val duration: String? = null,
    val courtType: String? = null
)
