package com.fleet.ledger.core.domain.model

data class MonthlyExpense(
    val id: Long = 0,
    val vehicleId: Long,
    val year: Int,
    val month: Int,
    val label: String,
    val amount: Double,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
