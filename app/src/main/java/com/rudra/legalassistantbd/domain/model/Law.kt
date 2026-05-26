package com.rudra.legalassistantbd.domain.model

data class Law(
    val id: Int,
    val titleEn: String,
    val titleBn: String,
    val shortTitle: String,
    val year: Int,
    val description: String,
    val isActive: Boolean = true
)
