package com.fleet.ledger.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fleet.ledger.MainActivity
import com.fleet.ledger.R

object AppNotificationManager {
    
    private const val CHANNEL_ID = "fleet_ledger_channel"
    private const val CHANNEL_NAME = "FiloTakip Bildirimleri"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Belge hatırlatmaları ve önemli bildirimler"
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showDocumentExpiryNotification(
        context: Context,
        documentTitle: String,
        daysLeft: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Belge Son Tarihi Yaklaşıyor")
            .setContentText("$documentTitle - $daysLeft gün kaldı")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            documentTitle.hashCode(),
            notification
        )
    }
    
    fun showMaintenanceReminder(
        context: Context,
        vehiclePlate: String,
        maintenanceType: String
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Bakım Zamanı")
            .setContentText("$vehiclePlate - $maintenanceType bakımı gerekiyor")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            vehiclePlate.hashCode(),
            notification
        )
    }
    
    fun showDailySummary(
        context: Context,
        tripCount: Int,
        totalIncome: Double
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Günlük Özet")
            .setContentText("Bugün $tripCount sefer, ₺${totalIncome.toInt()} gelir")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }
}
