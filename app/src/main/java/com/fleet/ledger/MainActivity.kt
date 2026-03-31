package com.fleet.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fleet.ledger.core.data.local.FleetDatabase
import com.fleet.ledger.core.data.repository.VehicleRepositoryImpl
import com.fleet.ledger.core.data.repository.PartnerRepositoryImpl
import com.fleet.ledger.core.domain.usecase.*
import com.fleet.ledger.feature.dashboard.DashboardViewModel
import com.fleet.ledger.feature.vehicle.VehiclesViewModel
import com.fleet.ledger.feature.partner.PartnersViewModel
import com.fleet.ledger.feature.report.ReportsViewModel

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual DI - Production'da Hilt/Koin kullanılmalı
        val database = FleetDatabase.getInstance(applicationContext)
        
        // Repositories
        val vehicleRepository = VehicleRepositoryImpl(database.vehicleDao())
        val partnerRepository = PartnerRepositoryImpl(database.partnerDao())
        
        // Use Cases
        val getVehiclesUseCase = GetVehiclesUseCase(vehicleRepository)
        val addVehicleUseCase = AddVehicleUseCase(vehicleRepository)
        val getPartnersUseCase = GetPartnersUseCase(partnerRepository)
        val addPartnerUseCase = AddPartnerUseCase(partnerRepository)
        
        // ViewModels
        val dashboardViewModel = DashboardViewModel(getVehiclesUseCase)
        val vehiclesViewModel = VehiclesViewModel(getVehiclesUseCase, addVehicleUseCase)
        val partnersViewModel = PartnersViewModel(getPartnersUseCase, addPartnerUseCase)
        val reportsViewModel = ReportsViewModel()
        
        setContent {
            FleetLedgerApp(
                dashboardViewModel = dashboardViewModel,
                vehiclesViewModel = vehiclesViewModel,
                partnersViewModel = partnersViewModel,
                reportsViewModel = reportsViewModel
            )
        }
    }
}
