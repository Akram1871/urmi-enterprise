package com.akram.babsharhisab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akram.babsharhisab.data.local.entity.SyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncAuditDao {

    @Query("SELECT * FROM sync_audit_logs ORDER BY executed_at DESC LIMIT :limit")
    fun observeRecentSyncLogs(limit: Int = 25): Flow<List<SyncLogEntity>>

    @Query("SELECT * FROM sync_audit_logs WHERE status = 'SUCCESS' ORDER BY executed_at DESC LIMIT 1")
    fun observeLastSuccessfulSync(): Flow<SyncLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncLog(log: SyncLogEntity)
}
