package com.fleet.ledger.feature.route

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouteMapViewModel : ViewModel() {
    
    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()
    
    private val _selectedRoute = MutableStateFlow<Route?>(null)
    val selectedRoute: StateFlow<Route?> = _selectedRoute.asStateFlow()
    
    fun selectRoute(route: Route) {
        _selectedRoute.value = route
    }
    
    // TODO: Google Maps API entegrasyonu
    // - Rota çizimi
    // - Mesafe hesaplama
    // - Trafik bilgisi
    // - Alternatif rotalar
}
