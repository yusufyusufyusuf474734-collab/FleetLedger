package com.fleet.ledger.core.domain.repository

import com.fleet.ledger.core.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getAllVehicles(): Flow<List<Vehicle>>
    fun getVehicleById(id: Long): Flow<Vehicle?>
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
}
