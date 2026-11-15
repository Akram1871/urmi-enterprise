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
import com.akram.babsharhisab.`data`.local.entity.ShopkeeperEntity
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
public class ShopkeeperDao_Impl(
  __db: RoomDatabase,
) : ShopkeeperDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfShopkeeperEntity: EntityInsertionAdapter<ShopkeeperEntity>

  private val __updateAdapterOfShopkeeperEntity: EntityDeletionOrUpdateAdapter<ShopkeeperEntity>

  private val __preparedStmtOfUpdateShopkeeperBakiBalance: SharedSQLiteStatement

  private val __preparedStmtOfSoftDeleteShopkeeper: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfShopkeeperEntity = object :
        EntityInsertionAdapter<ShopkeeperEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `shopkeepers` (`shopkeeper_id`,`name_bn`,`shop_name_bn`,`market_area_bn`,`phone_number`,`opening_baki_bdt`,`current_baki_bdt`,`last_transaction_at`,`created_at`,`updated_at`,`is_deleted`,`sync_status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ShopkeeperEntity) {
        statement.bindString(1, entity.shopkeeperId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.shopNameBn)
        statement.bindString(4, entity.marketAreaBn)
        statement.bindString(5, entity.phoneNumber)
        statement.bindLong(6, entity.openingBakiBdt)
        statement.bindLong(7, entity.currentBakiBdt)
        statement.bindLong(8, entity.lastTransactionAt)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindString(12, entity.syncStatus)
      }
    }
    this.__updateAdapterOfShopkeeperEntity = object :
        EntityDeletionOrUpdateAdapter<ShopkeeperEntity>(__db) {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `shopkeepers` SET `shopkeeper_id` = ?,`name_bn` = ?,`shop_name_bn` = ?,`market_area_bn` = ?,`phone_number` = ?,`opening_baki_bdt` = ?,`current_baki_bdt` = ?,`last_transaction_at` = ?,`created_at` = ?,`updated_at` = ?,`is_deleted` = ?,`sync_status` = ? WHERE `shopkeeper_id` = ?"

      protected override fun bind(statement: SupportSQLiteStatement, entity: ShopkeeperEntity) {
        statement.bindString(1, entity.shopkeeperId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.shopNameBn)
        statement.bindString(4, entity.marketAreaBn)
        statement.bindString(5, entity.phoneNumber)
        statement.bindLong(6, entity.openingBakiBdt)
        statement.bindLong(7, entity.currentBakiBdt)
        statement.bindLong(8, entity.lastTransactionAt)
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindString(12, entity.syncStatus)
        statement.bindString(13, entity.shopkeeperId)
      }
    }
    this.__preparedStmtOfUpdateShopkeeperBakiBalance = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE shopkeepers
            |        SET current_baki_bdt = ?,
            |            last_transaction_at = ?,
            |            updated_at = ?,
            |            sync_status = 'PENDING'
            |        WHERE shopkeeper_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
    this.__preparedStmtOfSoftDeleteShopkeeper = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = """
            |
            |        UPDATE shopkeepers
            |        SET is_deleted = 1,
            |            updated_at = ?,
            |            sync_status = 'PENDING'
            |        WHERE shopkeeper_id = ?
            |    
            """.trimMargin()
        return _query
      }
    }
  }

  public override suspend fun upsertShopkeeper(shopkeeper: ShopkeeperEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfShopkeeperEntity.insert(shopkeeper)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateShopkeeper(shopkeeper: ShopkeeperEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __updateAdapterOfShopkeeperEntity.handle(shopkeeper)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun updateShopkeeperBakiBalance(
    shopkeeperId: String,
    newBakiBdt: Long,
    eventMillis: Long,
    updatedAt: Long,
  ): Unit = CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfUpdateShopkeeperBakiBalance.acquire()
      var _argIndex: Int = 1
      _stmt.bindLong(_argIndex, newBakiBdt)
      _argIndex = 2
      _stmt.bindLong(_argIndex, eventMillis)
      _argIndex = 3
      _stmt.bindLong(_argIndex, updatedAt)
      _argIndex = 4
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
        __preparedStmtOfUpdateShopkeeperBakiBalance.release(_stmt)
      }
    }
  })

  public override suspend fun softDeleteShopkeeper(shopkeeperId: String, updatedAt: Long): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfSoftDeleteShopkeeper.acquire()
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
        __preparedStmtOfSoftDeleteShopkeeper.release(_stmt)
      }
    }
  })

  public override fun observeActiveShopkeepers(): Flow<List<ShopkeeperEntity>> {
    val _sql: String =
        "SELECT * FROM shopkeepers WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, name_bn ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("shopkeepers"), object :
        Callable<List<ShopkeeperEntity>> {
      public override fun call(): List<ShopkeeperEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfShopNameBn: Int = getColumnIndexOrThrow(_cursor, "shop_name_bn")
          val _cursorIndexOfMarketAreaBn: Int = getColumnIndexOrThrow(_cursor, "market_area_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfOpeningBakiBdt: Int = getColumnIndexOrThrow(_cursor, "opening_baki_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfLastTransactionAt: Int = getColumnIndexOrThrow(_cursor,
              "last_transaction_at")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<ShopkeeperEntity> =
              ArrayList<ShopkeeperEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ShopkeeperEntity
            val _tmpShopkeeperId: String
            _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpShopNameBn: String
            _tmpShopNameBn = _cursor.getString(_cursorIndexOfShopNameBn)
            val _tmpMarketAreaBn: String
            _tmpMarketAreaBn = _cursor.getString(_cursorIndexOfMarketAreaBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpOpeningBakiBdt: Long
            _tmpOpeningBakiBdt = _cursor.getLong(_cursorIndexOfOpeningBakiBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpLastTransactionAt: Long
            _tmpLastTransactionAt = _cursor.getLong(_cursorIndexOfLastTransactionAt)
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
                ShopkeeperEntity(_tmpShopkeeperId,_tmpNameBn,_tmpShopNameBn,_tmpMarketAreaBn,_tmpPhoneNumber,_tmpOpeningBakiBdt,_tmpCurrentBakiBdt,_tmpLastTransactionAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getActiveShopkeepers(): List<ShopkeeperEntity> {
    val _sql: String =
        "SELECT * FROM shopkeepers WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, name_bn ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<ShopkeeperEntity>> {
      public override fun call(): List<ShopkeeperEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfShopNameBn: Int = getColumnIndexOrThrow(_cursor, "shop_name_bn")
          val _cursorIndexOfMarketAreaBn: Int = getColumnIndexOrThrow(_cursor, "market_area_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfOpeningBakiBdt: Int = getColumnIndexOrThrow(_cursor, "opening_baki_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfLastTransactionAt: Int = getColumnIndexOrThrow(_cursor,
              "last_transaction_at")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<ShopkeeperEntity> =
              ArrayList<ShopkeeperEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ShopkeeperEntity
            val _tmpShopkeeperId: String
            _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpShopNameBn: String
            _tmpShopNameBn = _cursor.getString(_cursorIndexOfShopNameBn)
            val _tmpMarketAreaBn: String
            _tmpMarketAreaBn = _cursor.getString(_cursorIndexOfMarketAreaBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpOpeningBakiBdt: Long
            _tmpOpeningBakiBdt = _cursor.getLong(_cursorIndexOfOpeningBakiBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpLastTransactionAt: Long
            _tmpLastTransactionAt = _cursor.getLong(_cursorIndexOfLastTransactionAt)
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
                ShopkeeperEntity(_tmpShopkeeperId,_tmpNameBn,_tmpShopNameBn,_tmpMarketAreaBn,_tmpPhoneNumber,_tmpOpeningBakiBdt,_tmpCurrentBakiBdt,_tmpLastTransactionAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeShopkeeperById(shopkeeperId: String): Flow<ShopkeeperEntity?> {
    val _sql: String = "SELECT * FROM shopkeepers WHERE shopkeeper_id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, shopkeeperId)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("shopkeepers"), object :
        Callable<ShopkeeperEntity?> {
      public override fun call(): ShopkeeperEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfShopNameBn: Int = getColumnIndexOrThrow(_cursor, "shop_name_bn")
          val _cursorIndexOfMarketAreaBn: Int = getColumnIndexOrThrow(_cursor, "market_area_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfOpeningBakiBdt: Int = getColumnIndexOrThrow(_cursor, "opening_baki_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfLastTransactionAt: Int = getColumnIndexOrThrow(_cursor,
              "last_transaction_at")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: ShopkeeperEntity?
          if (_cursor.moveToFirst()) {
            val _tmpShopkeeperId: String
            _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpShopNameBn: String
            _tmpShopNameBn = _cursor.getString(_cursorIndexOfShopNameBn)
            val _tmpMarketAreaBn: String
            _tmpMarketAreaBn = _cursor.getString(_cursorIndexOfMarketAreaBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpOpeningBakiBdt: Long
            _tmpOpeningBakiBdt = _cursor.getLong(_cursorIndexOfOpeningBakiBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpLastTransactionAt: Long
            _tmpLastTransactionAt = _cursor.getLong(_cursorIndexOfLastTransactionAt)
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
                ShopkeeperEntity(_tmpShopkeeperId,_tmpNameBn,_tmpShopNameBn,_tmpMarketAreaBn,_tmpPhoneNumber,_tmpOpeningBakiBdt,_tmpCurrentBakiBdt,_tmpLastTransactionAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun getShopkeeperById(shopkeeperId: String): ShopkeeperEntity? {
    val _sql: String = "SELECT * FROM shopkeepers WHERE shopkeeper_id = ? LIMIT 1"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, shopkeeperId)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<ShopkeeperEntity?> {
      public override fun call(): ShopkeeperEntity? {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfShopNameBn: Int = getColumnIndexOrThrow(_cursor, "shop_name_bn")
          val _cursorIndexOfMarketAreaBn: Int = getColumnIndexOrThrow(_cursor, "market_area_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfOpeningBakiBdt: Int = getColumnIndexOrThrow(_cursor, "opening_baki_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfLastTransactionAt: Int = getColumnIndexOrThrow(_cursor,
              "last_transaction_at")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: ShopkeeperEntity?
          if (_cursor.moveToFirst()) {
            val _tmpShopkeeperId: String
            _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpShopNameBn: String
            _tmpShopNameBn = _cursor.getString(_cursorIndexOfShopNameBn)
            val _tmpMarketAreaBn: String
            _tmpMarketAreaBn = _cursor.getString(_cursorIndexOfMarketAreaBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpOpeningBakiBdt: Long
            _tmpOpeningBakiBdt = _cursor.getLong(_cursorIndexOfOpeningBakiBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpLastTransactionAt: Long
            _tmpLastTransactionAt = _cursor.getLong(_cursorIndexOfLastTransactionAt)
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
                ShopkeeperEntity(_tmpShopkeeperId,_tmpNameBn,_tmpShopNameBn,_tmpMarketAreaBn,_tmpPhoneNumber,_tmpOpeningBakiBdt,_tmpCurrentBakiBdt,_tmpLastTransactionAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override fun observeTotalMarketBaki(): Flow<Long> {
    val _sql: String =
        "SELECT COALESCE(SUM(current_baki_bdt), 0) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("shopkeepers"), object : Callable<Long> {
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

  public override suspend fun getTotalMarketBaki(): Long {
    val _sql: String =
        "SELECT COALESCE(SUM(current_baki_bdt), 0) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0"
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

  public override fun observeActiveBakiShopkeeperCount(): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("shopkeepers"), object : Callable<Int> {
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

  public override suspend fun getUnsyncedShopkeepers(): List<ShopkeeperEntity> {
    val _sql: String = "SELECT * FROM shopkeepers WHERE sync_status != 'SYNCED'"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<ShopkeeperEntity>> {
      public override fun call(): List<ShopkeeperEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfShopkeeperId: Int = getColumnIndexOrThrow(_cursor, "shopkeeper_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfShopNameBn: Int = getColumnIndexOrThrow(_cursor, "shop_name_bn")
          val _cursorIndexOfMarketAreaBn: Int = getColumnIndexOrThrow(_cursor, "market_area_bn")
          val _cursorIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_cursor, "phone_number")
          val _cursorIndexOfOpeningBakiBdt: Int = getColumnIndexOrThrow(_cursor, "opening_baki_bdt")
          val _cursorIndexOfCurrentBakiBdt: Int = getColumnIndexOrThrow(_cursor, "current_baki_bdt")
          val _cursorIndexOfLastTransactionAt: Int = getColumnIndexOrThrow(_cursor,
              "last_transaction_at")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "created_at")
          val _cursorIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_cursor, "updated_at")
          val _cursorIndexOfIsDeleted: Int = getColumnIndexOrThrow(_cursor, "is_deleted")
          val _cursorIndexOfSyncStatus: Int = getColumnIndexOrThrow(_cursor, "sync_status")
          val _result: MutableList<ShopkeeperEntity> =
              ArrayList<ShopkeeperEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: ShopkeeperEntity
            val _tmpShopkeeperId: String
            _tmpShopkeeperId = _cursor.getString(_cursorIndexOfShopkeeperId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpShopNameBn: String
            _tmpShopNameBn = _cursor.getString(_cursorIndexOfShopNameBn)
            val _tmpMarketAreaBn: String
            _tmpMarketAreaBn = _cursor.getString(_cursorIndexOfMarketAreaBn)
            val _tmpPhoneNumber: String
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber)
            val _tmpOpeningBakiBdt: Long
            _tmpOpeningBakiBdt = _cursor.getLong(_cursorIndexOfOpeningBakiBdt)
            val _tmpCurrentBakiBdt: Long
            _tmpCurrentBakiBdt = _cursor.getLong(_cursorIndexOfCurrentBakiBdt)
            val _tmpLastTransactionAt: Long
            _tmpLastTransactionAt = _cursor.getLong(_cursorIndexOfLastTransactionAt)
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
                ShopkeeperEntity(_tmpShopkeeperId,_tmpNameBn,_tmpShopNameBn,_tmpMarketAreaBn,_tmpPhoneNumber,_tmpOpeningBakiBdt,_tmpCurrentBakiBdt,_tmpLastTransactionAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpSyncStatus)
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

  public override suspend fun markShopkeepersSynced(shopkeeperIds: List<String>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      val _stringBuilder: StringBuilder = newStringBuilder()
      _stringBuilder.append("UPDATE shopkeepers SET sync_status = 'SYNCED' WHERE shopkeeper_id IN (")
      val _inputSize: Int = shopkeeperIds.size
      appendPlaceholders(_stringBuilder, _inputSize)
      _stringBuilder.append(")")
      val _sql: String = _stringBuilder.toString()
      val _stmt: SupportSQLiteStatement = __db.compileStatement(_sql)
      var _argIndex: Int = 1
      for (_item: String in shopkeeperIds) {
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
