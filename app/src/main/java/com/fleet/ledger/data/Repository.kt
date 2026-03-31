package com.fleet.ledger.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {
    val vehicles: Flow<List<Vehicle>> = db.vehicleDao().getAll()
    val summaries: Flow<List<VehicleSummary>> = db.tripDao().getSummaryPerVehicle()
    val allDocuments: Flow<List<Document>> = db.documentDao().getAll()

    fun tripsFor(vid: Long): Flow<List<Trip>> = db.tripDao().getByVehicle(vid)
    fun documentsFor(vid: Long): Flow<List<Document>> = db.documentDao().getByVehicle(vid)
    fun expiringSoon(days: Int = 30): Flow<List<Document>> {
        val threshold = System.currentTimeMillis() + days * 86_400_000L
        return db.documentDao().getExpiringSoon(threshold)
    }

    suspend fun addVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().insert(v) }
    suspend fun updateVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().update(v) }
    suspend fun deleteVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().delete(v) }

    suspend fun addTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().insert(t) }
    suspend fun updateTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().update(t) }
    suspend fun deleteTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().delete(t) }

    suspend fun addDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().insert(d) }
    suspend fun updateDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().update(d) }
    suspend fun deleteDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().delete(d) }
}
