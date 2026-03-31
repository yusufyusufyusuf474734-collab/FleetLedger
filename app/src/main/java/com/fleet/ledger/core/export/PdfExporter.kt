package com.fleet.ledger.core.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.fleet.ledger.core.domain.model.Trip
import com.fleet.ledger.core.domain.model.Vehicle
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {
    
    fun exportMonthlyReport(
        context: Context,
        vehicle: Vehicle,
        trips: List<Trip>,
        year: Int,
        month: Int
    ): Result<File> {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page = document.startPage(pageInfo)
            
            val canvas = page.canvas
            val paint = android.graphics.Paint()
            
            // Title
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("Aylık Rapor", 50f, 50f, paint)
            
            // Vehicle info
            paint.textSize = 16f
            paint.isFakeBoldText = false
            canvas.drawText("Araç: ${vehicle.plate}", 50f, 100f, paint)
            canvas.drawText("Dönem: $month/$year", 50f, 130f, paint)
            
            // Summary
            val totalIncome = trips.sumOf { it.income }
            val totalExpense = trips.sumOf { it.totalExpense }
            val netProfit = totalIncome - totalExpense
            
            canvas.drawText("Toplam Gelir: ₺${totalIncome.toInt()}", 50f, 180f, paint)
            canvas.drawText("Toplam Gider: ₺${totalExpense.toInt()}", 50f, 210f, paint)
            canvas.drawText("Net Kar: ₺${netProfit.toInt()}", 50f, 240f, paint)
            
            // Trips
            var y = 290f
            paint.textSize = 14f
            canvas.drawText("Seferler:", 50f, y, paint)
            y += 30f
            
            trips.forEach { trip ->
                val date = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(trip.date))
                canvas.drawText("$date - ${trip.description}", 50f, y, paint)
                y += 20f
                if (y > 800f) return@forEach // Page limit
            }
            
            document.finishPage(page)
            
            // Save file
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FiloTakip"
            )
            dir.mkdirs()
            
            val file = File(dir, "rapor_${vehicle.plate}_${month}_${year}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun exportTripReceipt(
        context: Context,
        trip: Trip,
        vehicle: Vehicle
    ): Result<File> {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            
            val canvas = page.canvas
            val paint = android.graphics.Paint()
            
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("Sefer Fişi", 50f, 50f, paint)
            
            paint.textSize = 14f
            paint.isFakeBoldText = false
            
            val date = SimpleDateFormat("dd.MM.yyyy", Locale("tr")).format(Date(trip.date))
            canvas.drawText("Tarih: $date", 50f, 100f, paint)
            canvas.drawText("Araç: ${vehicle.plate}", 50f, 130f, paint)
            canvas.drawText("Açıklama: ${trip.description}", 50f, 160f, paint)
            
            canvas.drawText("Gelir: ₺${trip.income.toInt()}", 50f, 210f, paint)
            canvas.drawText("Yakıt: ₺${trip.fuelCost.toInt()}", 50f, 240f, paint)
            canvas.drawText("Köprü: ₺${trip.bridgeCost.toInt()}", 50f, 270f, paint)
            canvas.drawText("Otoyol: ₺${trip.highwayCost.toInt()}", 50f, 300f, paint)
            canvas.drawText("Şoför: ₺${trip.driverFee.toInt()}", 50f, 330f, paint)
            canvas.drawText("Diğer: ₺${trip.otherCost.toInt()}", 50f, 360f, paint)
            
            paint.isFakeBoldText = true
            canvas.drawText("Net Kar: ₺${trip.netProfit.toInt()}", 50f, 410f, paint)
            
            document.finishPage(page)
            
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FiloTakip/Fişler"
            )
            dir.mkdirs()
            
            val file = File(dir, "fis_${trip.id}_${System.currentTimeMillis()}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
