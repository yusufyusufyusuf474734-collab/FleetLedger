package com.fleet.ledger.feature.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Maintenance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MaintenanceViewModel : ViewModel() {
    
    private val _maintenances = MutableStateFlow<List<Maintenance>>(emptyList())
    val maintenances: StateFlow<List<Maintenance>> = _maintenances.asStateFlow()
    
    private val _upcomingMaintenances = MutableStateFlow<List<Maintenance>>(emptyList())
    val upcomingMaintenances: StateFlow<List<Maintenance>> = _upcomingMaintenances.asStateFlow()
    
    // TODO: Implement actual data fetching
}
