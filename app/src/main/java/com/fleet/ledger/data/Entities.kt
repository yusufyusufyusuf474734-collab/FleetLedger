package com.fleet.ledger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plate: String,
    val name: String,        // Şoför / araç adı
    val brand: String = "",  // Marka/model
    val year: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "trips",
    foreignKeys = [ForeignKey(Vehicle::class, ["id"], ["vehicleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("vehicleId")]
)
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalExpense: Double get() = fuelCost + bridgeCost + highwayCost + driverFee + otherCost
    val netProfit: Double get() = income - totalExpense
}

enum class DocumentType(val label: String) {
    KASKO("Kasko"),
    TRAFIK_SIGORTASI("Trafik Sigortası"),
    KOLTUK_SIGORTASI("Koltuk Sigortası"),
    RUHSAT("Araç Ruhsatı"),
    MUAYENE("Araç Muayenesi"),
    DIGER("Diğer")
}

@Entity(
    tableName = "documents",
    foreignKeys = [ForeignKey(Vehicle::class, ["id"], ["vehicleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("vehicleId")]
)
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val type: DocumentType,
    val title: String,           // Belge başlığı / poliçe no
    val company: String = "",    // Sigorta şirketi
    val policyNo: String = "",   // Poliçe numarası
    val startDate: Long? = null, // Başlangıç tarihi
    val expiryDate: Long? = null,// Bitiş tarihi
    val amount: Double = 0.0,    // Prim / ücret
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
