package com.example.app_retrofit2.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext
    private val context: Context

) {
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder().setRequiredNetworkType(
                NetworkType.CONNECTED
            ).build()

        val request = PeriodicWorkRequestBuilder<ProductSyncWorker>(
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints).setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "product_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun syncNow() {
        val request = OneTimeWorkRequestBuilder<ProductSyncWorker>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(
                    NetworkType.CONNECTED
                ).build()
            ).build()
        WorkManager.getInstance(context).enqueueUniqueWork(
                "product_sync_now",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }
}