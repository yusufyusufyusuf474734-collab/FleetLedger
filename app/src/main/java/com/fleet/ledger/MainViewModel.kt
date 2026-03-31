package com.fleet.ledger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(AppDatabase.get(app))

    val vehicles: StateFlow<List<Vehicle>> = repo.vehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val summaries: StateFlow<List<VehicleSummary>> = repo.summaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDocuments: StateFlow<List<Document>> = repo.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val expiringSoon: StateFlow<List<Document>> = repo.expiringSoon(30)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPartners: StateFlow<List<Partner>> = repo.allPartners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun tripsFor(vid: Long): Flow<List<Trip>> = repo.tripsFor(vid)
    fun tripsInRange(vid: Long, from: Long, to: Long): Flow<List<Trip>> = repo.tripsInRange(vid, from, to)
    fun summariesInRange(from: Long, to: Long): Flow<List<VehicleSummary>> = repo.summariesInRange(from, to)
    fun documentsFor(vid: Long): Flow<List<Document>> = repo.documentsFor(vid)
    fun partnersFor(vid: Long): Flow<List<Partner>> = repo.partnersFor(vid)
    fun sharesFor(vid: Long): Flow<List<VehiclePartner>> = repo.sharesFor(vid)
    fun vehicleById(id: Long): Vehicle? = vehicles.value.find { it.id == id }
    fun summaryFor(vid: Long): VehicleSummary? = summaries.value.find { it.vehicleId == vid }

    // Araç
    fun addVehicle(plate: String, name: String, brand: String, year: Int) = viewModelScope.launch {
        repo.addVehicle(Vehicle(plate = plate.uppercase().trim(), name = name.trim(), brand = brand.trim(), year = year))
    }
    fun updateVehicle(v: Vehicle) = viewModelScope.launch { repo.updateVehicle(v) }
    fun deleteVehicle(v: Vehicle) = viewModelScope.launch { repo.deleteVehicle(v) }

    // Ortak
    fun addPartner(name: String, phone: String, note: String) = viewModelScope.launch {
        repo.addPartner(Partner(name = name.trim(), phone = phone.trim(), note = note.trim()))
    }
    fun updatePartner(p: Partner) = viewModelScope.launch { repo.updatePartner(p) }
    fun deletePartner(p: Partner) = viewModelScope.launch { repo.deletePartner(p) }
    fun setVehiclePartner(vehicleId: Long, partnerId: Long, share: Double) = viewModelScope.launch {
        repo.setVehiclePartner(VehiclePartner(vehicleId, partnerId, share))
    }
    fun removeVehiclePartner(vehicleId: Long, partnerId: Long) = viewModelScope.launch {
        repo.removeVehiclePartner(vehicleId, partnerId)
    }

    // Sefer
    fun addTrip(t: Trip) = viewModelScope.launch { repo.addTrip(t) }
    fun updateTrip(t: Trip) = viewModelScope.launch { repo.updateTrip(t) }
    fun deleteTrip(t: Trip) = viewModelScope.launch { repo.deleteTrip(t) }

    // Belge
    fun addDocument(d: Document) = viewModelScope.launch { repo.addDocument(d) }
    fun updateDocument(d: Document) = viewModelScope.launch { repo.updateDocument(d) }
    fun deleteDocument(d: Document) = viewModelScope.launch { repo.deleteDocument(d) }

    // Ay sonu hesabı yardımcısı
    fun monthRange(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0)
        val from = cal.timeInMillis
        cal.set(year, month - 1, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        val to = cal.timeInMillis
        return from to to
    }
}
