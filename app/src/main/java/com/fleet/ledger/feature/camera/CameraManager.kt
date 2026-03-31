package com.fleet.ledger.feature.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CameraManager {
    
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.filesDir, "receipts")
        storageDir.mkdirs()
        
        return File(storageDir, "RECEIPT_${timeStamp}.jpg")
    }
    
    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    fun saveReceiptImage(
        context: Context,
        tripId: Long,
        imageFile: File
    ): String {
        val destDir = File(context.filesDir, "receipts/$tripId")
        destDir.mkdirs()
        
        val destFile = File(destDir, imageFile.name)
        imageFile.copyTo(destFile, overwrite = true)
        
        return destFile.absolutePath
    }
    
    fun getReceiptImages(context: Context, tripId: Long): List<File> {
        val dir = File(context.filesDir, "receipts/$tripId")
        return dir.listFiles()?.toList() ?: emptyList()
    }
    
    fun deleteReceiptImage(imagePath: String): Boolean {
        return try {
            File(imagePath).delete()
        } catch (e: Exception) {
            false
        }
    }
}
