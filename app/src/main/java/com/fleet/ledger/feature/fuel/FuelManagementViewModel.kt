package com.fleet.ledger.feature.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.FuelRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FuelManagementViewModel : ViewModel() {
    
    private val _fuelRecords = MutableStateFlow<List<FuelRecord>>(emptyList())
    val fuelRecords: StateFlow<List<FuelRecord>> = _fuelRecords.asStateFlow()
    
    private val _statistics = MutableStateFlow(FuelStatistics())
    val statistics: StateFlow<FuelStatistics> = _statistics.asStateFlow()
    
    // TODO: Implement actual data fetching
}

data class FuelStatistics(
    val averageConsumption: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalLiters: Double = 0.0,
    val averagePrice: Double = 0.0
)
