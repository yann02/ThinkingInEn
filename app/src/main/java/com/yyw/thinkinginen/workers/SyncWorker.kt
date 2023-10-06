package com.yyw.thinkinginen.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.yyw.thinkinginen.di.IoDispatcher
import com.yyw.thinkinginen.repositories.MainRepository
import com.yyw.thinkinginen.utils.Synchronizer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val mainRepository: MainRepository,
) : CoroutineWorker(appContext, workerParams), Synchronizer {
    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(dispatcher) {
        val syncedSuccess = mainRepository.sync()
        if (syncedSuccess) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}