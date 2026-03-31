package com.fleet.ledger.core.domain.repository

import com.fleet.ledger.core.domain.model.Partner
import kotlinx.coroutines.flow.Flow

interface PartnerRepository {
    fun getAllPartners(): Flow<List<Partner>>
    fun getPartnerById(id: Long): Flow<Partner?>
    suspend fun insertPartner(partner: Partner)
    suspend fun updatePartner(partner: Partner)
    suspend fun deletePartner(partner: Partner)
}
