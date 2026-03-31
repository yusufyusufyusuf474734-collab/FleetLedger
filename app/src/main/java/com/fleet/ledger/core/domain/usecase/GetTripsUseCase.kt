package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow

class GetTripsUseCase(private val repository: TripRepository) {
    operator fun invoke(vehicleId: Long): Flow<List<Trip>> = 
        repository.getTripsByVehicle(vehicleId)
}
