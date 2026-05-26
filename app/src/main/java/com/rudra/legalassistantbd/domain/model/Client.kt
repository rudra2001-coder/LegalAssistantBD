package com.rudra.legalassistantbd.domain.model

data class Client(
    val id: Int,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val notes: String? = null
)
