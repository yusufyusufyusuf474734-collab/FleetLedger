package com.fleet.ledger.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fleet.ledger.core.domain.model.MonthlyExpense

@Entity(
    tableName = "monthly_expenses",
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
data class MonthlyExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val year: Int,
    val month: Int,
    val label: String,
    val amount: Double,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun MonthlyExpenseEntity.toDomain() = MonthlyExpense(
    id = id,
    vehicleId = vehicleId,
    year = year,
    month = month,
    label = label,
    amount = amount,
    note = note,
    createdAt = createdAt
)

fun MonthlyExpense.toEntity() = MonthlyExpenseEntity(
    id = id,
    vehicleId = vehicleId,
    year = year,
    month = month,
    label = label,
    amount = amount,
    note = note,
    createdAt = createdAt
)
