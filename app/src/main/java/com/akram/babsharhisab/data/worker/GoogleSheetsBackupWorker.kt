package com.akram.babsharhisab.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akram.babsharhisab.core.security.SecureConfigStore
import com.akram.babsharhisab.domain.repository.BackupSyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GoogleSheetsBackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupSyncRepository: BackupSyncRepository,
    private val secureConfigStore: SecureConfigStore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val config = secureConfigStore.getCurrentConfig()
        val isManualTrigger = inputData.getBoolean(KEY_MANUAL_TRIGGER, false)

        if (!isManualTrigger && (!config.autoBackupEnabled || !config.isConfigured)) {
            return Result.success()
        }

        val syncOutcome = backupSyncRepository.performGoogleSheetsSync()
        return syncOutcome.fold(
            onSuccess = { Result.success() },
            onFailure = {
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        )
    }

    companion object {
        const val KEY_MANUAL_TRIGGER = "is_manual_trigger"
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}
