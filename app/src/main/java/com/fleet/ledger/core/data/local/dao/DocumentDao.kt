package com.fleet.ledger.core.data.local.dao

import androidx.room.*
import com.fleet.ledger.core.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY expiryDate ASC")
    fun getAll(): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE vehicleId = :vehicleId ORDER BY expiryDate ASC")
    fun getByVehicle(vehicleId: Long): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate <= :deadline ORDER BY expiryDate ASC")
    fun getExpiringSoon(deadline: Long): Flow<List<DocumentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long
    
    @Update
    suspend fun update(document: DocumentEntity)
    
    @Delete
    suspend fun delete(document: DocumentEntity)
}
