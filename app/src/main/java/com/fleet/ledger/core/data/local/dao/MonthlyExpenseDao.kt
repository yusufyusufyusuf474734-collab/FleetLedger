package com.fleet.ledger.core.data.local.dao

import androidx.room.*
import com.fleet.ledger.core.data.local.entity.MonthlyExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyExpenseDao {
    @Query("SELECT * FROM monthly_expenses WHERE vehicleId = :vehicleId AND year = :year AND month = :month")
    fun getByVehicleAndMonth(vehicleId: Long, year: Int, month: Int): Flow<List<MonthlyExpenseEntity>>
    
    @Query("SELECT * FROM monthly_expenses WHERE year = :year AND month = :month")
    fun getByMonth(year: Int, month: Int): Flow<List<MonthlyExpenseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: MonthlyExpenseEntity): Long
    
    @Update
    suspend fun update(expense: MonthlyExpenseEntity)
    
    @Delete
    suspend fun delete(expense: MonthlyExpenseEntity)
}
