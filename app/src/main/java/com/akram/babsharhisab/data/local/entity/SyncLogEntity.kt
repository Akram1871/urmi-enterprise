package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_audit_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    val logId: Long = 0L,

    @ColumnInfo(name = "executed_at")
    val executedAt: Long,

    @ColumnInfo(name = "synced_records_count")
    val syncedRecordsCount: Int,

    @ColumnInfo(name = "status")
    val status: String, // SUCCESS or FAILED

    @ColumnInfo(name = "message_bn")
    val messageBn: String
)
