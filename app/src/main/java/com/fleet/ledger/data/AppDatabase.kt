package com.fleet.ledger.data

import android.content.Context
import androidx.room.*

@TypeConverters(Converters::class)
@Database(
    entities = [Vehicle::class, Partner::class, VehiclePartner::class, Trip::class, Document::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun partnerDao(): PartnerDao
    abstract fun tripDao(): TripDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "fleet_ledger.db")
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
        }
    }
}

class Converters {
    @TypeConverter fun fromDocType(v: DocumentType): String = v.name
    @TypeConverter fun toDocType(v: String): DocumentType = DocumentType.valueOf(v)
}
