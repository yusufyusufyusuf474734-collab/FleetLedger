package com.fleet.ledger.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {
    val vehicles: Flow<List<Vehicle>> = db.vehicleDao().getAll()
    val allTrips: Flow<List<Trip>> = db.tripDao().getAll()
    val summaries: Flow<List<VehicleSummary>> = db.tripDao().getSummaryPerVehicle()

    fun tripsForVehicle(vid: Long): Flow<List<Trip>> = db.tripDao().getByVehicle(vid)
    fun tripsForVehicleInRange(vid: Long, from: Long, to: Long): Flow<List<Trip>> =
        db.tripDao().getByVehicleAndRange(vid, from, to)

    suspend fun addVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().insert(v) }
    suspend fun updateVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().update(v) }
    suspend fun deleteVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().delete(v) }

    suspend fun addTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().insert(t) }
    suspend fun updateTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().update(t) }
    suspend fun deleteTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().delete(t) }
}
