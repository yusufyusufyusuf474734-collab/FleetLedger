package com.fleet.ledger.core.domain.model

data class Vehicle(
    val id: Long = 0,
    val plate: String,
    val name: String,
    val brand: String = "",
    val year: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
