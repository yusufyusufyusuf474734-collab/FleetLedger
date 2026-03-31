package com.fleet.ledger.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {
    val vehicles: Flow<List<Vehicle>> = db.vehicleDao().getAll()
    val summaries: Flow<List<VehicleSummary>> = db.tripDao().getSummaryPerVehicle()
    val allDocuments: Flow<List<Document>> = db.documentDao().getAll()
    val allPartners: Flow<List<Partner>> = db.partnerDao().getAll()

    fun tripsFor(vid: Long): Flow<List<Trip>> = db.tripDao().getByVehicle(vid)
    fun tripsInRange(vid: Long, from: Long, to: Long): Flow<List<Trip>> =
        db.tripDao().getByVehicleAndRange(vid, from, to)
    fun summariesInRange(from: Long, to: Long): Flow<List<VehicleSummary>> =
        db.tripDao().getSummaryPerVehicleInRange(from, to)
    fun documentsFor(vid: Long): Flow<List<Document>> = db.documentDao().getByVehicle(vid)
    fun expiringSoon(days: Int = 30): Flow<List<Document>> =
        db.documentDao().getExpiringSoon(System.currentTimeMillis() + days * 86_400_000L)
    fun partnersFor(vid: Long): Flow<List<Partner>> = db.partnerDao().getByVehicle(vid)
    fun sharesFor(vid: Long): Flow<List<VehiclePartner>> = db.partnerDao().getSharesByVehicle(vid)

    suspend fun addVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().insert(v) }
    suspend fun updateVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().update(v) }
    suspend fun deleteVehicle(v: Vehicle) = withContext(Dispatchers.IO) { db.vehicleDao().delete(v) }

    suspend fun addPartner(p: Partner) = withContext(Dispatchers.IO) { db.partnerDao().insert(p) }
    suspend fun updatePartner(p: Partner) = withContext(Dispatchers.IO) { db.partnerDao().update(p) }
    suspend fun deletePartner(p: Partner) = withContext(Dispatchers.IO) { db.partnerDao().delete(p) }
    suspend fun setVehiclePartner(vp: VehiclePartner) = withContext(Dispatchers.IO) { db.partnerDao().upsertVehiclePartner(vp) }
    suspend fun removeVehiclePartner(vid: Long, pid: Long) = withContext(Dispatchers.IO) { db.partnerDao().removeVehiclePartner(vid, pid) }

    suspend fun addTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().insert(t) }
    suspend fun updateTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().update(t) }
    suspend fun deleteTrip(t: Trip) = withContext(Dispatchers.IO) { db.tripDao().delete(t) }

    suspend fun addDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().insert(d) }
    suspend fun updateDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().update(d) }
    suspend fun deleteDocument(d: Document) = withContext(Dispatchers.IO) { db.documentDao().delete(d) }
}
