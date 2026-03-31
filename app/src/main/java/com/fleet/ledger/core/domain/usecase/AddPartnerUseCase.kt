package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Partner
import com.fleet.ledger.core.domain.repository.PartnerRepository

class AddPartnerUseCase(private val repository: PartnerRepository) {
    suspend operator fun invoke(partner: Partner) {
        repository.insertPartner(partner)
    }
}
