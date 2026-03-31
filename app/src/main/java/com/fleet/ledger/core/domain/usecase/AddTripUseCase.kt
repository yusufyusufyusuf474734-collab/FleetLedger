package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.repository.TripRepository

class AddTripUseCase(private val repository: TripRepository) {
    suspend operator fun invoke(trip: Trip) {
        repository.insertTrip(trip)
    }
}
