package com.akram.babsharhisab.data.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupWorkScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    private val connectedConstraints: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(false)
        .build()

    /**
     * Schedules the 12-hour periodic daily backup job (`babshar_hisab_daily_sheets_backup`).
     */
    fun scheduleDailyPeriodicBackup() {
        val periodicRequest = PeriodicWorkRequestBuilder<GoogleSheetsBackupWorker>(
            repeatInterval = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 2,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(connectedConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .addTag(TAG_BACKUP_WORK)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    /**
     * Schedules a debounced background sync 10 seconds after any local transaction mutation
     * so rapid entries batch into a single HTTP request whenever internet is connected.
     */
    fun scheduleDebouncedMutationSync() {
        val oneTimeRequest = OneTimeWorkRequestBuilder<GoogleSheetsBackupWorker>()
            .setConstraints(connectedConstraints)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .setInputData(workDataOf(GoogleSheetsBackupWorker.KEY_MANUAL_TRIGGER to false))
            .addTag(TAG_BACKUP_WORK)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_INSTANT_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )
    }

    /**
     * Immediately triggers a manual Google Sheets sync when the user taps "এখনই ব্যাকআপ নিন".
     */
    fun triggerManualSyncNow() {
        val manualRequest = OneTimeWorkRequestBuilder<GoogleSheetsBackupWorker>()
            .setConstraints(connectedConstraints)
            .setInputData(workDataOf(GoogleSheetsBackupWorker.KEY_MANUAL_TRIGGER to true))
            .addTag(TAG_MANUAL_BACKUP)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_MANUAL_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            manualRequest
        )
    }

    fun observeManualSyncWorkInfos(): Flow<List<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_MANUAL_WORK_NAME)

    companion object {
        const val UNIQUE_DAILY_WORK_NAME = "babshar_hisab_daily_sheets_backup"
        const val UNIQUE_INSTANT_WORK_NAME = "babshar_hisab_instant_sync"
        const val UNIQUE_MANUAL_WORK_NAME = "babshar_hisab_manual_sync"
        const val TAG_BACKUP_WORK = "tag_babshar_hisab_backup"
        const val TAG_MANUAL_BACKUP = "tag_babshar_hisab_manual_backup"
    }
}
