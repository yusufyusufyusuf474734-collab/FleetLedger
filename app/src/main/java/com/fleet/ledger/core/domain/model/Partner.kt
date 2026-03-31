package com.fleet.ledger.core.domain.model

data class Partner(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
