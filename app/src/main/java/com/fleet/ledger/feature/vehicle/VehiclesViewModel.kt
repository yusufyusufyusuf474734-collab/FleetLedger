package com.fleet.ledger.feature.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.usecase.AddVehicleUseCase
import com.fleet.ledger.core.domain.usecase.GetVehiclesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehiclesViewModel(
    getVehiclesUseCase: GetVehiclesUseCase,
    private val addVehicleUseCase: AddVehicleUseCase
) : ViewModel() {
    
    val vehicles: StateFlow<List<Vehicle>> = getVehiclesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun addVehicle(plate: String, name: String, brand: String, year: Int) {
        viewModelScope.launch {
            addVehicleUseCase(
                Vehicle(
                    plate = plate.uppercase().trim(),
                    name = name.trim(),
                    brand = brand.trim(),
                    year = year
                )
            )
        }
    }
}
