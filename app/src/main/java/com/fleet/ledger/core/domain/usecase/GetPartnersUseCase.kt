package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Partner
import com.fleet.ledger.core.domain.repository.PartnerRepository
import kotlinx.coroutines.flow.Flow

class GetPartnersUseCase(private val repository: PartnerRepository) {
    operator fun invoke(): Flow<List<Partner>> = repository.getAllPartners()
}
