package com.fleet.ledger.core.domain.model

data class VehiclePartnerShare(
    val vehicleId: Long,
    val vehiclePlate: String,
    val partnerId: Long,
    val partnerName: String,
    val sharePercent: Double,
    val totalProfit: Double,
    val shareAmount: Double
)
