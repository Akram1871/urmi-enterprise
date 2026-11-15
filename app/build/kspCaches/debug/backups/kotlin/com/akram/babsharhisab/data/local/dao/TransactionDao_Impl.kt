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
import androidx.room.SharedSQLiteStatement
import androidx.room.util.appendPlaceholders
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.newStringBuilder
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.akram.babsharhisab.`data`.local.entity.TransactionEntity
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
public class TransactionDao_Impl(
  __db: RoomDatabase,
) : TransactionDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfTransactionEntity: EntityInsertionAdapter<TransactionEntity>

  private val __updateAdapterOfTransactionEntity: EntityDeletionOrUpdateAdapter<TransactionEntity>

  private val __preparedStmtOfSoftDeleteTransaction: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteTransactionsByShopkeeper: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteTransactionsByVendor: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfTransactionEntity = object :
        EntityInsertionAdapter<TransactionEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `transactions` (`transaction_id`,`tx_type`,`category_id`,`vendor_id`,`shopkeeper_id`,`asset_id`,`total_bill_bdt`,`cash_amount_bdt`,`baki_delta_bdt`,`running_shopkeeper_baki_bdt`,`event_date_millis`,`date_key`,`year_month`,`note_bn`,`created_at`,`updated_at`,`version`,`is_deleted`,`sync_status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TransactionEntity) {
        statement.bindString(1, entity.transactionId)
        statement.bindString(2, entity.txType)
        statement.bindString(3, entity.categoryId)
        val _tmpVendorId: String? = entity.vendorId
        if (_tmpVendorId == null) {
          statement.bindNull(4)
        } else {
          statement.bindString(4, _tmpVendorId)
        }
        val _tmpShopkeeperId: String? = entity.shopkeeperId
        if (_tmpShopkeeperId == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpShopkeeperId)
        }
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpAssetId)
        }
        statement.bindLong(7, entity.totalBillBdt)
        statement.bindLong(8, entity.cashAmountBdt)
        statement.bindLong(9, entity.bakiDeltaBdt)
        statement.bindLong(10, entity.runningShopkeeperBakiBdt)
        statement.bindLong(11, entity.eventDateMillis)
        statement.bindString(12, entity.dateKey)
        statement.bindString(13, entity.yearMonth)
        statement.bindString(14, entity.noteBn)
        statement.bindLong(15, entity.createdAt)
        statement.bindLong(16, entity.updatedAt)
        statement.bindLong(17, entity.version.toLong())
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(18, _tmp.toLong())
        statement.bindString(19, entity.syncStatus)
      }
    }
    this.__updateAdapterOfTransactionEntity = object :
        EntityDeletionOrUpdateAdapter<TransactionEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `transactions` SET `transaction_id` = ?,`tx_type` = ?,`category_id` = ?,`vendor_id` = ?,`shopkeeper_id` = ?,`asset_id` = ?,`total_bill_bdt` = ?,`cash_amount_bdt` = ?,`baki_delta_bdt` = ?,`running_shopkeeper_baki_bdt` = ?,`event_date_millis` = ?,`date_key` = ?,`year_month` = ?,`note_bn` = ?,`created_at` = ?,`updated_at` = ?,`version` = ?,`is_deleted` = ?,`sync_status` = ? WHERE `transaction_id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: TransactionEntity) {
        statement.bindString(1, entity.transactionId)
        statement.bindString(2, entity.txType)
        statement.bindString(3, entity.categoryId)
        val _tmpVendorId: String? = entity.vendorId
        if (_tmpVendorId == null) {
          statement.bindNull(4)
        } else {
          statement.bindString(4, _tmpVendorId)
        }
        val _tmpShopkeeperId: String? = entity.shopkeeperId
        if (_tmpShopkeeperId == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpShopkeeperId)
        }
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpAssetId)
        }
        statement.bindLong(7, entity.totalBillBdt)
        statement.bindLong(8, entity.cashAmountBdt)
        statement.bindLong(9, entity.bakiDeltaBdt)
        statement.bindLong(10, entity.runningShopkeeperBakiBdt)
        statement.bindLong(11, entity.eventDateMillis)
        statement.bindString(12, entity.dateKey)
        statement.bindString(13, entity.yearMonth)
        statement.bindString(14, entity.noteBn)
        statement.bindLong(15, entity.createdAt)
        statement.bindLong(16, entity.updatedAt)
        statement.bindLong(17, entity.version.toLong())
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(18, _tmp.toLong())
        statement.bindString(19, entity.syncStatus)
        statement.bindString(20, entity.transactionId)
      }
    }
    this.__preparedStmtOfSoftDeleteTransaction = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE transactions
            |        SET is_deleted = 1,
            |            updated_at = ?,
            |            version = version + 1,
            |            sync_status = 'PENDING'
            |        WHERE transaction_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteTransactionsByShopkeeper = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE transactions
            |        SET is_deleted = 1,
            |            updated_at = ?,
            |            version = version + 1,
            |            sync_status = 'PENDING'
            |        WHERE shopkeeper_id = ? AND is_deleted = 0
            |    
            """.trimMargin()
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteTransactionsByVendor = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE transactions
            |        SET is_deleted = 1,
            |            updated_at = ?,
            |            version = version + 1,
            |            sync_status = 'PENDING'
            |        WHERE vendor_id = ? AND is_deleted = 0
            |    
            """.trimMargin()
        return _query
      }
    }
  }

  public override suspend fun upsertTransaction(transaction: TransactionEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfTransactionEntity.insert(transaction)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateTransaction(transaction: TransactionEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfTransactionEntity.handle(transaction)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun softDeleteTransaction(transactionId: String, updatedAt: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteTransaction.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 2
      _stmt.bindString(_argIndex, transactionId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSoftDeleteTransaction.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteTransactionsByShopkeeper(shopkeeperId: String,
      updatedAt: Long): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement =
          __preparedStmtOfSoftDeleteTransactionsByShopkeeper.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 2
      _stmt.bindString(_argIndex, shopkeeperId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSoftDeleteTransactionsByShopkeeper.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteTransactionsByVendor(vendorId: String, updatedAt: Long):
      Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteTransactionsByVendor.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 2
      _stmt.bindString(_argIndex, vendorId)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfSoftDeleteTransactionsByVendor.release(_stmt)
      }
    }
  })

  public override fun observeTransactionsByDate(dateKey: String): Flow<List<TransactionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM transactions
        |        WHERE is_deleted = 0 AND date_key = ?
        |        ORDER BY event_date_millis DESC, created_at DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, dateKey)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeRecentTransactions(limit: Int): Flow<List<TransactionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM transactions
        |        WHERE is_deleted = 0
        |        ORDER BY event_date_millis DESC, created_at DESC
        |        LIMIT ?
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, limit.toLong())
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeTransactionsForShopkeeper(shopkeeperId: String):
      Flow<List<TransactionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM transactions
        |        WHERE is_deleted = 0 AND shopkeeper_id = ?
        |        ORDER BY event_date_millis DESC, created_at DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, shopkeeperId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeTransactionsForVendor(vendorId: String):
      Flow<List<TransactionEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM transactions
        |        WHERE is_deleted = 0 AND vendor_id = ?
        |        ORDER BY event_date_millis DESC, created_at DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, vendorId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getTransactionById(transactionId: String): TransactionEntity? {
    val _sql: String = "SELECT * FROM transactions WHERE transaction_id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, transactionId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<TransactionEntity?> {
      public override fun call(): TransactionEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: TransactionEntity?
          if (_cursor.moveToFirst()) {
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _result =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeDailyAggregation(dateKey: String): Flow<DailyAggregationSnapshot> {
    val _sql: String = """
        |
        |        SELECT
        |            ? AS date_key,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_sales_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_baki_given_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'BAKI_COLLECTION' THEN cash_amount_bdt ELSE 0 END), 0) AS baki_collected_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS wholesale_purchase_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_purchase_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_vendor_baki_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'VENDOR_BAKI_PAYMENT' THEN cash_amount_bdt ELSE 0 END), 0) AS vendor_baki_paid_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS operating_expense_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS asset_maintenance_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS personal_home_expense_bdt
        |        FROM transactions
        |        WHERE is_deleted = 0 AND date_key = ?
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, dateKey)
    _argIndex = 2
    _statement.bindString(_argIndex, dateKey)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<DailyAggregationSnapshot> {
      public override fun call(): DailyAggregationSnapshot {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfDateKey: Int = 0
          val _cursorIndexOfTotalSalesBdt: Int = 1
          val _cursorIndexOfCashSalesBdt: Int = 2
          val _cursorIndexOfNewBakiGivenBdt: Int = 3
          val _cursorIndexOfBakiCollectedBdt: Int = 4
          val _cursorIndexOfWholesalePurchaseBdt: Int = 5
          val _cursorIndexOfCashPurchaseBdt: Int = 6
          val _cursorIndexOfNewVendorBakiBdt: Int = 7
          val _cursorIndexOfVendorBakiPaidBdt: Int = 8
          val _cursorIndexOfOperatingExpenseBdt: Int = 9
          val _cursorIndexOfAssetMaintenanceBdt: Int = 10
          val _cursorIndexOfPersonalHomeExpenseBdt: Int = 11
          val _result: DailyAggregationSnapshot
          if (_cursor.moveToFirst()) {
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpTotalSalesBdt: Long
            _tmpTotalSalesBdt = _cursor.getLong(_cursorIndexOfTotalSalesBdt)
            val _tmpCashSalesBdt: Long
            _tmpCashSalesBdt = _cursor.getLong(_cursorIndexOfCashSalesBdt)
            val _tmpNewBakiGivenBdt: Long
            _tmpNewBakiGivenBdt = _cursor.getLong(_cursorIndexOfNewBakiGivenBdt)
            val _tmpBakiCollectedBdt: Long
            _tmpBakiCollectedBdt = _cursor.getLong(_cursorIndexOfBakiCollectedBdt)
            val _tmpWholesalePurchaseBdt: Long
            _tmpWholesalePurchaseBdt = _cursor.getLong(_cursorIndexOfWholesalePurchaseBdt)
            val _tmpCashPurchaseBdt: Long
            _tmpCashPurchaseBdt = _cursor.getLong(_cursorIndexOfCashPurchaseBdt)
            val _tmpNewVendorBakiBdt: Long
            _tmpNewVendorBakiBdt = _cursor.getLong(_cursorIndexOfNewVendorBakiBdt)
            val _tmpVendorBakiPaidBdt: Long
            _tmpVendorBakiPaidBdt = _cursor.getLong(_cursorIndexOfVendorBakiPaidBdt)
            val _tmpOperatingExpenseBdt: Long
            _tmpOperatingExpenseBdt = _cursor.getLong(_cursorIndexOfOperatingExpenseBdt)
            val _tmpAssetMaintenanceBdt: Long
            _tmpAssetMaintenanceBdt = _cursor.getLong(_cursorIndexOfAssetMaintenanceBdt)
            val _tmpPersonalHomeExpenseBdt: Long
            _tmpPersonalHomeExpenseBdt = _cursor.getLong(_cursorIndexOfPersonalHomeExpenseBdt)
            _result =
                DailyAggregationSnapshot(_tmpDateKey,_tmpTotalSalesBdt,_tmpCashSalesBdt,_tmpNewBakiGivenBdt,_tmpBakiCollectedBdt,_tmpWholesalePurchaseBdt,_tmpCashPurchaseBdt,_tmpNewVendorBakiBdt,_tmpVendorBakiPaidBdt,_tmpOperatingExpenseBdt,_tmpAssetMaintenanceBdt,_tmpPersonalHomeExpenseBdt)
          } else {
            error("The query result was empty, but expected a single row to return a NON-NULL object of type <com.akram.babsharhisab.`data`.local.dao.DailyAggregationSnapshot>.")
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

  public override suspend fun getDailyAggregation(dateKey: String): DailyAggregationSnapshot {
    val _sql: String = """
        |
        |        SELECT
        |            ? AS date_key,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_sales_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_baki_given_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'BAKI_COLLECTION' THEN cash_amount_bdt ELSE 0 END), 0) AS baki_collected_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS wholesale_purchase_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_purchase_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_vendor_baki_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'VENDOR_BAKI_PAYMENT' THEN cash_amount_bdt ELSE 0 END), 0) AS vendor_baki_paid_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS operating_expense_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS asset_maintenance_bdt,
        |            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS personal_home_expense_bdt
        |        FROM transactions
        |        WHERE is_deleted = 0 AND date_key = ?
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 2)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, dateKey)
    _argIndex = 2
    _statement.bindString(_argIndex, dateKey)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<DailyAggregationSnapshot> {
      public override fun call(): DailyAggregationSnapshot {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfDateKey: Int = 0
          val _cursorIndexOfTotalSalesBdt: Int = 1
          val _cursorIndexOfCashSalesBdt: Int = 2
          val _cursorIndexOfNewBakiGivenBdt: Int = 3
          val _cursorIndexOfBakiCollectedBdt: Int = 4
          val _cursorIndexOfWholesalePurchaseBdt: Int = 5
          val _cursorIndexOfCashPurchaseBdt: Int = 6
          val _cursorIndexOfNewVendorBakiBdt: Int = 7
          val _cursorIndexOfVendorBakiPaidBdt: Int = 8
          val _cursorIndexOfOperatingExpenseBdt: Int = 9
          val _cursorIndexOfAssetMaintenanceBdt: Int = 10
          val _cursorIndexOfPersonalHomeExpenseBdt: Int = 11
          val _result: DailyAggregationSnapshot
          if (_cursor.moveToFirst()) {
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpTotalSalesBdt: Long
            _tmpTotalSalesBdt = _cursor.getLong(_cursorIndexOfTotalSalesBdt)
            val _tmpCashSalesBdt: Long
            _tmpCashSalesBdt = _cursor.getLong(_cursorIndexOfCashSalesBdt)
            val _tmpNewBakiGivenBdt: Long
            _tmpNewBakiGivenBdt = _cursor.getLong(_cursorIndexOfNewBakiGivenBdt)
            val _tmpBakiCollectedBdt: Long
            _tmpBakiCollectedBdt = _cursor.getLong(_cursorIndexOfBakiCollectedBdt)
            val _tmpWholesalePurchaseBdt: Long
            _tmpWholesalePurchaseBdt = _cursor.getLong(_cursorIndexOfWholesalePurchaseBdt)
            val _tmpCashPurchaseBdt: Long
            _tmpCashPurchaseBdt = _cursor.getLong(_cursorIndexOfCashPurchaseBdt)
            val _tmpNewVendorBakiBdt: Long
            _tmpNewVendorBakiBdt = _cursor.getLong(_cursorIndexOfNewVendorBakiBdt)
            val _tmpVendorBakiPaidBdt: Long
            _tmpVendorBakiPaidBdt = _cursor.getLong(_cursorIndexOfVendorBakiPaidBdt)
            val _tmpOperatingExpenseBdt: Long
            _tmpOperatingExpenseBdt = _cursor.getLong(_cursorIndexOfOperatingExpenseBdt)
            val _tmpAssetMaintenanceBdt: Long
            _tmpAssetMaintenanceBdt = _cursor.getLong(_cursorIndexOfAssetMaintenanceBdt)
            val _tmpPersonalHomeExpenseBdt: Long
            _tmpPersonalHomeExpenseBdt = _cursor.getLong(_cursorIndexOfPersonalHomeExpenseBdt)
            _result =
                DailyAggregationSnapshot(_tmpDateKey,_tmpTotalSalesBdt,_tmpCashSalesBdt,_tmpNewBakiGivenBdt,_tmpBakiCollectedBdt,_tmpWholesalePurchaseBdt,_tmpCashPurchaseBdt,_tmpNewVendorBakiBdt,_tmpVendorBakiPaidBdt,_tmpOperatingExpenseBdt,_tmpAssetMaintenanceBdt,_tmpPersonalHomeExpenseBdt)
          } else {
            error("The query result was empty, but expected a single row to return a NON-NULL object of type <com.akram.babsharhisab.`data`.local.dao.DailyAggregationSnapshot>.")
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public override fun observeMonthlyHomeExpense(yearMonth: String): Flow<Long> {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(total_bill_bdt), 0)
        |        FROM transactions
        |        WHERE is_deleted = 0 AND year_month = ? AND tx_type = 'PERSONAL_HOME_EXPENSE'
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, yearMonth)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object : Callable<Long> {
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

  public override fun observeDailyExpenseByCategory(dateKey: String):
      Flow<List<CategoryAggregationRow>> {
    val _sql: String = """
        |
        |        SELECT
        |            c.category_id AS category_id,
        |            c.name_bn AS name_bn,
        |            c.color_hex AS color_hex,
        |            c.icon_name AS icon_name,
        |            COALESCE(SUM(t.total_bill_bdt), 0) AS total_amount_bdt,
        |            COUNT(t.transaction_id) AS tx_count
        |        FROM transactions t
        |        INNER JOIN categories c ON t.category_id = c.category_id
        |        WHERE t.is_deleted = 0
        |          AND t.date_key = ?
        |          AND t.tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE')
        |        GROUP BY c.category_id
        |        ORDER BY total_amount_bdt DESC
        |    
        """.trimMargin()
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, dateKey)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions", "categories"), object :
        Callable<List<CategoryAggregationRow>> {
      public override fun call(): List<CategoryAggregationRow> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = 0
          val _cursorIndexOfNameBn: Int = 1
          val _cursorIndexOfColorHex: Int = 2
          val _cursorIndexOfIconName: Int = 3
          val _cursorIndexOfTotalAmountBdt: Int = 4
          val _cursorIndexOfTxCount: Int = 5
          val _result: MutableList<CategoryAggregationRow> =
              ArrayList<CategoryAggregationRow>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CategoryAggregationRow
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpTotalAmountBdt: Long
            _tmpTotalAmountBdt = _cursor.getLong(_cursorIndexOfTotalAmountBdt)
            val _tmpTxCount: Int
            _tmpTxCount = _cursor.getInt(_cursorIndexOfTxCount)
            _item =
                CategoryAggregationRow(_tmpCategoryId,_tmpNameBn,_tmpColorHex,_tmpIconName,_tmpTotalAmountBdt,_tmpTxCount)
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

  public override fun observeMonthlyIncomeExpense(yearMonths: List<String>):
      Flow<List<MonthlyIncomeExpenseRow>> {
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            year_month,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS total_purchase_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS total_operating_expense_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS total_asset_expense_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type IN ('SALE', 'BAKI_COLLECTION') THEN cash_amount_bdt ELSE 0 END), 0) AS total_cash_in_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' THEN total_bill_bdt ELSE 0 END), 0) AS total_home_expense_bdt")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        FROM transactions")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE is_deleted = 0 AND year_month IN (")
    val _inputSize: Int = yearMonths.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        GROUP BY year_month")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        ORDER BY year_month ASC")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _statement: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (_item: String in yearMonths) {
      _statement.bindString(_argIndex, _item)
      _argIndex++
    }
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<MonthlyIncomeExpenseRow>> {
      public override fun call(): List<MonthlyIncomeExpenseRow> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfYearMonth: Int = 0
          val _cursorIndexOfTotalSalesBdt: Int = 1
          val _cursorIndexOfTotalPurchaseBdt: Int = 2
          val _cursorIndexOfTotalOperatingExpenseBdt: Int = 3
          val _cursorIndexOfTotalAssetExpenseBdt: Int = 4
          val _cursorIndexOfTotalCashInBdt: Int = 5
          val _cursorIndexOfTotalHomeExpenseBdt: Int = 6
          val _result: MutableList<MonthlyIncomeExpenseRow> =
              ArrayList<MonthlyIncomeExpenseRow>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item_1: MonthlyIncomeExpenseRow
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpTotalSalesBdt: Long
            _tmpTotalSalesBdt = _cursor.getLong(_cursorIndexOfTotalSalesBdt)
            val _tmpTotalPurchaseBdt: Long
            _tmpTotalPurchaseBdt = _cursor.getLong(_cursorIndexOfTotalPurchaseBdt)
            val _tmpTotalOperatingExpenseBdt: Long
            _tmpTotalOperatingExpenseBdt = _cursor.getLong(_cursorIndexOfTotalOperatingExpenseBdt)
            val _tmpTotalAssetExpenseBdt: Long
            _tmpTotalAssetExpenseBdt = _cursor.getLong(_cursorIndexOfTotalAssetExpenseBdt)
            val _tmpTotalCashInBdt: Long
            _tmpTotalCashInBdt = _cursor.getLong(_cursorIndexOfTotalCashInBdt)
            val _tmpTotalHomeExpenseBdt: Long
            _tmpTotalHomeExpenseBdt = _cursor.getLong(_cursorIndexOfTotalHomeExpenseBdt)
            _item_1 =
                MonthlyIncomeExpenseRow(_tmpYearMonth,_tmpTotalSalesBdt,_tmpTotalPurchaseBdt,_tmpTotalOperatingExpenseBdt,_tmpTotalAssetExpenseBdt,_tmpTotalCashInBdt,_tmpTotalHomeExpenseBdt)
            _result.add(_item_1)
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

  public override fun observePeriodExpenseByCategory(yearMonths: List<String>):
      Flow<List<CategoryAggregationRow>> {
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            c.category_id AS category_id,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            c.name_bn AS name_bn,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            c.color_hex AS color_hex,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            c.icon_name AS icon_name,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(t.total_bill_bdt), 0) AS total_amount_bdt,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COUNT(t.transaction_id) AS tx_count")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        FROM transactions t")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        INNER JOIN categories c ON t.category_id = c.category_id")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE t.is_deleted = 0")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND t.year_month IN (")
    val _inputSize: Int = yearMonths.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND t.tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE')")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        GROUP BY c.category_id")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        ORDER BY total_amount_bdt DESC")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _statement: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (_item: String in yearMonths) {
      _statement.bindString(_argIndex, _item)
      _argIndex++
    }
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions", "categories"), object :
        Callable<List<CategoryAggregationRow>> {
      public override fun call(): List<CategoryAggregationRow> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = 0
          val _cursorIndexOfNameBn: Int = 1
          val _cursorIndexOfColorHex: Int = 2
          val _cursorIndexOfIconName: Int = 3
          val _cursorIndexOfTotalAmountBdt: Int = 4
          val _cursorIndexOfTxCount: Int = 5
          val _result: MutableList<CategoryAggregationRow> =
              ArrayList<CategoryAggregationRow>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item_1: CategoryAggregationRow
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpTotalAmountBdt: Long
            _tmpTotalAmountBdt = _cursor.getLong(_cursorIndexOfTotalAmountBdt)
            val _tmpTxCount: Int
            _tmpTxCount = _cursor.getInt(_cursorIndexOfTxCount)
            _item_1 =
                CategoryAggregationRow(_tmpCategoryId,_tmpNameBn,_tmpColorHex,_tmpIconName,_tmpTotalAmountBdt,_tmpTxCount)
            _result.add(_item_1)
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

  public override fun observeMonthlyCategoryTrends(yearMonths: List<String>):
      Flow<List<MonthlyCategoryTrendRow>> {
    val _stringBuilder: StringBuilder = newStringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            year_month,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            category_id,")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("            COALESCE(SUM(total_bill_bdt), 0) AS total_amount_bdt")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        FROM transactions")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE is_deleted = 0")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND year_month IN (")
    val _inputSize: Int = yearMonths.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE', 'PERSONAL_HOME_EXPENSE')")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        GROUP BY year_month, category_id")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        ORDER BY year_month ASC")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    val _argCount: Int = 0 + _inputSize
    val _statement: RoomSQLiteQuery = acquire(_sql, _argCount)
    var _argIndex: Int = 1
    for (_item: String in yearMonths) {
      _statement.bindString(_argIndex, _item)
      _argIndex++
    }
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object :
        Callable<List<MonthlyCategoryTrendRow>> {
      public override fun call(): List<MonthlyCategoryTrendRow> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfYearMonth: Int = 0
          val _cursorIndexOfCategoryId: Int = 1
          val _cursorIndexOfTotalAmountBdt: Int = 2
          val _result: MutableList<MonthlyCategoryTrendRow> =
              ArrayList<MonthlyCategoryTrendRow>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item_1: MonthlyCategoryTrendRow
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpTotalAmountBdt: Long
            _tmpTotalAmountBdt = _cursor.getLong(_cursorIndexOfTotalAmountBdt)
            _item_1 = MonthlyCategoryTrendRow(_tmpYearMonth,_tmpCategoryId,_tmpTotalAmountBdt)
            _result.add(_item_1)
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

  public override fun observePendingSyncTransactionCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM transactions WHERE sync_status != 'SYNCED'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("transactions"), object : Callable<Int> {
      public override fun call(): Int {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _result: Int
          if (_cursor.moveToFirst()) {
            val _tmp: Int
            _tmp = _cursor.getInt(0)
            _result = _tmp
          } else {
            _result = 0
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

  public override suspend fun getUnsyncedTransactions(batchSize: Int): List<TransactionEntity> {
    val _sql: String =
        "SELECT * FROM transactions WHERE sync_status != 'SYNCED' ORDER BY updated_at ASC LIMIT ?"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindLong(_argIndex, batchSize.toLong())
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getAllActiveTransactionsForExport(): List<TransactionEntity> {
    val _sql: String =
        "SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY event_date_millis DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<TransactionEntity>> {
      public override fun call(): List<TransactionEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfTransactionId: Int = getColumnIndexOrThrow(_cursor, "transaction_id")
          val _cursorIndexOfTxType: Int = getColumnIndexOrThrow(_cursor, "tx_type")
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfAssetId: Int = getColumnIndexOrThrow(_cursor, "asset_id")
          val _cursorIndexOfTotalBillBdt: Int = getColumnIndexOrThrow(_cursor, "total_bill_bdt")
          val _cursorIndexOfCashAmountBdt: Int = getColumnIndexOrThrow(_cursor, "cash_amount_bdt")
          val _cursorIndexOfBakiDeltaBdt: Int = getColumnIndexOrThrow(_cursor, "baki_delta_bdt")
          val _cursorIndexOfRunningShopkeeperBakiBdt: Int = getColumnIndexOrThrow(_cursor,
              "running_shopkeeper_baki_bdt")
          val _cursorIndexOfEventDateMillis: Int = getColumnIndexOrThrow(_cursor,
              "event_date_millis")
          val _cursorIndexOfDateKey: Int = getColumnIndexOrThrow(_cursor, "date_key")
          val _cursorIndexOfYearMonth: Int = getColumnIndexOrThrow(_cursor, "year_month")
          val _cursorIndexOfNoteBn: Int = getColumnIndexOrThrow(_cursor, "note_bn")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfVersion: Int = getColumnIndexOrThrow(_cursor, "version")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<TransactionEntity> =
              ArrayList<TransactionEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: TransactionEntity
            val _tmpTransactionId: String
            _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId)
            val _tmpTxType: String
            _tmpTxType = _cursor.getString(_cursorIndexOfTxType)
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpVendorId: String?
            if (_cursor.isNull(_cursorIndexOfVendorId)) {
              _tmpVendorId = null
            } else {
              _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            }
            val _tmpShopkeeperId: String?
            if (_cursor.isNull(_cursorIndexOfShopkeeperId)) {
              _tmpShopkeeperId = null
            } else {
              _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            }
            val _tmpAssetId: String?
            if (_cursor.isNull(_cursorIndexOfAssetId)) {
              _tmpAssetId = null
            } else {
              _tmpAssetId = _cursor.getString(_cursorIndexOfAssetId)
            }
            val _tmpTotalBillBdt: Long
            _tmpTotalBillBdt = _cursor.getLong(_cursorIndexOfTotalBillBdt)
            val _tmpCashAmountBdt: Long
            _tmpCashAmountBdt = _cursor.getLong(_cursorIndexOfCashAmountBdt)
            val _tmpBakiDeltaBdt: Long
            _tmpBakiDeltaBdt = _cursor.getLong(_cursorIndexOfBakiDeltaBdt)
            val _tmpRunningShopkeeperBakiBdt: Long
            _tmpRunningShopkeeperBakiBdt = _cursor.getLong(_cursorIndexOfRunningShopkeeperBakiBdt)
            val _tmpEventDateMillis: Long
            _tmpEventDateMillis = _cursor.getLong(_cursorIndexOfEventDateMillis)
            val _tmpDateKey: String
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey)
            val _tmpYearMonth: String
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth)
            val _tmpNoteBn: String
            _tmpNoteBn = _cursor.getString(_cursorIndexOfNoteBn)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpVersion: Int
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                TransactionEntity(_tmpTransactionId,_tmpTxType,_tmpCategoryId,_tmpVendorId,_tmpShopkeeperId,_tmpAssetId,_tmpTotalBillBdt,_tmpCashAmountBdt,_tmpBakiDeltaBdt,_tmpRunningShopkeeperBakiBdt,_tmpEventDateMillis,_tmpDateKey,_tmpYearMonth,_tmpNoteBn,_tmpCreatedAt,_tmpUpdatedAt,_tmpVersion,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun markTransactionsSynced(transactionIds: List<String>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stringBuilder: StringBuilder = newStringBuilder()
      _stringBuilder.append("UPDATE transactions SET sync_status = 'SYNCED' WHERE transaction_id IN (")
      val _inputSize: Int = transactionIds.size
      appendPlaceholders(_stringBuilder, _inputSize)
      _stringBuilder.append(")")
      val _sql: String = _stringBuilder.toString()
      val _stmt: SupportSQLiteStatement = __db.compileStatement(_sql)
      var _argIndex: Int = 1
      for (_item: String in transactionIds) {
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
