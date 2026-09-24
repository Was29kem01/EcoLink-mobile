package com.project.ecolink.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.ecolink.data.local.AppDatabase
import kotlinx.coroutines.delay

class ReportSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.reportDao()

        // Fetch all pending reports synchronously (not a Flow, just a single list)
        val pendingReports = dao.getAllReportsSync()

        if (pendingReports.isEmpty()) {
            return Result.success()
        }

        var hasFailures = false

        for (report in pendingReports) {
            try {
                // Simulate network request to send report
                Log.d("ReportSyncWorker", "Attempting to sync report ${report.localId}")
                val success = uploadReportToServer(report)
                
                if (success) {
                    // Fail-retry-next behavior: delete from local DB upon successful sync
                    Log.d("ReportSyncWorker", "Successfully synced ${report.localId}. Deleting local copy.")
                    dao.deleteReportById(report.localId)
                } else {
                    // Mark as failure but continue to next (skip and continue)
                    Log.e("ReportSyncWorker", "Failed to sync report ${report.localId}. Skipping for now.")
                    hasFailures = true
                }
            } catch (e: Exception) {
                Log.e("ReportSyncWorker", "Exception while syncing report ${report.localId}", e)
                hasFailures = true
            }
        }

        // If any report failed, return retry so WorkManager will backoff and try again later
        return if (hasFailures) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    // Mock network request. In reality, use Retrofit.
    private suspend fun uploadReportToServer(report: com.project.ecolink.data.local.ReportEntity): Boolean {
        // Simulate network delay
        delay(1000)
        
        // Let's pretend it succeeds 80% of the time to demonstrate fail-retry-next
        val randomSuccess = (1..10).random() > 2 
        
        if (randomSuccess) {
            // Upload successful
            return true
        } else {
            // Server error / network failure
            return false
        }
    }
}
