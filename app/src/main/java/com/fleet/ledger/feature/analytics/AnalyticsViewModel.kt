package com.fleet.ledger.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.repository.TripRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {
    
    val incomeData: StateFlow<List<Pair<String, Float>>> = flow {
        // TODO: Implement actual data fetching
        emit(
            listOf(
                "Oca" to 50000f,
                "Şub" to 65000f,
                "Mar" to 58000f,
                "Nis" to 72000f,
                "May" to 68000f,
                "Haz" to 75000f
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val expenseBreakdown: StateFlow<List<Pair<String, Float>>> = flow {
        emit(
            listOf(
                "Yakıt" to 35000f,
                "Köprü" to 5000f,
                "Otoyol" to 8000f,
                "Şoför" to 15000f,
                "Diğer" to 7000f
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val vehiclePerformance: StateFlow<List<Pair<String, Float>>> = flow {
        emit(
            listOf(
                "34ABC123" to 25000f,
                "06XYZ456" to 32000f,
                "35DEF789" to 28000f,
                "16GHI012" to 30000f
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
