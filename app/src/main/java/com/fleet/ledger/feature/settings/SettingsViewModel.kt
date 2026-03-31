package com.fleet.ledger.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    private val _currency = MutableStateFlow("TRY (₺)")
    val currency: StateFlow<String> = _currency.asStateFlow()
    
    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        // TODO: Save to preferences
    }
    
    fun setCurrency(currency: String) {
        _currency.value = currency
        // TODO: Save to preferences
    }
}
