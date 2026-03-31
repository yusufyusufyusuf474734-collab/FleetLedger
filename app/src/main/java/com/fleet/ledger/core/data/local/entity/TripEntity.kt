package com.fleet.ledger.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fleet.ledger.core.domain.model.Trip

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class TripEntity(
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
    val receiptImagePath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun TripEntity.toDomain() = Trip(
    id = id,
    vehicleId = vehicleId,
    date = date,
    description = description,
    income = income,
    fuelCost = fuelCost,
    bridgeCost = bridgeCost,
    highwayCost = highwayCost,
    driverFee = driverFee,
    otherCost = otherCost,
    note = note,
    receiptImagePath = receiptImagePath,
    createdAt = createdAt
)

fun Trip.toEntity() = TripEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    description = description,
    income = income,
    fuelCost = fuelCost,
    bridgeCost = bridgeCost,
    highwayCost = highwayCost,
    driverFee = driverFee,
    otherCost = otherCost,
    note = note,
    receiptImagePath = receiptImagePath,
    createdAt = createdAt
)
