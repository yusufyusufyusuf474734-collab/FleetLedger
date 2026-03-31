package com.fleet.ledger.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fleet.ledger.core.domain.model.Vehicle

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plate: String,
    val name: String,
    val brand: String = "",
    val year: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun VehicleEntity.toDomain() = Vehicle(
    id = id,
    plate = plate,
    name = name,
    brand = brand,
    year = year,
    createdAt = createdAt
)

fun Vehicle.toEntity() = VehicleEntity(
    id = id,
    plate = plate,
    name = name,
    brand = brand,
    year = year,
    createdAt = createdAt
)
