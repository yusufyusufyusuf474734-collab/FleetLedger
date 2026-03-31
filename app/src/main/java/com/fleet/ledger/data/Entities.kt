package com.fleet.ledger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plate: String,       // Plaka
    val name: String,        // Şoför adı veya araç adı
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "trips",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: Long,              // epoch ms
    val description: String = "", // Sefer açıklaması (örn: İstanbul-Ankara)
    // Gelir
    val income: Double = 0.0,
    // Gider kalemleri
    val fuelCost: Double = 0.0,      // Yakıt
    val bridgeCost: Double = 0.0,    // Köprü
    val highwayCost: Double = 0.0,   // Otoban
    val driverFee: Double = 0.0,     // Şoför ücreti
    val otherCost: Double = 0.0,     // Diğer
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalExpense: Double get() = fuelCost + bridgeCost + highwayCost + driverFee + otherCost
    val netProfit: Double get() = income - totalExpense
}
