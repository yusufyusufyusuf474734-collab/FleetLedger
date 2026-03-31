package com.fleet.ledger.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fleet.ledger.core.domain.model.Document
import com.fleet.ledger.core.domain.model.DocumentType

@Entity(
    tableName = "documents",
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
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val type: String,
    val title: String,
    val company: String = "",
    val policyNo: String = "",
    val startDate: Long? = null,
    val expiryDate: Long? = null,
    val amount: Double = 0.0,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun DocumentEntity.toDomain() = Document(
    id = id,
    vehicleId = vehicleId,
    type = DocumentType.valueOf(type),
    title = title,
    company = company,
    policyNo = policyNo,
    startDate = startDate,
    expiryDate = expiryDate,
    amount = amount,
    note = note,
    createdAt = createdAt
)

fun Document.toEntity() = DocumentEntity(
    id = id,
    vehicleId = vehicleId,
    type = type.name,
    title = title,
    company = company,
    policyNo = policyNo,
    startDate = startDate,
    expiryDate = expiryDate,
    amount = amount,
    note = note,
    createdAt = createdAt
)
