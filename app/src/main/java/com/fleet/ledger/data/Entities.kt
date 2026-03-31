package com.fleet.ledger.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── Araç ─────────────────────────────────────────────────────────────────────

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plate: String,
    val name: String,
    val brand: String = "",
    val year: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ── Ortak ─────────────────────────────────────────────────────────────────────

@Entity(tableName = "partners")
data class Partner(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ── Araç-Ortak ilişkisi (hisse oranı) ────────────────────────────────────────

@Entity(
    tableName = "vehicle_partners",
    primaryKeys = ["vehicleId", "partnerId"],
    foreignKeys = [
        ForeignKey(Vehicle::class, ["id"], ["vehicleId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Partner::class, ["id"], ["partnerId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("vehicleId"), Index("partnerId")]
)
data class VehiclePartner(
    val vehicleId: Long,
    val partnerId: Long,
    val sharePercent: Double  // 0-100 arası hisse yüzdesi
)

// ── Sefer ─────────────────────────────────────────────────────────────────────

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
    val receiptImagePath: String = "",   // Fiş fotoğrafı yolu
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalExpense: Double get() = fuelCost + bridgeCost + highwayCost + driverFee + otherCost
    val netProfit: Double get() = income - totalExpense
}

// ── Aylık Sabit Gider ─────────────────────────────────────────────────────────

@Entity(
    tableName = "monthly_expenses",
    foreignKeys = [ForeignKey(Vehicle::class, ["id"], ["vehicleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("vehicleId")]
)
data class MonthlyExpense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val year: Int,
    val month: Int,          // 1-12
    val label: String,       // SGK, HGS, Bakım, vb.
    val amount: Double,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ── Belge ─────────────────────────────────────────────────────────────────────

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
    val title: String,
    val company: String = "",
    val policyNo: String = "",
    val startDate: Long? = null,
    val expiryDate: Long? = null,
    val amount: Double = 0.0,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
