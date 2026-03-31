package com.fleet.ledger.core.data.repository

import com.fleet.ledger.core.data.local.dao.DocumentDao
import com.fleet.ledger.core.data.local.entity.toDomain
import com.fleet.ledger.core.data.local.entity.toEntity
import com.fleet.ledger.core.domain.model.Document
import com.fleet.ledger.core.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao
) : DocumentRepository {
    
    override fun getAllDocuments(): Flow<List<Document>> =
        documentDao.getAll().map { entities -> entities.map { it.toDomain() } }
    
    override fun getDocumentsByVehicle(vehicleId: Long): Flow<List<Document>> =
        documentDao.getByVehicle(vehicleId).map { entities -> entities.map { it.toDomain() } }
    
    override fun getExpiringSoon(daysAhead: Int): Flow<List<Document>> {
        val deadline = System.currentTimeMillis() + (daysAhead * 24 * 60 * 60 * 1000L)
        return documentDao.getExpiringSoon(deadline).map { entities -> entities.map { it.toDomain() } }
    }
    
    override suspend fun insertDocument(document: Document) {
        documentDao.insert(document.toEntity())
    }
    
    override suspend fun updateDocument(document: Document) {
        documentDao.update(document.toEntity())
    }
    
    override suspend fun deleteDocument(document: Document) {
        documentDao.delete(document.toEntity())
    }
}
