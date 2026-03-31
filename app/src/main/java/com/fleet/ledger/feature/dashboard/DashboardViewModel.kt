package com.fleet.ledger.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Vehicle
import com.fleet.ledger.core.domain.usecase.GetVehiclesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    getVehiclesUseCase: GetVehiclesUseCase
) : ViewModel() {
    
    val vehicles: StateFlow<List<Vehicle>> = getVehiclesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
