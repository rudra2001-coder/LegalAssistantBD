package com.rudra.legalassistantbd.domain.model

data class DocumentTemplate(
    val id: Int,
    val name: String,
    val type: String,
    val templateContent: String,
    val placeholders: List<String> = emptyList()
)
