package com.fleet.ledger.core.data.repository

import com.fleet.ledger.core.data.local.dao.VehicleDao
import com.fleet.ledger.core.data.local.entity.toDomain
import com.fleet.ledger.core.data.local.entity.toEntity
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val vehicleDao: VehicleDao
) : VehicleRepository {
    
    override fun getAllVehicles(): Flow<List<Vehicle>> =
        vehicleDao.getAll().map { entities -> entities.map { it.toDomain() } }
    
    override fun getVehicleById(id: Long): Flow<Vehicle?> =
        vehicleDao.getById(id).map { it?.toDomain() }
    
    override suspend fun insertVehicle(vehicle: Vehicle) {
        vehicleDao.insert(vehicle.toEntity())
    }
    
    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.update(vehicle.toEntity())
    }
    
    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.delete(vehicle.toEntity())
    }
}
