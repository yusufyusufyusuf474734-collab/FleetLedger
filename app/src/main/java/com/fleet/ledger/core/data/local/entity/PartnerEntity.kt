package com.fleet.ledger.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fleet.ledger.core.domain.model.Partner

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun PartnerEntity.toDomain() = Partner(
    id = id,
    name = name,
    phone = phone,
    note = note,
    createdAt = createdAt
)

fun Partner.toEntity() = PartnerEntity(
    id = id,
    name = name,
    phone = phone,
    note = note,
    createdAt = createdAt
)
