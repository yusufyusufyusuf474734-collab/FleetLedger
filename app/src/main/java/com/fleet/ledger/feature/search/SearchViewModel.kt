package com.fleet.ledger.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Trip
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _filters = MutableStateFlow(SearchFilters())
    val filters: StateFlow<SearchFilters> = _filters.asStateFlow()
    
    val searchResults: StateFlow<List<Trip>> = combine(
        _searchQuery,
        _filters
    ) { query, filters ->
        // TODO: Implement actual search with filters
        emptyList<Trip>()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun search(query: String) {
        _searchQuery.value = query
    }
    
    fun updateFilters(filters: SearchFilters) {
        _filters.value = filters
    }
}

data class SearchFilters(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val showIncome: Boolean = true,
    val showExpense: Boolean = true,
    val showProfit: Boolean = true,
    val vehicleIds: List<Long> = emptyList()
)
