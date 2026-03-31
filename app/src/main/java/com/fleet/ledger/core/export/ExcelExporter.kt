package com.fleet.ledger.core.export

import android.content.Context
import android.os.Environment
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.model.Vehicle
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExcelExporter {
    
    fun exportTripsToCSV(
        context: Context,
        vehicle: Vehicle,
        trips: List<Trip>
    ): Result<File> {
        return try {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FiloTakip"
            )
            dir.mkdirs()
            
            val file = File(dir, "seferler_${vehicle.plate}_${System.currentTimeMillis()}.csv")
            val writer = FileWriter(file)
            
            // Header
            writer.append("Tarih,Açıklama,Gelir,Yakıt,Köprü,Otoyol,Şoför,Diğer,Toplam Gider,Net Kar\n")
            
            // Data
            trips.forEach { trip ->
                val date = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(trip.date))
                writer.append("$date,")
                writer.append("\"${trip.description}\",")
                writer.append("${trip.income},")
                writer.append("${trip.fuelCost},")
                writer.append("${trip.bridgeCost},")
                writer.append("${trip.highwayCost},")
                writer.append("${trip.driverFee},")
                writer.append("${trip.otherCost},")
                writer.append("${trip.totalExpense},")
                writer.append("${trip.netProfit}\n")
            }
            
            writer.flush()
            writer.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun exportAllDataToCSV(
        context: Context,
        vehicles: List<Vehicle>,
        allTrips: Map<Long, List<Trip>>
    ): Result<File> {
        return try {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FiloTakip"
            )
            dir.mkdirs()
            
            val file = File(dir, "tum_veriler_${System.currentTimeMillis()}.csv")
            val writer = FileWriter(file)
            
            writer.append("Plaka,Tarih,Açıklama,Gelir,Gider,Kar\n")
            
            vehicles.forEach { vehicle ->
                val trips = allTrips[vehicle.id] ?: emptyList()
                trips.forEach { trip ->
                    val date = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(trip.date))
                    writer.append("${vehicle.plate},")
                    writer.append("$date,")
                    writer.append("\"${trip.description}\",")
                    writer.append("${trip.income},")
                    writer.append("${trip.totalExpense},")
                    writer.append("${trip.netProfit}\n")
                }
            }
            
            writer.flush()
            writer.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
