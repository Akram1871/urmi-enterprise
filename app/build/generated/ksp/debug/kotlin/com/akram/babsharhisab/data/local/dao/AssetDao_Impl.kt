package com.akram.babsharhisab.`data`.local.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityDeletionOrUpdateAdapter
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.appendPlaceholders
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.newStringBuilder
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.akram.babsharhisab.`data`.local.entity.AssetMaintenanceEntity
import java.lang.Class
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class AssetDao_Impl(
  __db: RoomDatabase,
) : AssetDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfAssetMaintenanceEntity:
      EntityInsertionAdapter<AssetMaintenanceEntity>

  private val __updateAdapterOfAssetMaintenanceEntity:
      EntityDeletionOrUpdateAdapter<AssetMaintenanceEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfAssetMaintenanceEntity = object :
        EntityInsertionAdapter<AssetMaintenanceEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `assets_maintenance` (`asset_id`,`asset_name_bn`,`category_id`,`purchase_cost_bdt`,`lifespan_months`,`purchase_date_millis`,`next_due_date_millis`,`daily_depreciation_bdt`,`monthly_depreciation_bdt`,`notes_bn`,`is_active`,`updated_at`,`is_deleted`,`sync_status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: AssetMaintenanceEntity) {
        statement.bindString(1, entity.assetId)
        statement.bindString(2, entity.assetNameBn)
        statement.bindString(3, entity.categoryId)
        statement.bindLong(4, entity.purchaseCostBdt)
        statement.bindLong(5, entity.lifespanMonths.toLong())
        statement.bindLong(6, entity.purchaseDateMillis)
        statement.bindLong(7, entity.nextDueDateMillis)
        statement.bindLong(8, entity.dailyDepreciationBdt)
        statement.bindLong(9, entity.monthlyDepreciationBdt)
        statement.bindString(10, entity.notesBn)
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.updatedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindString(14, entity.syncStatus)
      }
    }
    this.__updateAdapterOfAssetMaintenanceEntity = object :
        EntityDeletionOrUpdateAdapter<AssetMaintenanceEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `assets_maintenance` SET `asset_id` = ?,`asset_name_bn` = ?,`category_id` = ?,`purchase_cost_bdt` = ?,`lifespan_months` = ?,`purchase_date_millis` = ?,`next_due_date_millis` = ?,`daily_depreciation_bdt` = ?,`monthly_depreciation_bdt` = ?,`notes_bn` = ?,`is_active` = ?,`updated_at` = ?,`is_deleted` = ?,`sync_status` = ? WHERE `asset_id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: AssetMaintenanceEntity) {
        statement.bindString(1, entity.assetId)
        statement.bindString(2, entity.assetNameBn)
        statement.bindString(3, entity.categoryId)
        statement.bindLong(4, entity.purchaseCostBdt)
        statement.bindLong(5, entity.lifespanMonths.toLong())
        statement.bindLong(6, entity.purchaseDateMillis)
        statement.bindLong(7, entity.nextDueDateMillis)
        statement.bindLong(8, entity.dailyDepreciationBdt)
        statement.bindLong(9, entity.monthlyDepreciationBdt)
        statement.bindString(10, entity.notesBn)
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.updatedAt)
        val _tmp_1: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(13, _tmp_1.toLong())
        statement.bindString(14, entity.syncStatus)
        statement.bindString(15, entity.assetId)
      }
    }
  }

  public override suspend fun upsertAsset(asset: AssetMaintenanceEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfAssetMaintenanceEntity.insert(asset)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateAsset(asset: AssetMaintenanceEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfAssetMaintenanceEntity.handle(asset)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun observeAllAssets(): Flow<List<AssetMaintenanceEntity>> {
    val _sql: String =
        "SELECT * FROM assets_maintenance WHERE is_deleted = 0 ORDER BY is_active DESC, next_due_date_millis ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("assets_maintenance"), object :
        Callable<List<AssetMaintenanceEntity>> {
      public override fun call(): List<AssetMaintenanceEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfAssetNameBn: Int = getColumnIndexOrThrow(_cursor, "asset_name_bn")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfPurchaseCostBdt: Int = getColumnIndexOrThrow(_cursor,
              "purchase_cost_bdt")
          val _cursorIndexOfLifespanMonths: Int = getColumnIndexOrThrow(_cursor, "lifespan_months")
          val _cursorIndexOfPurchaseDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "purchase_date_millis")
          val _cursorIndexOfNextDueDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "next_due_date_millis")
          val _cursorIndexOfDailyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "daily_depreciation_bdt")
          val _cursorIndexOfMonthlyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "monthly_depreciation_bdt")
          val _cursorIndexOfNotesBn: Int = getColumnIndexOrThrow(_cursor, "notes_bn")
          val _cursorIndexOfIsActive: Int = getColumnIndexOrThrow(_cursor, "is_active")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<AssetMaintenanceEntity> =
              ArrayList<AssetMaintenanceEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: AssetMaintenanceEntity
            val _tmpAssetId: String
            _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            val _tmpAssetNameBn: String
            _tmpAssetNameBn = _cursor.getString(_cursorIndexOfAssetNameBn)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpPurchaseCostBdt: Long
            _tmpPurchaseCostBdt = _cursor.getLong(_cursorIndexOfPurchaseCostBdt)
            val _tmpLifespanMonths: Int
            _tmpLifespanMonths = _cursor.getInt(_cursorIndexOfLifespanMonths)
            val _tmpPurchaseDateMillis: Long
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis)
            val _tmpNextDueDateMillis: Long
            _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis)
            val _tmpDailyDepreciationBdt: Long
            _tmpDailyDepreciationBdt = _cursor.getLong(_cursorIndexOfDailyDepreciationBdt)
            val _tmpMonthlyDepreciationBdt: Long
            _tmpMonthlyDepreciationBdt = _cursor.getLong(_cursorIndexOfMonthlyDepreciationBdt)
            val _tmpNotesBn: String
            _tmpNotesBn = _cursor.getString(_cursorIndexOfNotesBn)
            val _tmpIsActive: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsActive)
            _tmpIsActive = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp_1 != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                AssetMaintenanceEntity(_tmpAssetId,_tmpAssetNameBn,_tmpCategoryId,_tmpPurchaseCostBdt,_tmpLifespanMonths,_tmpPurchaseDateMillis,_tmpNextDueDateMillis,_tmpDailyDepreciationBdt,_tmpMonthlyDepreciationBdt,_tmpNotesBn,_tmpIsActive,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeActiveAssets(): Flow<List<AssetMaintenanceEntity>> {
    val _sql: String =
        "SELECT * FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1 ORDER BY next_due_date_millis ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("assets_maintenance"), object :
        Callable<List<AssetMaintenanceEntity>> {
      public override fun call(): List<AssetMaintenanceEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfAssetNameBn: Int = getColumnIndexOrThrow(_cursor, "asset_name_bn")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfPurchaseCostBdt: Int = getColumnIndexOrThrow(_cursor,
              "purchase_cost_bdt")
          val _cursorIndexOfLifespanMonths: Int = getColumnIndexOrThrow(_cursor, "lifespan_months")
          val _cursorIndexOfPurchaseDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "purchase_date_millis")
          val _cursorIndexOfNextDueDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "next_due_date_millis")
          val _cursorIndexOfDailyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "daily_depreciation_bdt")
          val _cursorIndexOfMonthlyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "monthly_depreciation_bdt")
          val _cursorIndexOfNotesBn: Int = getColumnIndexOrThrow(_cursor, "notes_bn")
          val _cursorIndexOfIsActive: Int = getColumnIndexOrThrow(_cursor, "is_active")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<AssetMaintenanceEntity> =
              ArrayList<AssetMaintenanceEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: AssetMaintenanceEntity
            val _tmpAssetId: String
            _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            val _tmpAssetNameBn: String
            _tmpAssetNameBn = _cursor.getString(_cursorIndexOfAssetNameBn)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpPurchaseCostBdt: Long
            _tmpPurchaseCostBdt = _cursor.getLong(_cursorIndexOfPurchaseCostBdt)
            val _tmpLifespanMonths: Int
            _tmpLifespanMonths = _cursor.getInt(_cursorIndexOfLifespanMonths)
            val _tmpPurchaseDateMillis: Long
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis)
            val _tmpNextDueDateMillis: Long
            _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis)
            val _tmpDailyDepreciationBdt: Long
            _tmpDailyDepreciationBdt = _cursor.getLong(_cursorIndexOfDailyDepreciationBdt)
            val _tmpMonthlyDepreciationBdt: Long
            _tmpMonthlyDepreciationBdt = _cursor.getLong(_cursorIndexOfMonthlyDepreciationBdt)
            val _tmpNotesBn: String
            _tmpNotesBn = _cursor.getString(_cursorIndexOfNotesBn)
            val _tmpIsActive: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsActive)
            _tmpIsActive = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp_1 != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                AssetMaintenanceEntity(_tmpAssetId,_tmpAssetNameBn,_tmpCategoryId,_tmpPurchaseCostBdt,_tmpLifespanMonths,_tmpPurchaseDateMillis,_tmpNextDueDateMillis,_tmpDailyDepreciationBdt,_tmpMonthlyDepreciationBdt,_tmpNotesBn,_tmpIsActive,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeTotalDailyDepreciation(): Flow<Long> {
    val _sql: String =
        "SELECT COALESCE(SUM(daily_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("assets_maintenance"), object :
        Callable<Long> {
      public override fun call(): Long {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long
          if (_cursor.moveToFirst()) {
            val _tmp: Long
            _tmp = _cursor.getLong(0)
            _result = _tmp
          } else {
            _result = 0L
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

  public override suspend fun getTotalDailyDepreciation(): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(daily_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<Long> {
      public override fun call(): Long {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long
          if (_cursor.moveToFirst()) {
            val _tmp: Long
            _tmp = _cursor.getLong(0)
            _result = _tmp
          } else {
            _result = 0L
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeTotalMonthlyDepreciation(): Flow<Long> {
    val _sql: String =
        "SELECT COALESCE(SUM(monthly_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("assets_maintenance"), object :
        Callable<Long> {
      public override fun call(): Long {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Long
          if (_cursor.moveToFirst()) {
            val _tmp: Long
            _tmp = _cursor.getLong(0)
            _result = _tmp
          } else {
            _result = 0L
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

  public override suspend fun getAssetById(assetId: String): AssetMaintenanceEntity? {
    val _sql: String = "SELECT * FROM assets_maintenance WHERE asset_id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, assetId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<AssetMaintenanceEntity?> {
      public override fun call(): AssetMaintenanceEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfAssetNameBn: Int = getColumnIndexOrThrow(_cursor, "asset_name_bn")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfPurchaseCostBdt: Int = getColumnIndexOrThrow(_cursor,
              "purchase_cost_bdt")
          val _cursorIndexOfLifespanMonths: Int = getColumnIndexOrThrow(_cursor, "lifespan_months")
          val _cursorIndexOfPurchaseDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "purchase_date_millis")
          val _cursorIndexOfNextDueDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "next_due_date_millis")
          val _cursorIndexOfDailyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "daily_depreciation_bdt")
          val _cursorIndexOfMonthlyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "monthly_depreciation_bdt")
          val _cursorIndexOfNotesBn: Int = getColumnIndexOrThrow(_cursor, "notes_bn")
          val _cursorIndexOfIsActive: Int = getColumnIndexOrThrow(_cursor, "is_active")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: AssetMaintenanceEntity?
          if (_cursor.moveToFirst()) {
            val _tmpAssetId: String
            _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            val _tmpAssetNameBn: String
            _tmpAssetNameBn = _cursor.getString(_cursorIndexOfAssetNameBn)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpPurchaseCostBdt: Long
            _tmpPurchaseCostBdt = _cursor.getLong(_cursorIndexOfPurchaseCostBdt)
            val _tmpLifespanMonths: Int
            _tmpLifespanMonths = _cursor.getInt(_cursorIndexOfLifespanMonths)
            val _tmpPurchaseDateMillis: Long
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis)
            val _tmpNextDueDateMillis: Long
            _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis)
            val _tmpDailyDepreciationBdt: Long
            _tmpDailyDepreciationBdt = _cursor.getLong(_cursorIndexOfDailyDepreciationBdt)
            val _tmpMonthlyDepreciationBdt: Long
            _tmpMonthlyDepreciationBdt = _cursor.getLong(_cursorIndexOfMonthlyDepreciationBdt)
            val _tmpNotesBn: String
            _tmpNotesBn = _cursor.getString(_cursorIndexOfNotesBn)
            val _tmpIsActive: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsActive)
            _tmpIsActive = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp_1 != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _result =
                AssetMaintenanceEntity(_tmpAssetId,_tmpAssetNameBn,_tmpCategoryId,_tmpPurchaseCostBdt,_tmpLifespanMonths,_tmpPurchaseDateMillis,_tmpNextDueDateMillis,_tmpDailyDepreciationBdt,_tmpMonthlyDepreciationBdt,_tmpNotesBn,_tmpIsActive,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
          } else {
            _result = null
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun getUnsyncedAssets(): List<AssetMaintenanceEntity> {
    val _sql: String = "SELECT * FROM assets_maintenance WHERE sync_status != 'SYNCED'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<AssetMaintenanceEntity>>
        {
      public override fun call(): List<AssetMaintenanceEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfAssetNameBn: Int = getColumnIndexOrThrow(_cursor, "asset_name_bn")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfPurchaseCostBdt: Int = getColumnIndexOrThrow(_cursor,
              "purchase_cost_bdt")
          val _cursorIndexOfLifespanMonths: Int = getColumnIndexOrThrow(_cursor, "lifespan_months")
          val _cursorIndexOfPurchaseDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "purchase_date_millis")
          val _cursorIndexOfNextDueDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "next_due_date_millis")
          val _cursorIndexOfDailyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "daily_depreciation_bdt")
          val _cursorIndexOfMonthlyDepreciationBdt: Int = getColumnIndexOrThrow(_cursor,
              "monthly_depreciation_bdt")
          val _cursorIndexOfNotesBn: Int = getColumnIndexOrThrow(_cursor, "notes_bn")
          val _cursorIndexOfIsActive: Int = getColumnIndexOrThrow(_cursor, "is_active")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<AssetMaintenanceEntity> =
              ArrayList<AssetMaintenanceEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: AssetMaintenanceEntity
            val _tmpAssetId: String
            _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            val _tmpAssetNameBn: String
            _tmpAssetNameBn = _cursor.getString(_cursorIndexOfAssetNameBn)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpPurchaseCostBdt: Long
            _tmpPurchaseCostBdt = _cursor.getLong(_cursorIndexOfPurchaseCostBdt)
            val _tmpLifespanMonths: Int
            _tmpLifespanMonths = _cursor.getInt(_cursorIndexOfLifespanMonths)
            val _tmpPurchaseDateMillis: Long
            _tmpPurchaseDateMillis = _cursor.getLong(_cursorIndexOfPurchaseDateMillis)
            val _tmpNextDueDateMillis: Long
            _tmpNextDueDateMillis = _cursor.getLong(_cursorIndexOfNextDueDateMillis)
            val _tmpDailyDepreciationBdt: Long
            _tmpDailyDepreciationBdt = _cursor.getLong(_cursorIndexOfDailyDepreciationBdt)
            val _tmpMonthlyDepreciationBdt: Long
            _tmpMonthlyDepreciationBdt = _cursor.getLong(_cursorIndexOfMonthlyDepreciationBdt)
            val _tmpNotesBn: String
            _tmpNotesBn = _cursor.getString(_cursorIndexOfNotesBn)
            val _tmpIsActive: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsActive)
            _tmpIsActive = _tmp != 0
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp_1: Int
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp_1 != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                AssetMaintenanceEntity(_tmpAssetId,_tmpAssetNameBn,_tmpCategoryId,_tmpPurchaseCostBdt,_tmpLifespanMonths,_tmpPurchaseDateMillis,_tmpNextDueDateMillis,_tmpDailyDepreciationBdt,_tmpMonthlyDepreciationBdt,_tmpNotesBn,_tmpIsActive,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override suspend fun markAssetsSynced(assetIds: List<String>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stringBuilder: StringBuilder = newStringBuilder()
      _stringBuilder.append("UPDATE assets_maintenance SET sync_status = 'SYNCED' WHERE asset_id IN (")
      val _inputSize: Int = assetIds.size
      appendPlaceholders(_stringBuilder, _inputSize)
      _stringBuilder.append(")")
      val _sql: String = _stringBuilder.toString()
      val _stmt: SupportSQLiteStatement = __db.compileStatement(_sql)
      var _argIndex: Int = 1
      for (_item: String in assetIds) {
        _stmt.bindString(_argIndex, _item)
        _argIndex++
      }
      __db.beginTransaction()
      try {
        _stmt.executeUpdateDelete()
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
