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
        TripEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FleetDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    
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
