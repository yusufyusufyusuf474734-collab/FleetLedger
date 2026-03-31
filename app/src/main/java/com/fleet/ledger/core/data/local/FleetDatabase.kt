package com.fleet.ledger.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fleet.ledger.core.data.local.dao.VehicleDao
import com.fleet.ledger.core.data.local.entity.VehicleEntity
import com.fleet.ledger.core.data.local.entity.TripEntity

@Database(
    entities = [
        VehicleEntity::class,
        TripEntity::class,
        com.fleet.ledger.core.data.local.entity.PartnerEntity::class,
        com.fleet.ledger.core.data.local.entity.DocumentEntity::class,
        com.fleet.ledger.core.data.local.entity.MonthlyExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FleetDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun tripDao(): com.fleet.ledger.core.data.local.dao.TripDao
    abstract fun partnerDao(): com.fleet.ledger.core.data.local.dao.PartnerDao
    abstract fun documentDao(): com.fleet.ledger.core.data.local.dao.DocumentDao
    abstract fun monthlyExpenseDao(): com.fleet.ledger.core.data.local.dao.MonthlyExpenseDao
    
    companion object {
        @Volatile
        private var INSTANCE: FleetDatabase? = null
        
        fun getInstance(context: Context): FleetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FleetDatabase::class.java,
                    "fleet_ledger_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
