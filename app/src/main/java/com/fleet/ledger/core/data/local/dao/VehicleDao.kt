package com.fleet.ledger.core.data.local.dao

import androidx.room.*
import com.fleet.ledger.core.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY createdAt DESC")
    fun getAll(): Flow<List<VehicleEntity>>
    
    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getById(id: Long): Flow<VehicleEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity): Long
    
    @Update
    suspend fun update(vehicle: VehicleEntity)
    
    @Delete
    suspend fun delete(vehicle: VehicleEntity)
}
