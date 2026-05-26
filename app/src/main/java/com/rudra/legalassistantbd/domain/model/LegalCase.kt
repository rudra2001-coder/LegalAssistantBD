package com.rudra.legalassistantbd.domain.model

data class LegalCase(
    val id: Int,
    val caseNumber: String,
    val title: String,
    val caseType: String,
    val clientId: Int? = null,
    val opponentName: String? = null,
    val courtName: String? = null,
    val judgeName: String? = null,
    val filingDate: Long,
    val nextHearing: Long? = null,
    val status: String = "Active",
    val description: String? = null
)
