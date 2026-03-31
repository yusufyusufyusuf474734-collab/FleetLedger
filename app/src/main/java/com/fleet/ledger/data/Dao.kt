package com.fleet.ledger.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY plate ASC")
    fun getAll(): Flow<List<Vehicle>>

    @Insert fun insert(v: Vehicle): Long
    @Update fun update(v: Vehicle)
    @Delete fun delete(v: Vehicle)
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE vehicleId = :vid ORDER BY date DESC")
    fun getByVehicle(vid: Long): Flow<List<Trip>>

    @Query("SELECT * FROM trips ORDER BY date DESC")
    fun getAll(): Flow<List<Trip>>

    @Query("""
        SELECT vehicleId,
               SUM(income) AS totalIncome,
               SUM(fuelCost + bridgeCost + highwayCost + driverFee + otherCost) AS totalExpense
        FROM trips GROUP BY vehicleId
    """)
    fun getSummaryPerVehicle(): Flow<List<VehicleSummary>>

    @Insert fun insert(t: Trip): Long
    @Update fun update(t: Trip)
    @Delete fun delete(t: Trip)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE vehicleId = :vid ORDER BY expiryDate ASC")
    fun getByVehicle(vid: Long): Flow<List<Document>>

    @Query("SELECT * FROM documents ORDER BY expiryDate ASC")
    fun getAll(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate <= :threshold ORDER BY expiryDate ASC")
    fun getExpiringSoon(threshold: Long): Flow<List<Document>>

    @Insert fun insert(d: Document): Long
    @Update fun update(d: Document)
    @Delete fun delete(d: Document)
}

data class VehicleSummary(
    val vehicleId: Long,
    val totalIncome: Double,
    val totalExpense: Double
) {
    val netProfit: Double get() = totalIncome - totalExpense
}
