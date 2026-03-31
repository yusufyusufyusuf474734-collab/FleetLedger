package com.fleet.ledger.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.model.VehicleSummary
import com.fleet.ledger.core.domain.model.Document
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.repository.TripRepository
import com.fleet.ledger.core.domain.repository.DocumentRepository
import com.fleet.ledger.core.domain.usecase.GetVehiclesUseCase
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    getVehiclesUseCase: GetVehiclesUseCase,
    private val tripRepository: com.fleet.ledger.core.domain.repository.TripRepository,
    private val documentRepository: com.fleet.ledger.core.domain.repository.DocumentRepository
) : ViewModel() {
    
    val vehicles: StateFlow<List<Vehicle>> = getVehiclesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val summaries: StateFlow<List<com.fleet.ledger.core.domain.model.VehicleSummary>> = 
        vehicles.flatMapLatest { vehicleList ->
            combine(vehicleList.map { vehicle ->
                tripRepository.getTripsByVehicle(vehicle.id).map { trips ->
                    com.fleet.ledger.core.domain.model.VehicleSummary(
                        vehicleId = vehicle.id,
                        vehiclePlate = vehicle.plate,
                        totalIncome = trips.sumOf { it.income },
                        totalExpense = trips.sumOf { it.totalExpense },
                        netProfit = trips.sumOf { it.netProfit },
                        tripCount = trips.size,
                        lastTripDate = trips.maxOfOrNull { it.date }
                    )
                }
            }) { it.toList() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val recentTrips: StateFlow<List<com.fleet.ledger.core.domain.model.Trip>> =
        vehicles.flatMapLatest { vehicleList ->
            if (vehicleList.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(vehicleList.map { tripRepository.getTripsByVehicle(it.id) }) { arrays ->
                    arrays.flatMap { it }.sortedByDescending { it.date }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val expiringSoon: StateFlow<List<com.fleet.ledger.core.domain.model.Document>> =
        documentRepository.getExpiringSoon(30)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
