package com.fleet.ledger.core.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class DocumentExpiryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        // TODO: Check documents and send notifications
        return Result.success()
    }
    
    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<DocumentExpiryWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "document_expiry_check",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
