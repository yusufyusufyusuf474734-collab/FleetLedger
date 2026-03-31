package com.fleet.ledger.feature.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.model.VehicleSummary
import com.fleet.ledger.core.domain.repository.TripRepository
import com.fleet.ledger.core.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleRepository: VehicleRepository,
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _vehicleId = MutableStateFlow<Long?>(null)
    
    val vehicle: StateFlow<Vehicle?> = _vehicleId
        .filterNotNull()
        .flatMapLatest { vehicleRepository.getVehicleById(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val trips: StateFlow<List<Trip>> = _vehicleId
        .filterNotNull()
        .flatMapLatest { tripRepository.getTripsByVehicle(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val summary: StateFlow<VehicleSummary> = trips.map { tripList ->
        VehicleSummary(
            vehicleId = _vehicleId.value ?: 0,
            vehiclePlate = vehicle.value?.plate ?: "",
            totalIncome = tripList.sumOf { it.income },
            totalExpense = tripList.sumOf { it.totalExpense },
            netProfit = tripList.sumOf { it.netProfit },
            tripCount = tripList.size,
            lastTripDate = tripList.maxOfOrNull { it.date }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        VehicleSummary(0, "", 0.0, 0.0, 0.0, 0, null)
    )
    
    fun loadVehicle(vehicleId: Long) {
        _vehicleId.value = vehicleId
    }
    
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            tripRepository.insertTrip(trip)
        }
    }
}
