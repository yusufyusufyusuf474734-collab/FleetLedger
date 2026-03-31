package com.fleet.ledger.core.data.repository

import com.fleet.ledger.core.data.local.dao.PartnerDao
import com.fleet.ledger.core.data.local.entity.toDomain
import com.fleet.ledger.core.data.local.entity.toEntity
import com.fleet.ledger.core.domain.model.Partner
import com.fleet.ledger.core.domain.repository.PartnerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartnerRepositoryImpl(
    private val partnerDao: PartnerDao
) : PartnerRepository {
    
    override fun getAllPartners(): Flow<List<Partner>> =
        partnerDao.getAll().map { entities -> entities.map { it.toDomain() } }
    
    override fun getPartnerById(id: Long): Flow<Partner?> =
        partnerDao.getById(id).map { it?.toDomain() }
    
    override suspend fun insertPartner(partner: Partner) {
        partnerDao.insert(partner.toEntity())
    }
    
    override suspend fun updatePartner(partner: Partner) {
        partnerDao.update(partner.toEntity())
    }
    
    override suspend fun deletePartner(partner: Partner) {
        partnerDao.delete(partner.toEntity())
    }
}
