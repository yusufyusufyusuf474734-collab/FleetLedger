package com.fleet.ledger.core.domain.model

data class VehicleSummary(
    val vehicleId: Long,
    val vehiclePlate: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val netProfit: Double,
    val tripCount: Int,
    val lastTripDate: Long?
)
