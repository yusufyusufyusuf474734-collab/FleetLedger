package com.fleet.ledger.core.domain.model

data class Trip(
    val id: Long = 0,
    val vehicleId: Long,
    val date: Long,
    val description: String = "",
    val income: Double = 0.0,
    val fuelCost: Double = 0.0,
    val bridgeCost: Double = 0.0,
    val highwayCost: Double = 0.0,
    val driverFee: Double = 0.0,
    val otherCost: Double = 0.0,
    val note: String = "",
    val receiptImagePath: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalExpense: Double get() = fuelCost + bridgeCost + highwayCost + driverFee + otherCost
    val netProfit: Double get() = income - totalExpense
}
