package com.fleet.ledger.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.fleet.ledger.data.*
import com.fleet.ledger.ui.tl
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val monthNames = listOf("","Ocak","Şubat","Mart","Nisan","Mayıs","Haziran",
        "Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık")

    // Renkler
    private val colorPrimary = Color.rgb(59, 130, 246)   // Blue500
    private val colorGreen   = Color.rgb(16, 185, 129)
    private val colorRed     = Color.rgb(239, 68, 68)
    private val colorBg      = Color.rgb(13, 19, 33)
    private val colorSurface = Color.rgb(28, 35, 51)
    private val colorText    = Color.rgb(240, 244, 250)
    private val colorMuted   = Color.rgb(176, 186, 200)
    private val colorLine    = Color.rgb(46, 61, 85)

    fun generateMonthlyReport(
        context: Context,
        year: Int,
        month: Int,
        vehicles: List<Vehicle>,
        summaries: List<VehicleSummary>,
        monthlyExpenses: List<MonthlyExpense>,
        partners: List<Partner>,
        shares: Map<Long, List<VehiclePartner>>
    ): File {
        val doc = PdfDocument()
        val pageWidth = 595; val pageHeight = 842  // A4
        val page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas

        // Arka plan
        canvas.drawColor(colorBg)

        var y = 0f

        // Header
        y = drawHeader(canvas, "FiloTakip — Aylık Rapor",
            "${monthNames[month]} $year", pageWidth, y)

        // Genel özet
        val totalIncome  = summaries.sumOf { it.totalIncome }
        val totalExpense = summaries.sumOf { it.totalExpense }
        val totalMonthlyExp = monthlyExpenses.sumOf { it.amount }
        val totalNet = totalIncome - totalExpense - totalMonthlyExp

        y = drawSectionTitle(canvas, "GENEL ÖZET", pageWidth, y)
        y = drawKpiRow(canvas, listOf(
            "Toplam Gelir" to totalIncome.tl(),
            "Sefer Gideri" to totalExpense.tl(),
            "Sabit Gider"  to totalMonthlyExp.tl(),
            "Net Kâr"      to totalNet.tl()
        ), pageWidth, y, totalNet)

        // Araç bazlı
        vehicles.forEach { vehicle ->
            val s = summaries.find { it.vehicleId == vehicle.id }
            val expenses = monthlyExpenses.filter { it.vehicleId == vehicle.id }
            val vehicleShares = shares[vehicle.id] ?: emptyList()
            val expTotal = expenses.sumOf { it.amount }
            val net = (s?.netProfit ?: 0.0) - expTotal

            if (y > pageHeight - 120) {
                doc.finishPage(page)
                val p2 = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, doc.pages.size + 1).create())
                // yeni sayfa için canvas değişkeni gerekiyor ama PdfDocument API'si bunu desteklemiyor
                // Bu yüzden tek sayfada devam ediyoruz
            }

            y = drawSectionTitle(canvas, "${vehicle.plate} — ${vehicle.name}", pageWidth, y)
            y = drawKpiRow(canvas, listOf(
                "Gelir"       to (s?.totalIncome ?: 0.0).tl(),
                "Sefer Gid."  to (s?.totalExpense ?: 0.0).tl(),
                "Sabit Gid."  to expTotal.tl(),
                "Net"         to net.tl()
            ), pageWidth, y, net)

            // Sabit giderler
            if (expenses.isNotEmpty()) {
                expenses.forEach { exp ->
                    y = drawRow(canvas, exp.label, exp.amount.tl(), pageWidth, y, colorMuted)
                }
            }

            // Ortaklık payları
            if (vehicleShares.isNotEmpty()) {
                y = drawSmallTitle(canvas, "Ortaklık Payları", pageWidth, y)
                vehicleShares.forEach { share ->
                    val partner = partners.find { it.id == share.partnerId }
                    val partnerNet = net * share.sharePercent / 100.0
                    y = drawRow(canvas,
                        "${partner?.name ?: "?"} (%${share.sharePercent.toInt()})",
                        partnerNet.tl(), pageWidth, y,
                        if (partnerNet >= 0) colorGreen else colorRed)
                }
            }

            y += 8f
        }

        // Footer
        drawFooter(canvas, pageWidth, pageHeight)

        doc.finishPage(page)

        val file = File(context.cacheDir, "FiloTakip_${monthNames[month]}_$year.pdf")
        file.outputStream().use { doc.writeTo(it) }
        doc.close()
        return file
    }

    fun shareFile(context: Context, file: File, title: String = "Raporu Paylaş") {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    // ── Çizim yardımcıları ────────────────────────────────────────────────────

    private fun drawHeader(canvas: Canvas, title: String, subtitle: String, w: Int, startY: Float): Float {
        val paint = Paint().apply { isAntiAlias = true }
        // Header bg
        paint.color = colorSurface
        canvas.drawRect(0f, 0f, w.toFloat(), 70f, paint)
        // Accent çizgi
        paint.color = colorPrimary
        canvas.drawRect(0f, 68f, w.toFloat(), 70f, paint)
        // Başlık
        paint.color = colorText
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText(title, 24f, 32f, paint)
        // Alt başlık
        paint.color = colorMuted
        paint.textSize = 11f
        paint.isFakeBoldText = false
        canvas.drawText(subtitle, 24f, 52f, paint)
        // Tarih
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(sdf.format(Date()), (w - 24).toFloat(), 52f, paint)
        paint.textAlign = Paint.Align.LEFT
        return 86f
    }

    private fun drawSectionTitle(canvas: Canvas, text: String, w: Int, y: Float): Float {
        val paint = Paint().apply { isAntiAlias = true }
        paint.color = colorPrimary
        canvas.drawRect(24f, y, 28f, y + 14f, paint)
        paint.color = colorText
        paint.textSize = 11f
        paint.isFakeBoldText = true
        canvas.drawText(text, 34f, y + 11f, paint)
        paint.color = colorLine
        paint.strokeWidth = 0.5f
        canvas.drawLine(24f, y + 18f, (w - 24).toFloat(), y + 18f, paint)
        return y + 26f
    }

    private fun drawSmallTitle(canvas: Canvas, text: String, w: Int, y: Float): Float {
        val paint = Paint().apply { isAntiAlias = true; color = colorMuted; textSize = 9f }
        canvas.drawText(text.uppercase(), 34f, y + 9f, paint)
        return y + 16f
    }

    private fun drawKpiRow(canvas: Canvas, items: List<Pair<String, String>>, w: Int, y: Float, net: Double): Float {
        val paint = Paint().apply { isAntiAlias = true }
        val colW = (w - 48f) / items.size
        items.forEachIndexed { i, (label, value) ->
            val x = 24f + i * colW
            // Kart bg
            paint.color = colorSurface
            canvas.drawRoundRect(x + 2, y, x + colW - 4, y + 36f, 6f, 6f, paint)
            // Değer
            val isNet = label == "Net" || label == "Net Kâr"
            paint.color = if (isNet) (if (net >= 0) colorGreen else colorRed) else colorText
            paint.textSize = 12f
            paint.isFakeBoldText = true
            canvas.drawText(value, x + 8f, y + 20f, paint)
            // Etiket
            paint.color = colorMuted
            paint.textSize = 8f
            paint.isFakeBoldText = false
            canvas.drawText(label, x + 8f, y + 32f, paint)
        }
        return y + 44f
    }

    private fun drawRow(canvas: Canvas, label: String, value: String, w: Int, y: Float, valueColor: Int): Float {
        val paint = Paint().apply { isAntiAlias = true }
        paint.color = colorMuted
        paint.textSize = 9f
        canvas.drawText(label, 40f, y + 9f, paint)
        paint.color = valueColor
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(value, (w - 24).toFloat(), y + 9f, paint)
        paint.textAlign = Paint.Align.LEFT
        paint.color = colorLine
        paint.strokeWidth = 0.3f
        canvas.drawLine(40f, y + 12f, (w - 24).toFloat(), y + 12f, paint)
        return y + 16f
    }

    private fun drawFooter(canvas: Canvas, w: Int, h: Int) {
        val paint = Paint().apply { isAntiAlias = true; color = colorLine; strokeWidth = 0.5f }
        canvas.drawLine(24f, (h - 30).toFloat(), (w - 24).toFloat(), (h - 30).toFloat(), paint)
        paint.color = colorMuted
        paint.textSize = 8f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("FiloTakip — Araç Filo Yönetim Sistemi", (w / 2).toFloat(), (h - 16).toFloat(), paint)
    }
}
