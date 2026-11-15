package com.akram.babsharhisab.`data`.local.dao

import android.database.Cursor
import androidx.room.CoroutinesRoom
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.akram.babsharhisab.`data`.local.entity.SyncLogEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class SyncAuditDao_Impl(
  __db: RoomDatabase,
) : SyncAuditDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfSyncLogEntity: EntityInsertionAdapter<SyncLogEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfSyncLogEntity = object : EntityInsertionAdapter<SyncLogEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `sync_audit_logs` (`log_id`,`executed_at`,`synced_records_count`,`status`,`message_bn`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: SyncLogEntity) {
        statement.bindLong(1, entity.logId)
        statement.bindLong(2, entity.executedAt)
        statement.bindLong(3, entity.syncedRecordsCount.toLong())
        statement.bindString(4, entity.status)
        statement.bindString(5, entity.messageBn)
      }
    }
  }

  public override suspend fun insertSyncLog(log: SyncLogEntity): Unit = CoroutinesRoom.execute(__db,
      true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfSyncLogEntity.insert(log)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun observeRecentSyncLogs(limit: Int): Flow<List<SyncLogEntity>> {
    val _sql: String = "SELECT * FROM sync_audit_logs ORDER BY executed_at DESC LIMIT ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, limit.toLong())
    return CoroutinesRoom.createFlow(__db, false, arrayOf("sync_audit_logs"), object :
        Callable<List<SyncLogEntity>> {
      public override fun call(): List<SyncLogEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfLogId: Int = getColumnIndexOrThrow(_cursor, "log_id")
          val _cursorIndexOfExecutedAt: Int = getColumnIndexOrThrow(_cursor, "executed_at")
          val _cursorIndexOfSyncedRecordsCount: Int = getColumnIndexOrThrow(_cursor,
              "synced_records_count")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfMessageBn: Int = getColumnIndexOrThrow(_cursor, "message_bn")
          val _result: MutableList<SyncLogEntity> = ArrayList<SyncLogEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: SyncLogEntity
            val _tmpLogId: Long
            _tmpLogId = _cursor.getLong(_cursorIndexOfLogId)
            val _tmpExecutedAt: Long
            _tmpExecutedAt = _cursor.getLong(_cursorIndexOfExecutedAt)
            val _tmpSyncedRecordsCount: Int
            _tmpSyncedRecordsCount = _cursor.getInt(_cursorIndexOfSyncedRecordsCount)
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            val _tmpMessageBn: String
            _tmpMessageBn = _cursor.getString(_cursorIndexOfMessageBn)
            _item =
                SyncLogEntity(_tmpLogId,_tmpExecutedAt,_tmpSyncedRecordsCount,_tmpStatus,_tmpMessageBn)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public override fun observeLastSuccessfulSync(): Flow<SyncLogEntity?> {
    val _sql: String =
        "SELECT * FROM sync_audit_logs WHERE status = 'SUCCESS' ORDER BY executed_at DESC LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("sync_audit_logs"), object :
        Callable<SyncLogEntity?> {
      public override fun call(): SyncLogEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfLogId: Int = getColumnIndexOrThrow(_cursor, "log_id")
          val _cursorIndexOfExecutedAt: Int = getColumnIndexOrThrow(_cursor, "executed_at")
          val _cursorIndexOfSyncedRecordsCount: Int = getColumnIndexOrThrow(_cursor,
              "synced_records_count")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfMessageBn: Int = getColumnIndexOrThrow(_cursor, "message_bn")
          val _result: SyncLogEntity?
          if (_cursor.moveToFirst()) {
            val _tmpLogId: Long
            _tmpLogId = _cursor.getLong(_cursorIndexOfLogId)
            val _tmpExecutedAt: Long
            _tmpExecutedAt = _cursor.getLong(_cursorIndexOfExecutedAt)
            val _tmpSyncedRecordsCount: Int
            _tmpSyncedRecordsCount = _cursor.getInt(_cursorIndexOfSyncedRecordsCount)
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            val _tmpMessageBn: String
            _tmpMessageBn = _cursor.getString(_cursorIndexOfMessageBn)
            _result =
                SyncLogEntity(_tmpLogId,_tmpExecutedAt,_tmpSyncedRecordsCount,_tmpStatus,_tmpMessageBn)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
        }
      }

      protected fun finalize() {
        _statement.release()
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
