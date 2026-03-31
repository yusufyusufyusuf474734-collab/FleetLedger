package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehiclesUseCase(private val repository: VehicleRepository) {
    operator fun invoke(): Flow<List<Vehicle>> = repository.getAllVehicles()
}
