package com.fleet.ledger.core.data.local.dao

import androidx.room.*
import com.fleet.ledger.core.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY date DESC")
    fun getAll(): Flow<List<TripEntity>>
    
    @Query("SELECT * FROM trips WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getByVehicle(vehicleId: Long): Flow<List<TripEntity>>
    
    @Query("SELECT * FROM trips WHERE vehicleId = :vehicleId AND date BETWEEN :from AND :to ORDER BY date DESC")
    fun getByVehicleAndRange(vehicleId: Long, from: Long, to: Long): Flow<List<TripEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity): Long
    
    @Update
    suspend fun update(trip: TripEntity)
    
    @Delete
    suspend fun delete(trip: TripEntity)
}
