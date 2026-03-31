package com.fleet.ledger.core.domain.repository

import com.fleet.ledger.core.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    fun getDocumentsByVehicle(vehicleId: Long): Flow<List<Document>>
    fun getExpiringSoon(daysAhead: Int): Flow<List<Document>>
    suspend fun insertDocument(document: Document)
    suspend fun updateDocument(document: Document)
    suspend fun deleteDocument(document: Document)
}
