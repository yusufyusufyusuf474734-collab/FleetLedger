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
interface PartnerDao {
    @Query("SELECT * FROM partners ORDER BY name ASC")
    fun getAll(): Flow<List<Partner>>

    @Query("""
        SELECT p.* FROM partners p
        INNER JOIN vehicle_partners vp ON p.id = vp.partnerId
        WHERE vp.vehicleId = :vid
    """)
    fun getByVehicle(vid: Long): Flow<List<Partner>>

    @Query("SELECT * FROM vehicle_partners WHERE vehicleId = :vid")
    fun getSharesByVehicle(vid: Long): Flow<List<VehiclePartner>>

    @Insert fun insert(p: Partner): Long
    @Update fun update(p: Partner)
    @Delete fun delete(p: Partner)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertVehiclePartner(vp: VehiclePartner)

    @Query("DELETE FROM vehicle_partners WHERE vehicleId = :vid AND partnerId = :pid")
    fun removeVehiclePartner(vid: Long, pid: Long)

    @Query("DELETE FROM vehicle_partners WHERE vehicleId = :vid")
    fun removeAllPartnersFromVehicle(vid: Long)
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE vehicleId = :vid ORDER BY date DESC")
    fun getByVehicle(vid: Long): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE vehicleId = :vid AND date BETWEEN :from AND :to ORDER BY date DESC")
    fun getByVehicleAndRange(vid: Long, from: Long, to: Long): Flow<List<Trip>>

    @Query("SELECT * FROM trips ORDER BY date DESC")
    fun getAll(): Flow<List<Trip>>

    @Query("""
        SELECT vehicleId,
               SUM(income) AS totalIncome,
               SUM(fuelCost + bridgeCost + highwayCost + driverFee + otherCost) AS totalExpense
        FROM trips GROUP BY vehicleId
    """)
    fun getSummaryPerVehicle(): Flow<List<VehicleSummary>>

    @Query("""
        SELECT vehicleId,
               SUM(income) AS totalIncome,
               SUM(fuelCost + bridgeCost + highwayCost + driverFee + otherCost) AS totalExpense
        FROM trips
        WHERE date BETWEEN :from AND :to
        GROUP BY vehicleId
    """)
    fun getSummaryPerVehicleInRange(from: Long, to: Long): Flow<List<VehicleSummary>>

    @Insert fun insert(t: Trip): Long
    @Update fun update(t: Trip)
    @Delete fun delete(t: Trip)
}

@Dao
interface MonthlyExpenseDao {
    @Query("SELECT * FROM monthly_expenses WHERE vehicleId = :vid AND year = :y AND month = :m ORDER BY label ASC")
    fun getByVehicleAndMonth(vid: Long, y: Int, m: Int): Flow<List<MonthlyExpense>>

    @Query("SELECT * FROM monthly_expenses WHERE year = :y AND month = :m ORDER BY vehicleId, label ASC")
    fun getByMonth(y: Int, m: Int): Flow<List<MonthlyExpense>>

    @Insert fun insert(e: MonthlyExpense): Long
    @Update fun update(e: MonthlyExpense)
    @Delete fun delete(e: MonthlyExpense)
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
