package com.akram.babsharhisab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akram.babsharhisab.data.local.entity.ShopkeeperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopkeeperDao {

    @Query("SELECT * FROM shopkeepers WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, name_bn ASC")
    fun observeActiveShopkeepers(): Flow<List<ShopkeeperEntity>>

    @Query("SELECT * FROM shopkeepers WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, name_bn ASC")
    suspend fun getActiveShopkeepers(): List<ShopkeeperEntity>

    @Query("SELECT * FROM shopkeepers WHERE shopkeeper_id = :shopkeeperId LIMIT 1")
    fun observeShopkeeperById(shopkeeperId: String): Flow<ShopkeeperEntity?>

    @Query("SELECT * FROM shopkeepers WHERE shopkeeper_id = :shopkeeperId LIMIT 1")
    suspend fun getShopkeeperById(shopkeeperId: String): ShopkeeperEntity?

    @Query("SELECT COALESCE(SUM(current_baki_bdt), 0) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0")
    fun observeTotalMarketBaki(): Flow<Long>

    @Query("SELECT COALESCE(SUM(current_baki_bdt), 0) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0")
    suspend fun getTotalMarketBaki(): Long

    @Query("SELECT COUNT(*) FROM shopkeepers WHERE is_deleted = 0 AND current_baki_bdt > 0")
    fun observeActiveBakiShopkeeperCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShopkeeper(shopkeeper: ShopkeeperEntity)

    @Update
    suspend fun updateShopkeeper(shopkeeper: ShopkeeperEntity)

    @Query("""
        UPDATE shopkeepers
        SET current_baki_bdt = :newBakiBdt,
            last_transaction_at = :eventMillis,
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE shopkeeper_id = :shopkeeperId
    """)
    suspend fun updateShopkeeperBakiBalance(
        shopkeeperId: String,
        newBakiBdt: Long,
        eventMillis: Long,
        updatedAt: Long
    )

    @Query("SELECT * FROM shopkeepers WHERE sync_status != 'SYNCED'")
    suspend fun getUnsyncedShopkeepers(): List<ShopkeeperEntity>

    @Query("""
        UPDATE shopkeepers
        SET is_deleted = 1,
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE shopkeeper_id = :shopkeeperId
    """)
    suspend fun softDeleteShopkeeper(shopkeeperId: String, updatedAt: Long)

    @Query("UPDATE shopkeepers SET sync_status = 'SYNCED' WHERE shopkeeper_id IN (:shopkeeperIds)")
    suspend fun markShopkeepersSynced(shopkeeperIds: List<String>)
}
