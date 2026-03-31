package com.fleet.ledger.core.domain.model

enum class MaintenanceType(val label: String) {
    OIL_CHANGE("Yağ Değişimi"),
    TIRE_CHANGE("Lastik Değişimi"),
    BRAKE_CHECK("Fren Kontrolü"),
    FILTER_CHANGE("Filtre Değişimi"),
    GENERAL_SERVICE("Genel Bakım"),
    REPAIR("Tamir"),
    OTHER("Diğer")
}

data class Maintenance(
    val id: Long = 0,
    val vehicleId: Long,
    val type: MaintenanceType,
    val description: String,
    val date: Long,
    val cost: Double,
    val mileage: Int = 0,
    val nextMaintenanceKm: Int? = null,
    val nextMaintenanceDate: Long? = null,
    val workshop: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
