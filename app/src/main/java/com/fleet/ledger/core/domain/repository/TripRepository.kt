package com.fleet.ledger.core.domain.repository

import com.fleet.ledger.core.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getTripsByVehicle(vehicleId: Long): Flow<List<Trip>>
    fun getTripsInRange(vehicleId: Long, from: Long, to: Long): Flow<List<Trip>>
    suspend fun insertTrip(trip: Trip)
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)
}
