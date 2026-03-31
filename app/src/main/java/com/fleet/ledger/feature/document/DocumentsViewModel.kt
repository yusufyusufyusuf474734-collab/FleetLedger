package com.fleet.ledger.feature.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleet.ledger.core.domain.model.Document
import com.fleet.ledger.core.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DocumentsViewModel(
    documentRepository: DocumentRepository
) : ViewModel() {
    
    val documents: StateFlow<List<Document>> = documentRepository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val expiringSoon: StateFlow<List<Document>> = documentRepository.getExpiringSoon(30)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
