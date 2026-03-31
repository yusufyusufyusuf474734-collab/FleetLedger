package com.fleet.ledger.feature.partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Partner
import com.fleet.ledger.core.domain.usecase.AddPartnerUseCase
import com.fleet.ledger.core.domain.usecase.GetPartnersUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PartnersViewModel(
    getPartnersUseCase: GetPartnersUseCase,
    private val addPartnerUseCase: AddPartnerUseCase
) : ViewModel() {
    
    val partners: StateFlow<List<Partner>> = getPartnersUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun addPartner(name: String, phone: String, note: String) {
        viewModelScope.launch {
            addPartnerUseCase(
                Partner(
                    name = name.trim(),
                    phone = phone.trim(),
                    note = note.trim()
                )
            )
        }
    }
}
