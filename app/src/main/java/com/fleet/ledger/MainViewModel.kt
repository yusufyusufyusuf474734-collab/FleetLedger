package com.fleet.ledger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    fun tripsFor(vid: Long): Flow<List<Trip>> = repo.tripsFor(vid)
    fun documentsFor(vid: Long): Flow<List<Document>> = repo.documentsFor(vid)
    fun summaryFor(vid: Long): VehicleSummary? = summaries.value.find { it.vehicleId == vid }
    fun vehicleById(id: Long): Vehicle? = vehicles.value.find { it.id == id }

    // Araç
    fun addVehicle(plate: String, name: String, brand: String, year: Int) = viewModelScope.launch {
        repo.addVehicle(Vehicle(plate = plate.uppercase().trim(), name = name.trim(),
            brand = brand.trim(), year = year))
    }
    fun updateVehicle(v: Vehicle) = viewModelScope.launch { repo.updateVehicle(v) }
    fun deleteVehicle(v: Vehicle) = viewModelScope.launch { repo.deleteVehicle(v) }

    // Sefer
    fun addTrip(t: Trip) = viewModelScope.launch { repo.addTrip(t) }
    fun updateTrip(t: Trip) = viewModelScope.launch { repo.updateTrip(t) }
    fun deleteTrip(t: Trip) = viewModelScope.launch { repo.deleteTrip(t) }

    // Belge
    fun addDocument(d: Document) = viewModelScope.launch { repo.addDocument(d) }
    fun updateDocument(d: Document) = viewModelScope.launch { repo.updateDocument(d) }
    fun deleteDocument(d: Document) = viewModelScope.launch { repo.deleteDocument(d) }
}
