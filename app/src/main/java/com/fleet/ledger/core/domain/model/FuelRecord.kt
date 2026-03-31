package com.fleet.ledger.core.domain.model

data class FuelRecord(
    val id: Long = 0,
    val vehicleId: Long,
    val date: Long,
    val liters: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    val mileage: Int,
    val station: String = "",
    val fuelType: String = "Dizel",
    val fullTank: Boolean = true,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val kmPerLiter: Double
        get() = if (liters > 0) mileage / liters else 0.0
}
