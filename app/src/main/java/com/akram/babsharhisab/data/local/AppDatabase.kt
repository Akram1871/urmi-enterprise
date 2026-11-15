package com.akram.babsharhisab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akram.babsharhisab.data.local.dao.AssetDao
import com.akram.babsharhisab.data.local.dao.CategoryDao
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao
import com.akram.babsharhisab.data.local.dao.SyncAuditDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.dao.VendorDao
import com.akram.babsharhisab.data.local.entity.AssetMaintenanceEntity
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.ShopkeeperEntity
import com.akram.babsharhisab.data.local.entity.SyncLogEntity
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import com.akram.babsharhisab.data.local.entity.VendorEntity

@Database(
    entities = [
        CategoryEntity::class,
        VendorEntity::class,
        ShopkeeperEntity::class,
        AssetMaintenanceEntity::class,
        TransactionEntity::class,
        SyncLogEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun vendorDao(): VendorDao
    abstract fun shopkeeperDao(): ShopkeeperDao
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
    abstract fun syncAuditDao(): SyncAuditDao

    companion object {
        const val DATABASE_NAME = "babshar_hisab.db"
    }
}
