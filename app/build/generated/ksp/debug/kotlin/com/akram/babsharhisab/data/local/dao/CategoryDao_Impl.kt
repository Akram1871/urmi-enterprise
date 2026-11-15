package com.akram.babsharhisab.`data`.local.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.akram.babsharhisab.`data`.local.entity.CategoryEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class CategoryDao_Impl(
  __db: RoomDatabase,
) : CategoryDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCategoryEntity: EntityInsertionAdapter<CategoryEntity>

  private val __insertionAdapterOfCategoryEntity_1: EntityInsertionAdapter<CategoryEntity>
  init {
    this.__db = __db
    this.__insertionAdapterOfCategoryEntity = object : EntityInsertionAdapter<CategoryEntity>(__db)
        {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `categories` (`category_id`,`name_bn`,`type`,`icon_name`,`color_hex`,`sort_order`,`is_system`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CategoryEntity) {
        statement.bindString(1, entity.categoryId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.type)
        statement.bindString(4, entity.iconName)
        statement.bindString(5, entity.colorHex)
        statement.bindLong(6, entity.sortOrder.toLong())
        val _tmp: Int = if (entity.isSystem) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__insertionAdapterOfCategoryEntity_1 = object :
        EntityInsertionAdapter<CategoryEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `categories` (`category_id`,`name_bn`,`type`,`icon_name`,`color_hex`,`sort_order`,`is_system`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CategoryEntity) {
        statement.bindString(1, entity.categoryId)
        statement.bindString(2, entity.nameBn)
        statement.bindString(3, entity.type)
        statement.bindString(4, entity.iconName)
        statement.bindString(5, entity.colorHex)
        statement.bindLong(6, entity.sortOrder.toLong())
        val _tmp: Int = if (entity.isSystem) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
  }

  public override suspend fun insertCategoriesIgnore(categories: List<CategoryEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCategoryEntity.insert(categories)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun upsertCategory(category: CategoryEntity): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCategoryEntity_1.insert(category)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override fun observeAllCategories(): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories ORDER BY sort_order ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("categories"), object :
        Callable<List<CategoryEntity>> {
      public override fun call(): List<CategoryEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "icon_name")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "color_hex")
          val _cursorIndexOfSortOrder: Int = getColumnIndexOrThrow(_cursor, "sort_order")
          val _cursorIndexOfIsSystem: Int = getColumnIndexOrThrow(_cursor, "is_system")
          val _result: MutableList<CategoryEntity> = ArrayList<CategoryEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CategoryEntity
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpSortOrder: Int
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder)
            val _tmpIsSystem: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsSystem)
            _tmpIsSystem = _tmp != 0
            _item =
                CategoryEntity(_tmpCategoryId,_tmpNameBn,_tmpType,_tmpIconName,_tmpColorHex,_tmpSortOrder,_tmpIsSystem)
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

  public override suspend fun getAllCategories(): List<CategoryEntity> {
    val _sql: String = "SELECT * FROM categories ORDER BY sort_order ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<CategoryEntity>> {
      public override fun call(): List<CategoryEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "icon_name")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "color_hex")
          val _cursorIndexOfSortOrder: Int = getColumnIndexOrThrow(_cursor, "sort_order")
          val _cursorIndexOfIsSystem: Int = getColumnIndexOrThrow(_cursor, "is_system")
          val _result: MutableList<CategoryEntity> = ArrayList<CategoryEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CategoryEntity
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpSortOrder: Int
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder)
            val _tmpIsSystem: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsSystem)
            _tmpIsSystem = _tmp != 0
            _item =
                CategoryEntity(_tmpCategoryId,_tmpNameBn,_tmpType,_tmpIconName,_tmpColorHex,_tmpSortOrder,_tmpIsSystem)
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

  public override fun observeCategoriesByType(type: String): Flow<List<CategoryEntity>> {
    val _sql: String = "SELECT * FROM categories WHERE type = ? ORDER BY sort_order ASC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, type)
    return CoroutinesRoom.createFlow(__db, false, arrayOf("categories"), object :
        Callable<List<CategoryEntity>> {
      public override fun call(): List<CategoryEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfCategoryId: Int = getColumnIndexOrThrow(_cursor, "category_id")
          val _cursorIndexOfNameBn: Int = getColumnIndexOrThrow(_cursor, "name_bn")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfIconName: Int = getColumnIndexOrThrow(_cursor, "icon_name")
          val _cursorIndexOfColorHex: Int = getColumnIndexOrThrow(_cursor, "color_hex")
          val _cursorIndexOfSortOrder: Int = getColumnIndexOrThrow(_cursor, "sort_order")
          val _cursorIndexOfIsSystem: Int = getColumnIndexOrThrow(_cursor, "is_system")
          val _result: MutableList<CategoryEntity> = ArrayList<CategoryEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CategoryEntity
            val _tmpCategoryId: String
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId)
            val _tmpNameBn: String
            _tmpNameBn = _cursor.getString(_cursorIndexOfNameBn)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpIconName: String
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName)
            val _tmpColorHex: String
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex)
            val _tmpSortOrder: Int
            _tmpSortOrder = _cursor.getInt(_cursorIndexOfSortOrder)
            val _tmpIsSystem: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfIsSystem)
            _tmpIsSystem = _tmp != 0
            _item =
                CategoryEntity(_tmpCategoryId,_tmpNameBn,_tmpType,_tmpIconName,_tmpColorHex,_tmpSortOrder,_tmpIsSystem)
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

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
