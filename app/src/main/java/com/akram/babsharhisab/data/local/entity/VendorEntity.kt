package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vendors",
    indices = [
        Index(value = ["sync_status"], name = "index_vendors_sync_status"),
        Index(value = ["is_deleted", "total_purchased_bdt"], name = "index_vendors_total_purchased")
    ]
)
data class VendorEntity(
    @PrimaryKey
    @ColumnInfo(name = "vendor_id")
    val vendorId: String,

    @ColumnInfo(name = "name_bn")
    val nameBn: String,

    @ColumnInfo(name = "market_location_bn")
    val marketLocationBn: String = "",

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String = "",

    @ColumnInfo(name = "total_purchased_bdt")
    val totalPurchasedBdt: Long = 0L,

    @ColumnInfo(name = "current_baki_bdt", defaultValue = "0")
    val currentBakiBdt: Long = 0L,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = SyncStatus.PENDING
)

object SyncStatus {
    const val PENDING = "PENDING"
    const val SYNCED = "SYNCED"
    const val FAILED = "FAILED"
}
