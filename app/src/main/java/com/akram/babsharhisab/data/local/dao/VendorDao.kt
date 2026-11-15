package com.akram.babsharhisab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akram.babsharhisab.data.local.entity.VendorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorDao {

    @Query("SELECT * FROM vendors WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, total_purchased_bdt DESC, name_bn ASC")
    fun observeActiveVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE is_deleted = 0 ORDER BY current_baki_bdt DESC, total_purchased_bdt DESC, name_bn ASC")
    suspend fun getActiveVendors(): List<VendorEntity>

    @Query("SELECT * FROM vendors WHERE vendor_id = :vendorId LIMIT 1")
    suspend fun getVendorById(vendorId: String): VendorEntity?

    @Query("SELECT COALESCE(SUM(current_baki_bdt), 0) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0")
    fun observeTotalVendorBaki(): Flow<Long>

    @Query("SELECT COALESCE(SUM(current_baki_bdt), 0) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0")
    suspend fun getTotalVendorBaki(): Long

    @Query("SELECT COUNT(*) FROM vendors WHERE is_deleted = 0 AND current_baki_bdt > 0")
    fun observeActiveBakiVendorCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVendor(vendor: VendorEntity)

    @Update
    suspend fun updateVendor(vendor: VendorEntity)

    @Query("""
        UPDATE vendors
        SET total_purchased_bdt = MAX(0, total_purchased_bdt + :deltaBdt),
            current_baki_bdt = MAX(0, current_baki_bdt + :bakiDeltaBdt),
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE vendor_id = :vendorId
    """)
    suspend fun incrementVendorPurchaseAndBaki(
        vendorId: String,
        deltaBdt: Long,
        bakiDeltaBdt: Long,
        updatedAt: Long
    )

    @Query("""
        UPDATE vendors
        SET current_baki_bdt = MAX(0, :newBakiBdt),
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE vendor_id = :vendorId
    """)
    suspend fun updateVendorBakiBalance(vendorId: String, newBakiBdt: Long, updatedAt: Long)

    @Query("SELECT * FROM vendors WHERE sync_status != 'SYNCED'")
    suspend fun getUnsyncedVendors(): List<VendorEntity>

    @Query("""
        UPDATE vendors
        SET is_deleted = 1,
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE vendor_id = :vendorId
    """)
    suspend fun softDeleteVendor(vendorId: String, updatedAt: Long)

    @Query("UPDATE vendors SET sync_status = 'SYNCED' WHERE vendor_id IN (:vendorIds)")
    suspend fun markVendorsSynced(vendorIds: List<String>)
}
