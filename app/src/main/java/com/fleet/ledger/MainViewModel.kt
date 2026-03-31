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

    fun tripsFor(vehicleId: Long): Flow<List<Trip>> = repo.tripsForVehicle(vehicleId)

    fun addVehicle(plate: String, name: String) = viewModelScope.launch {
        repo.addVehicle(Vehicle(plate = plate.uppercase().trim(), name = name.trim()))
    }
    fun updateVehicle(v: Vehicle) = viewModelScope.launch { repo.updateVehicle(v) }
    fun deleteVehicle(v: Vehicle) = viewModelScope.launch { repo.deleteVehicle(v) }

    fun addTrip(t: Trip) = viewModelScope.launch { repo.addTrip(t) }
    fun updateTrip(t: Trip) = viewModelScope.launch { repo.updateTrip(t) }
    fun deleteTrip(t: Trip) = viewModelScope.launch { repo.deleteTrip(t) }

    fun summaryFor(vehicleId: Long): VehicleSummary? =
        summaries.value.find { it.vehicleId == vehicleId }
}
