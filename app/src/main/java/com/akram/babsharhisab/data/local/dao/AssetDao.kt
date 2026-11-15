package com.akram.babsharhisab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akram.babsharhisab.data.local.entity.AssetMaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Query("SELECT * FROM assets_maintenance WHERE is_deleted = 0 ORDER BY is_active DESC, next_due_date_millis ASC")
    fun observeAllAssets(): Flow<List<AssetMaintenanceEntity>>

    @Query("SELECT * FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1 ORDER BY next_due_date_millis ASC")
    fun observeActiveAssets(): Flow<List<AssetMaintenanceEntity>>

    @Query("SELECT COALESCE(SUM(daily_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1")
    fun observeTotalDailyDepreciation(): Flow<Long>

    @Query("SELECT COALESCE(SUM(daily_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1")
    suspend fun getTotalDailyDepreciation(): Long

    @Query("SELECT COALESCE(SUM(monthly_depreciation_bdt), 0) FROM assets_maintenance WHERE is_deleted = 0 AND is_active = 1")
    fun observeTotalMonthlyDepreciation(): Flow<Long>

    @Query("SELECT * FROM assets_maintenance WHERE asset_id = :assetId LIMIT 1")
    suspend fun getAssetById(assetId: String): AssetMaintenanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAsset(asset: AssetMaintenanceEntity)

    @Update
    suspend fun updateAsset(asset: AssetMaintenanceEntity)

    @Query("SELECT * FROM assets_maintenance WHERE sync_status != 'SYNCED'")
    suspend fun getUnsyncedAssets(): List<AssetMaintenanceEntity>

    @Query("UPDATE assets_maintenance SET sync_status = 'SYNCED' WHERE asset_id IN (:assetIds)")
    suspend fun markAssetsSynced(assetIds: List<String>)
}
