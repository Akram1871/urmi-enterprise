package com.akram.babsharhisab.`data`.local

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.akram.babsharhisab.`data`.local.dao.AssetDao
import com.akram.babsharhisab.`data`.local.dao.AssetDao_Impl
import com.akram.babsharhisab.`data`.local.dao.CategoryDao
import com.akram.babsharhisab.`data`.local.dao.CategoryDao_Impl
import com.akram.babsharhisab.`data`.local.dao.ShopkeeperDao
import com.akram.babsharhisab.`data`.local.dao.ShopkeeperDao_Impl
import com.akram.babsharhisab.`data`.local.dao.SyncAuditDao
import com.akram.babsharhisab.`data`.local.dao.SyncAuditDao_Impl
import com.akram.babsharhisab.`data`.local.dao.TransactionDao
import com.akram.babsharhisab.`data`.local.dao.TransactionDao_Impl
import com.akram.babsharhisab.`data`.local.dao.VendorDao
import com.akram.babsharhisab.`data`.local.dao.VendorDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Boolean
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class AppDatabase_Impl : AppDatabase() {
  private val _categoryDao: Lazy<CategoryDao> = lazy {
    CategoryDao_Impl(this)
  }


  private val _vendorDao: Lazy<VendorDao> = lazy {
    VendorDao_Impl(this)
  }


  private val _shopkeeperDao: Lazy<ShopkeeperDao> = lazy {
    ShopkeeperDao_Impl(this)
  }


  private val _assetDao: Lazy<AssetDao> = lazy {
    AssetDao_Impl(this)
  }


  private val _transactionDao: Lazy<TransactionDao> = lazy {
    TransactionDao_Impl(this)
  }


  private val _syncAuditDao: Lazy<SyncAuditDao> = lazy {
    SyncAuditDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(3) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`category_id` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `type` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `color_hex` TEXT NOT NULL, `sort_order` INTEGER NOT NULL, `is_system` INTEGER NOT NULL, PRIMARY KEY(`category_id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `vendors` (`vendor_id` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `market_location_bn` TEXT NOT NULL, `phone_number` TEXT NOT NULL, `total_purchased_bdt` INTEGER NOT NULL, `current_baki_bdt` INTEGER NOT NULL DEFAULT 0, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `sync_status` TEXT NOT NULL, PRIMARY KEY(`vendor_id`))")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vendors_sync_status` ON `vendors` (`sync_status`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vendors_total_purchased` ON `vendors` (`is_deleted`, `total_purchased_bdt`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `shopkeepers` (`shopkeeper_id` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `shop_name_bn` TEXT NOT NULL, `market_area_bn` TEXT NOT NULL, `phone_number` TEXT NOT NULL, `opening_baki_bdt` INTEGER NOT NULL, `current_baki_bdt` INTEGER NOT NULL, `last_transaction_at` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `sync_status` TEXT NOT NULL, PRIMARY KEY(`shopkeeper_id`))")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_shopkeepers_baki_balance` ON `shopkeepers` (`is_deleted`, `current_baki_bdt`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_shopkeepers_sync_status` ON `shopkeepers` (`sync_status`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `assets_maintenance` (`asset_id` TEXT NOT NULL, `asset_name_bn` TEXT NOT NULL, `category_id` TEXT NOT NULL, `purchase_cost_bdt` INTEGER NOT NULL, `lifespan_months` INTEGER NOT NULL, `purchase_date_millis` INTEGER NOT NULL, `next_due_date_millis` INTEGER NOT NULL, `daily_depreciation_bdt` INTEGER NOT NULL, `monthly_depreciation_bdt` INTEGER NOT NULL, `notes_bn` TEXT NOT NULL, `is_active` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `sync_status` TEXT NOT NULL, PRIMARY KEY(`asset_id`), FOREIGN KEY(`category_id`) REFERENCES `categories`(`category_id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_category_id` ON `assets_maintenance` (`category_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_active_due` ON `assets_maintenance` (`is_deleted`, `is_active`, `next_due_date_millis`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_sync_status` ON `assets_maintenance` (`sync_status`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`transaction_id` TEXT NOT NULL, `tx_type` TEXT NOT NULL, `category_id` TEXT NOT NULL, `vendor_id` TEXT, `shopkeeper_id` TEXT, `asset_id` TEXT, `total_bill_bdt` INTEGER NOT NULL, `cash_amount_bdt` INTEGER NOT NULL, `baki_delta_bdt` INTEGER NOT NULL, `running_shopkeeper_baki_bdt` INTEGER NOT NULL, `event_date_millis` INTEGER NOT NULL, `date_key` TEXT NOT NULL, `year_month` TEXT NOT NULL, `note_bn` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `version` INTEGER NOT NULL, `is_deleted` INTEGER NOT NULL, `sync_status` TEXT NOT NULL, PRIMARY KEY(`transaction_id`), FOREIGN KEY(`category_id`) REFERENCES `categories`(`category_id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`vendor_id`) REFERENCES `vendors`(`vendor_id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`shopkeeper_id`) REFERENCES `shopkeepers`(`shopkeeper_id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`asset_id`) REFERENCES `assets_maintenance`(`asset_id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_category_id` ON `transactions` (`category_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_vendor_id` ON `transactions` (`vendor_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_shopkeeper_id` ON `transactions` (`shopkeeper_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_asset_id` ON `transactions` (`asset_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_date_key` ON `transactions` (`is_deleted`, `date_key`, `tx_type`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_year_month_cat` ON `transactions` (`is_deleted`, `year_month`, `category_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_shopkeeper_ledger` ON `transactions` (`is_deleted`, `shopkeeper_id`, `event_date_millis`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_vendor_ledger` ON `transactions` (`is_deleted`, `vendor_id`, `event_date_millis`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tx_sync_queue` ON `transactions` (`sync_status`, `updated_at`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_audit_logs` (`log_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `executed_at` INTEGER NOT NULL, `synced_records_count` INTEGER NOT NULL, `status` TEXT NOT NULL, `message_bn` TEXT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7df86a7116a67dc9767238e817c8551d')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `categories`")
        db.execSQL("DROP TABLE IF EXISTS `vendors`")
        db.execSQL("DROP TABLE IF EXISTS `shopkeepers`")
        db.execSQL("DROP TABLE IF EXISTS `assets_maintenance`")
        db.execSQL("DROP TABLE IF EXISTS `transactions`")
        db.execSQL("DROP TABLE IF EXISTS `sync_audit_logs`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        db.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsCategories: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(7)
        _columnsCategories.put("category_id", TableInfo.Column("category_id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("name_bn", TableInfo.Column("name_bn", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("icon_name", TableInfo.Column("icon_name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("color_hex", TableInfo.Column("color_hex", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("sort_order", TableInfo.Column("sort_order", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCategories.put("is_system", TableInfo.Column("is_system", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCategories: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesCategories: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoCategories: TableInfo = TableInfo("categories", _columnsCategories,
            _foreignKeysCategories, _indicesCategories)
        val _existingCategories: TableInfo = read(db, "categories")
        if (!_infoCategories.equals(_existingCategories)) {
          return RoomOpenHelper.ValidationResult(false, """
              |categories(com.akram.babsharhisab.data.local.entity.CategoryEntity).
              | Expected:
              |""".trimMargin() + _infoCategories + """
              |
              | Found:
              |""".trimMargin() + _existingCategories)
        }
        val _columnsVendors: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(10)
        _columnsVendors.put("vendor_id", TableInfo.Column("vendor_id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("name_bn", TableInfo.Column("name_bn", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("market_location_bn", TableInfo.Column("market_location_bn", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("phone_number", TableInfo.Column("phone_number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("total_purchased_bdt", TableInfo.Column("total_purchased_bdt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("current_baki_bdt", TableInfo.Column("current_baki_bdt", "INTEGER",
            true, 0, "0", TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVendors.put("sync_status", TableInfo.Column("sync_status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVendors: HashSet<TableInfo.ForeignKey> = HashSet<TableInfo.ForeignKey>(0)
        val _indicesVendors: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesVendors.add(TableInfo.Index("index_vendors_sync_status", false,
            listOf("sync_status"), listOf("ASC")))
        _indicesVendors.add(TableInfo.Index("index_vendors_total_purchased", false,
            listOf("is_deleted", "total_purchased_bdt"), listOf("ASC", "ASC")))
        val _infoVendors: TableInfo = TableInfo("vendors", _columnsVendors, _foreignKeysVendors,
            _indicesVendors)
        val _existingVendors: TableInfo = read(db, "vendors")
        if (!_infoVendors.equals(_existingVendors)) {
          return RoomOpenHelper.ValidationResult(false, """
              |vendors(com.akram.babsharhisab.data.local.entity.VendorEntity).
              | Expected:
              |""".trimMargin() + _infoVendors + """
              |
              | Found:
              |""".trimMargin() + _existingVendors)
        }
        val _columnsShopkeepers: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(12)
        _columnsShopkeepers.put("shopkeeper_id", TableInfo.Column("shopkeeper_id", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("name_bn", TableInfo.Column("name_bn", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("shop_name_bn", TableInfo.Column("shop_name_bn", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("market_area_bn", TableInfo.Column("market_area_bn", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("phone_number", TableInfo.Column("phone_number", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("opening_baki_bdt", TableInfo.Column("opening_baki_bdt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("current_baki_bdt", TableInfo.Column("current_baki_bdt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("last_transaction_at", TableInfo.Column("last_transaction_at",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShopkeepers.put("sync_status", TableInfo.Column("sync_status", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysShopkeepers: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesShopkeepers: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(2)
        _indicesShopkeepers.add(TableInfo.Index("index_shopkeepers_baki_balance", false,
            listOf("is_deleted", "current_baki_bdt"), listOf("ASC", "ASC")))
        _indicesShopkeepers.add(TableInfo.Index("index_shopkeepers_sync_status", false,
            listOf("sync_status"), listOf("ASC")))
        val _infoShopkeepers: TableInfo = TableInfo("shopkeepers", _columnsShopkeepers,
            _foreignKeysShopkeepers, _indicesShopkeepers)
        val _existingShopkeepers: TableInfo = read(db, "shopkeepers")
        if (!_infoShopkeepers.equals(_existingShopkeepers)) {
          return RoomOpenHelper.ValidationResult(false, """
              |shopkeepers(com.akram.babsharhisab.data.local.entity.ShopkeeperEntity).
              | Expected:
              |""".trimMargin() + _infoShopkeepers + """
              |
              | Found:
              |""".trimMargin() + _existingShopkeepers)
        }
        val _columnsAssetsMaintenance: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(14)
        _columnsAssetsMaintenance.put("asset_id", TableInfo.Column("asset_id", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("asset_name_bn", TableInfo.Column("asset_name_bn", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("category_id", TableInfo.Column("category_id", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("purchase_cost_bdt", TableInfo.Column("purchase_cost_bdt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("lifespan_months", TableInfo.Column("lifespan_months",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("purchase_date_millis",
            TableInfo.Column("purchase_date_millis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("next_due_date_millis",
            TableInfo.Column("next_due_date_millis", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("daily_depreciation_bdt",
            TableInfo.Column("daily_depreciation_bdt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("monthly_depreciation_bdt",
            TableInfo.Column("monthly_depreciation_bdt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("notes_bn", TableInfo.Column("notes_bn", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("is_active", TableInfo.Column("is_active", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetsMaintenance.put("sync_status", TableInfo.Column("sync_status", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAssetsMaintenance: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(1)
        _foreignKeysAssetsMaintenance.add(TableInfo.ForeignKey("categories", "RESTRICT",
            "NO ACTION", listOf("category_id"), listOf("category_id")))
        val _indicesAssetsMaintenance: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(3)
        _indicesAssetsMaintenance.add(TableInfo.Index("index_assets_category_id", false,
            listOf("category_id"), listOf("ASC")))
        _indicesAssetsMaintenance.add(TableInfo.Index("index_assets_active_due", false,
            listOf("is_deleted", "is_active", "next_due_date_millis"), listOf("ASC", "ASC", "ASC")))
        _indicesAssetsMaintenance.add(TableInfo.Index("index_assets_sync_status", false,
            listOf("sync_status"), listOf("ASC")))
        val _infoAssetsMaintenance: TableInfo = TableInfo("assets_maintenance",
            _columnsAssetsMaintenance, _foreignKeysAssetsMaintenance, _indicesAssetsMaintenance)
        val _existingAssetsMaintenance: TableInfo = read(db, "assets_maintenance")
        if (!_infoAssetsMaintenance.equals(_existingAssetsMaintenance)) {
          return RoomOpenHelper.ValidationResult(false, """
              |assets_maintenance(com.akram.babsharhisab.data.local.entity.AssetMaintenanceEntity).
              | Expected:
              |""".trimMargin() + _infoAssetsMaintenance + """
              |
              | Found:
              |""".trimMargin() + _existingAssetsMaintenance)
        }
        val _columnsTransactions: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(19)
        _columnsTransactions.put("transaction_id", TableInfo.Column("transaction_id", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("tx_type", TableInfo.Column("tx_type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("category_id", TableInfo.Column("category_id", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("vendor_id", TableInfo.Column("vendor_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("shopkeeper_id", TableInfo.Column("shopkeeper_id", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("asset_id", TableInfo.Column("asset_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("total_bill_bdt", TableInfo.Column("total_bill_bdt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("cash_amount_bdt", TableInfo.Column("cash_amount_bdt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("baki_delta_bdt", TableInfo.Column("baki_delta_bdt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("running_shopkeeper_baki_bdt",
            TableInfo.Column("running_shopkeeper_baki_bdt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("event_date_millis", TableInfo.Column("event_date_millis",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("date_key", TableInfo.Column("date_key", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("year_month", TableInfo.Column("year_month", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("note_bn", TableInfo.Column("note_bn", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("created_at", TableInfo.Column("created_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("updated_at", TableInfo.Column("updated_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("version", TableInfo.Column("version", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("is_deleted", TableInfo.Column("is_deleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("sync_status", TableInfo.Column("sync_status", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactions: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(4)
        _foreignKeysTransactions.add(TableInfo.ForeignKey("categories", "RESTRICT", "NO ACTION",
            listOf("category_id"), listOf("category_id")))
        _foreignKeysTransactions.add(TableInfo.ForeignKey("vendors", "SET NULL", "NO ACTION",
            listOf("vendor_id"), listOf("vendor_id")))
        _foreignKeysTransactions.add(TableInfo.ForeignKey("shopkeepers", "SET NULL", "NO ACTION",
            listOf("shopkeeper_id"), listOf("shopkeeper_id")))
        _foreignKeysTransactions.add(TableInfo.ForeignKey("assets_maintenance", "SET NULL",
            "NO ACTION", listOf("asset_id"), listOf("asset_id")))
        val _indicesTransactions: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(9)
        _indicesTransactions.add(TableInfo.Index("index_tx_category_id", false,
            listOf("category_id"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_vendor_id", false, listOf("vendor_id"),
            listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_shopkeeper_id", false,
            listOf("shopkeeper_id"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_asset_id", false, listOf("asset_id"),
            listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_date_key", false, listOf("is_deleted",
            "date_key", "tx_type"), listOf("ASC", "ASC", "ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_year_month_cat", false,
            listOf("is_deleted", "year_month", "category_id"), listOf("ASC", "ASC", "ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_shopkeeper_ledger", false,
            listOf("is_deleted", "shopkeeper_id", "event_date_millis"), listOf("ASC", "ASC",
            "ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_vendor_ledger", false,
            listOf("is_deleted", "vendor_id", "event_date_millis"), listOf("ASC", "ASC", "ASC")))
        _indicesTransactions.add(TableInfo.Index("index_tx_sync_queue", false, listOf("sync_status",
            "updated_at"), listOf("ASC", "ASC")))
        val _infoTransactions: TableInfo = TableInfo("transactions", _columnsTransactions,
            _foreignKeysTransactions, _indicesTransactions)
        val _existingTransactions: TableInfo = read(db, "transactions")
        if (!_infoTransactions.equals(_existingTransactions)) {
          return RoomOpenHelper.ValidationResult(false, """
              |transactions(com.akram.babsharhisab.data.local.entity.TransactionEntity).
              | Expected:
              |""".trimMargin() + _infoTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingTransactions)
        }
        val _columnsSyncAuditLogs: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(5)
        _columnsSyncAuditLogs.put("log_id", TableInfo.Column("log_id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncAuditLogs.put("executed_at", TableInfo.Column("executed_at", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncAuditLogs.put("synced_records_count", TableInfo.Column("synced_records_count",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncAuditLogs.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncAuditLogs.put("message_bn", TableInfo.Column("message_bn", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSyncAuditLogs: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesSyncAuditLogs: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoSyncAuditLogs: TableInfo = TableInfo("sync_audit_logs", _columnsSyncAuditLogs,
            _foreignKeysSyncAuditLogs, _indicesSyncAuditLogs)
        val _existingSyncAuditLogs: TableInfo = read(db, "sync_audit_logs")
        if (!_infoSyncAuditLogs.equals(_existingSyncAuditLogs)) {
          return RoomOpenHelper.ValidationResult(false, """
              |sync_audit_logs(com.akram.babsharhisab.data.local.entity.SyncLogEntity).
              | Expected:
              |""".trimMargin() + _infoSyncAuditLogs + """
              |
              | Found:
              |""".trimMargin() + _existingSyncAuditLogs)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "7df86a7116a67dc9767238e817c8551d", "ca64d5261f38dc62fc26011e1d71ec53")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "categories","vendors","shopkeepers","assets_maintenance","transactions","sync_audit_logs")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    val _supportsDeferForeignKeys: Boolean = android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.LOLLIPOP
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE")
      }
      super.beginTransaction()
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE")
      }
      _db.execSQL("DELETE FROM `categories`")
      _db.execSQL("DELETE FROM `vendors`")
      _db.execSQL("DELETE FROM `shopkeepers`")
      _db.execSQL("DELETE FROM `assets_maintenance`")
      _db.execSQL("DELETE FROM `transactions`")
      _db.execSQL("DELETE FROM `sync_audit_logs`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE")
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(CategoryDao::class.java, CategoryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VendorDao::class.java, VendorDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ShopkeeperDao::class.java, ShopkeeperDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AssetDao::class.java, AssetDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransactionDao::class.java, TransactionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SyncAuditDao::class.java, SyncAuditDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun categoryDao(): CategoryDao = _categoryDao.value

  public override fun vendorDao(): VendorDao = _vendorDao.value

  public override fun shopkeeperDao(): ShopkeeperDao = _shopkeeperDao.value

  public override fun assetDao(): AssetDao = _assetDao.value

  public override fun transactionDao(): TransactionDao = _transactionDao.value

  public override fun syncAuditDao(): SyncAuditDao = _syncAuditDao.value
}
