package com.fleet.ledger.core.data.local.dao

import androidx.room.*
import com.fleet.ledger.core.data.local.entity.PartnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartnerDao {
    @Query("SELECT * FROM partners ORDER BY name ASC")
    fun getAll(): Flow<List<PartnerEntity>>
    
    @Query("SELECT * FROM partners WHERE id = :id")
    fun getById(id: Long): Flow<PartnerEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(partner: PartnerEntity): Long
    
    @Update
    suspend fun update(partner: PartnerEntity)
    
    @Delete
    suspend fun delete(partner: PartnerEntity)
}
