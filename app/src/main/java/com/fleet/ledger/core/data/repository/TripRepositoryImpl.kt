package com.fleet.ledger.core.data.repository

import com.fleet.ledger.core.data.local.dao.TripDao
import com.fleet.ledger.core.data.local.entity.toDomain
import com.fleet.ledger.core.data.local.entity.toEntity
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val tripDao: TripDao
) : TripRepository {
    
    override fun getTripsByVehicle(vehicleId: Long): Flow<List<Trip>> =
        tripDao.getByVehicle(vehicleId).map { entities -> entities.map { it.toDomain() } }
    
    override fun getTripsInRange(vehicleId: Long, from: Long, to: Long): Flow<List<Trip>> =
        tripDao.getByVehicleAndRange(vehicleId, from, to).map { entities -> entities.map { it.toDomain() } }
    
    override suspend fun insertTrip(trip: Trip) {
        tripDao.insert(trip.toEntity())
    }
    
    override suspend fun updateTrip(trip: Trip) {
        tripDao.update(trip.toEntity())
    }
    
    override suspend fun deleteTrip(trip: Trip) {
        tripDao.delete(trip.toEntity())
    }
}
