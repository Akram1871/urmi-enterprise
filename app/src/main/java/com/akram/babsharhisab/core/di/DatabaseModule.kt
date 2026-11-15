package com.akram.babsharhisab.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.akram.babsharhisab.data.local.AppDatabase
import com.akram.babsharhisab.data.local.dao.AssetDao
import com.akram.babsharhisab.data.local.dao.CategoryDao
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao
import com.akram.babsharhisab.data.local.dao.SyncAuditDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.dao.VendorDao
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE vendors ADD COLUMN current_baki_bdt INTEGER NOT NULL DEFAULT 0")
            seedDefaultCategoriesSql(db)
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Reconcile any existing Mahajan rows where total_purchased_bdt was recorded
            // while current_baki_bdt remained 0 due to legacy schema/sync
            db.execSQL(
                """
                UPDATE vendors
                SET current_baki_bdt = total_purchased_bdt,
                    sync_status = 'PENDING'
                WHERE is_deleted = 0
                  AND current_baki_bdt = 0
                  AND total_purchased_bdt > 0
                  AND NOT EXISTS (
                      SELECT 1 FROM transactions t
                      WHERE t.vendor_id = vendors.vendor_id
                        AND t.tx_type = 'VENDOR_BAKI_PAYMENT'
                        AND t.is_deleted = 0
                  )
                """.trimIndent()
            )
            seedDefaultCategoriesSql(db)
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys = ON;")
                    seedDefaultCategoriesSql(db)
                    reconcileLegacyVendorBakiSql(db)
                }
            })
            .build()
    }

    private fun reconcileLegacyVendorBakiSql(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            UPDATE vendors
            SET current_baki_bdt = total_purchased_bdt,
                sync_status = 'PENDING'
            WHERE is_deleted = 0
              AND current_baki_bdt = 0
              AND total_purchased_bdt > 0
              AND NOT EXISTS (
                  SELECT 1 FROM transactions t
                  WHERE t.vendor_id = vendors.vendor_id
                    AND t.tx_type IN ('VENDOR_BAKI_PAYMENT', 'WHOLESALE_PURCHASE')
                    AND t.cash_amount_bdt > 0
                    AND t.is_deleted = 0
              )
            """.trimIndent()
        )
    }

    private fun seedDefaultCategoriesSql(db: SupportSQLiteDatabase) {
        val defaults = CategoryEntity.defaultSystemCategories()
        db.beginTransaction()
        try {
            for (cat in defaults) {
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO categories (category_id, name_bn, type, icon_name, color_hex, sort_order, is_system)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf<Any>(
                        cat.categoryId,
                        cat.nameBn,
                        cat.type,
                        cat.iconName,
                        cat.colorHex,
                        cat.sortOrder,
                        if (cat.isSystem) 1 else 0
                    )
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideVendorDao(db: AppDatabase): VendorDao = db.vendorDao()

    @Provides
    fun provideShopkeeperDao(db: AppDatabase): ShopkeeperDao = db.shopkeeperDao()

    @Provides
    fun provideAssetDao(db: AppDatabase): AssetDao = db.assetDao()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideSyncAuditDao(db: AppDatabase): SyncAuditDao = db.syncAuditDao()
}
