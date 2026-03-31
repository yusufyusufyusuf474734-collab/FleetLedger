package com.fleet.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fleet.ledger.core.data.local.FleetDatabase
import com.fleet.ledger.core.data.repository.*
import com.fleet.ledger.core.domain.usecase.*
import com.fleet.ledger.feature.dashboard.DashboardViewModel
import com.fleet.ledger.feature.vehicle.VehiclesViewModel
import com.fleet.ledger.feature.partner.PartnersViewModel
import com.fleet.ledger.feature.report.ReportsViewModel
import com.fleet.ledger.feature.document.DocumentsViewModel
import com.fleet.ledger.feature.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Manual DI - Production'da Hilt/Koin kullanılmalı
        val database = FleetDatabase.getInstance(applicationContext)
        
        // Repositories
        val vehicleRepository = VehicleRepositoryImpl(database.vehicleDao())
        val partnerRepository = PartnerRepositoryImpl(database.partnerDao())
        val tripRepository = TripRepositoryImpl(database.tripDao())
        val documentRepository = DocumentRepositoryImpl(database.documentDao())
        
        // Use Cases
        val getVehiclesUseCase = GetVehiclesUseCase(vehicleRepository)
        val addVehicleUseCase = AddVehicleUseCase(vehicleRepository)
        val getPartnersUseCase = GetPartnersUseCase(partnerRepository)
        val addPartnerUseCase = AddPartnerUseCase(partnerRepository)
        
        // ViewModels
        val dashboardViewModel = DashboardViewModel(
            getVehiclesUseCase,
            tripRepository,
            documentRepository
        )
        val vehiclesViewModel = VehiclesViewModel(getVehiclesUseCase, addVehicleUseCase)
        val partnersViewModel = PartnersViewModel(getPartnersUseCase, addPartnerUseCase)
        val reportsViewModel = ReportsViewModel()
        val documentsViewModel = DocumentsViewModel(documentRepository)
        val settingsViewModel = SettingsViewModel()
        
        setContent {
            FleetLedgerApp(
                dashboardViewModel = dashboardViewModel,
                vehiclesViewModel = vehiclesViewModel,
                partnersViewModel = partnersViewModel,
                reportsViewModel = reportsViewModel,
                documentsViewModel = documentsViewModel,
                settingsViewModel = settingsViewModel
            )
        }
    }
}
