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
import com.akram.babsharhisab.`data`.local.entity.VendorEntity
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
public class VendorDao_Impl(
  __db: RoomDatabase,
) : VendorDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfVendorEntity: EntityInsertionAdapter<VendorEntity>

  private val __updateAdapterOfVendorEntity: EntityDeletionOrUpdateAdapter<VendorEntity>

  private val __preparedStmtOfIncrementVendorPurchaseAndBaki: SharedSQLiteStatement

  private val __preparedStmtOfUpdateVendorBakiBalance: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteVendor: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfVendorEntity = object : EntityInsertionAdapter<VendorEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `vendors` (`vendor_id`,`name_bn`,`market_location_bn`,`phone_number`,`total_purchased_bdt`,`current_baki_bdt`,`created_at`,`updated_at`,`is_deleted`,`sync_status`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VendorEntity) {
        statement.bindString(1, entity.vendorId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.marketLocationBn)
        statement.bindString(4, entity.phoneNumber)
        statement.bindLong(5, entity.totalPurchasedBdt)
        statement.bindLong(6, entity.currentBakiBdt)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindString(10, entity.syncStatus)
      }
    }
    this.__updateAdapterOfVendorEntity = object : EntityDeletionOrUpdateAdapter<VendorEntity>(__db)
        {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `vendors` SET `vendor_id` = ?,`name_bn` = ?,`market_location_bn` = ?,`phone_number` = ?,`total_purchased_bdt` = ?,`current_baki_bdt` = ?,`created_at` = ?,`updated_at` = ?,`is_deleted` = ?,`sync_status` = ? WHERE `vendor_id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: VendorEntity) {
        statement.bindString(1, entity.vendorId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.marketLocationBn)
        statement.bindString(4, entity.phoneNumber)
        statement.bindLong(5, entity.totalPurchasedBdt)
        statement.bindLong(6, entity.currentBakiBdt)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindString(10, entity.syncStatus)
        statement.bindString(11, entity.vendorId)
      }
    }
    this.__preparedStmtOfIncrementVendorPurchaseAndBaki = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE vendors
            |        SET total_purchased_bdt = MAX(0, total_purchased_bdt + ?),
            |            current_baki_bdt = MAX(0, current_baki_bdt + ?),
            |            updated_at = ?,
            |            sync_status = 'PENDING'
            |        WHERE vendor_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
    this.__preparedStmtOfUpdateVendorBakiBalance = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE vendors
            |        SET current_baki_bdt = MAX(0, ?),
            |            updated_at = ?,
            |            sync_status = 'PENDING'
            |        WHERE vendor_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteVendor = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE vendors
            |        SET is_deleted = 1,
            |            updated_at = ?,
            |            sync_status = 'PENDING'
            |        WHERE vendor_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
  }

  public override suspend fun upsertVendor(vendor: VendorEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfVendorEntity.insert(vendor)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateVendor(vendor: VendorEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfVendorEntity.handle(vendor)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun incrementVendorPurchaseAndBaki(
    vendorId: String,
    deltaBdt: Long,
    bakiDeltaBdt: Long,
    updatedAt: Long,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfIncrementVendorPurchaseAndBaki.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, deltaBdt)
      _argIndex = 2
      _stmt.bindLong(_argIndex, bakiDeltaBdt)
      _argIndex = 3
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 4
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
        __preparedStmtOfIncrementVendorPurchaseAndBaki.release(_stmt)
      }
    }
  })

  public override suspend fun updateVendorBakiBalance(
    vendorId: String,
    newBakiBdt: Long,
    updatedAt: Long,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateVendorBakiBalance.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, newBakiBdt)
      _argIndex = 2
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 3
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
        __preparedStmtOfUpdateVendorBakiBalance.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteVendor(vendorId: String, updatedAt: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteVendor.acquire()
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
        __preparedStmtOfSoftDeleteVendor.release(_stmt)
      }
    }
  })

  public override fun observeActiveVendors(): Flow<List<VendorEntity>> {
    val _sql: String =
        "SELECT * FROM vendors WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, total_purchased_bdt DESC, name_bn ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("vendors"), object :
        Callable<List<VendorEntity>> {
      public override fun call(): List<VendorEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfMarketLocationBn: Int = getColumnIndexOrThrow(_cursor,
              "market_location_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfTotalPurchasedBdt: Int = getColumnIndexOrThrow(_cursor,
              "total_purchased_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<VendorEntity> = ArrayList<VendorEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VendorEntity
            val _tmpVendorId: String
            _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpMarketLocationBn: String
            _tmpMarketLocationBn = _cursor.getString(_cursorIndexOfMarketLocationBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpTotalPurchasedBdt: Long
            _tmpTotalPurchasedBdt = _cursor.getLong(_cursorIndexOfTotalPurchasedBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                VendorEntity(_tmpVendorId,_tmpNameBn,_tmpMarketLocationBn,_tmpPhoneNumber,_tmpTotalPurchasedBdt,_tmpCurrentBakiBdt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getActiveVendors(): List<VendorEntity> {
    val _sql: String =
        "SELECT * FROM vendors WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, total_purchased_bdt DESC, name_bn ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<VendorEntity>> {
      public override fun call(): List<VendorEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfMarketLocationBn: Int = getColumnIndexOrThrow(_cursor,
              "market_location_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfTotalPurchasedBdt: Int = getColumnIndexOrThrow(_cursor,
              "total_purchased_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<VendorEntity> = ArrayList<VendorEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VendorEntity
            val _tmpVendorId: String
            _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpMarketLocationBn: String
            _tmpMarketLocationBn = _cursor.getString(_cursorIndexOfMarketLocationBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpTotalPurchasedBdt: Long
            _tmpTotalPurchasedBdt = _cursor.getLong(_cursorIndexOfTotalPurchasedBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                VendorEntity(_tmpVendorId,_tmpNameBn,_tmpMarketLocationBn,_tmpPhoneNumber,_tmpTotalPurchasedBdt,_tmpCurrentBakiBdt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getVendorById(vendorId: String): VendorEntity? {
    val _sql: String = "SELECT * FROM vendors WHERE vendor_id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, vendorId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<VendorEntity?> {
      public override fun call(): VendorEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfMarketLocationBn: Int = getColumnIndexOrThrow(_cursor,
              "market_location_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfTotalPurchasedBdt: Int = getColumnIndexOrThrow(_cursor,
              "total_purchased_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: VendorEntity?
          if (_cursor.moveToFirst()) {
            val _tmpVendorId: String
            _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpMarketLocationBn: String
            _tmpMarketLocationBn = _cursor.getString(_cursorIndexOfMarketLocationBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpTotalPurchasedBdt: Long
            _tmpTotalPurchasedBdt = _cursor.getLong(_cursorIndexOfTotalPurchasedBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _result =
                VendorEntity(_tmpVendorId,_tmpNameBn,_tmpMarketLocationBn,_tmpPhoneNumber,_tmpTotalPurchasedBdt,_tmpCurrentBakiBdt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeTotalVendorBaki(): Flow<Long> {
    val _sql: String =
        "SELECT COALESCE(SUM(current_baki_bdt), 0) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("vendors"), object : Callable<Long> {
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

  public override suspend fun getTotalVendorBaki(): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(current_baki_bdt), 0) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0"
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

  public override fun observeActiveBakiVendorCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("vendors"), object : Callable<Int> {
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

  public override suspend fun getUnsyncedVendors(): List<VendorEntity> {
    val _sql: String = "SELECT * FROM vendors WHERE sync_status != 'SYNCED'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<VendorEntity>> {
      public override fun call(): List<VendorEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfVendorId: Int = getColumnIndexOrThrow(_cursor, "vendor_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfMarketLocationBn: Int = getColumnIndexOrThrow(_cursor,
              "market_location_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfTotalPurchasedBdt: Int = getColumnIndexOrThrow(_cursor,
              "total_purchased_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<VendorEntity> = ArrayList<VendorEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: VendorEntity
            val _tmpVendorId: String
            _tmpVendorId = _cursor.getString(_cursorIndexOfVendorId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpMarketLocationBn: String
            _tmpMarketLocationBn = _cursor.getString(_cursorIndexOfMarketLocationBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpTotalPurchasedBdt: Long
            _tmpTotalPurchasedBdt = _cursor.getLong(_cursorIndexOfTotalPurchasedBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpCreatedAt: Long
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt)
            val _tmpUpdatedAt: Long
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt)
            val _tmpIsDeleted: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsDeleted)
            _tmpIsDeleted = _tmp != 0
            val _tmpSyncStatus: String
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus)
            _item =
                VendorEntity(_tmpVendorId,_tmpNameBn,_tmpMarketLocationBn,_tmpPhoneNumber,_tmpTotalPurchasedBdt,_tmpCurrentBakiBdt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun markVendorsSynced(vendorIds: List<String>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stringBuilder: StringBuilder = newStringBuilder()
      _stringBuilder.append("UPDATE vendors SET sync_status = 'SYNCED' WHERE vendor_id IN (")
      val _inputSize: Int = vendorIds.size
      appendPlaceholders(_stringBuilder, _inputSize)
      _stringBuilder.append(")")
      val _sql: String = _stringBuilder.toString()
      val _stmt: SupportSQLiteStatement = __db.compileStatement(_sql)
      var _argIndex: Int = 1
      for (_item: String in vendorIds) {
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
