package com.fleet.ledger.core.domain.usecase

import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.repository.VehicleRepository

class DeleteVehicleUseCase(private val repository: VehicleRepository) {
    suspend operator fun invoke(vehicle: Vehicle) {
        repository.deleteVehicle(vehicle)
    }
}
