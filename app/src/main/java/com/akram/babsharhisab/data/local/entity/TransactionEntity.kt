package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = VendorEntity::class,
            parentColumns = ["vendor_id"],
            childColumns = ["vendor_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ShopkeeperEntity::class,
            parentColumns = ["shopkeeper_id"],
            childColumns = ["shopkeeper_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AssetMaintenanceEntity::class,
            parentColumns = ["asset_id"],
            childColumns = ["asset_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["category_id"], name = "index_tx_category_id"),
        Index(value = ["vendor_id"], name = "index_tx_vendor_id"),
        Index(value = ["shopkeeper_id"], name = "index_tx_shopkeeper_id"),
        Index(value = ["asset_id"], name = "index_tx_asset_id"),
        Index(value = ["is_deleted", "date_key", "tx_type"], name = "index_tx_date_key"),
        Index(value = ["is_deleted", "year_month", "category_id"], name = "index_tx_year_month_cat"),
        Index(value = ["is_deleted", "shopkeeper_id", "event_date_millis"], name = "index_tx_shopkeeper_ledger"),
        Index(value = ["is_deleted", "vendor_id", "event_date_millis"], name = "index_tx_vendor_ledger"),
        Index(value = ["sync_status", "updated_at"], name = "index_tx_sync_queue")
    ]
)
data class TransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "transaction_id")
    val transactionId: String,

    @ColumnInfo(name = "tx_type")
    val txType: String, // SALE, BAKI_COLLECTION, WHOLESALE_PURCHASE, OPERATING_EXPENSE, ASSET_MAINTENANCE

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "vendor_id")
    val vendorId: String? = null,

    @ColumnInfo(name = "shopkeeper_id")
    val shopkeeperId: String? = null,

    @ColumnInfo(name = "asset_id")
    val assetId: String? = null,

    @ColumnInfo(name = "total_bill_bdt")
    val totalBillBdt: Long,

    @ColumnInfo(name = "cash_amount_bdt")
    val cashAmountBdt: Long,

    @ColumnInfo(name = "baki_delta_bdt")
    val bakiDeltaBdt: Long = 0L,

    @ColumnInfo(name = "running_shopkeeper_baki_bdt")
    val runningShopkeeperBakiBdt: Long = 0L,

    @ColumnInfo(name = "event_date_millis")
    val eventDateMillis: Long,

    @ColumnInfo(name = "date_key")
    val dateKey: String, // YYYY-MM-DD

    @ColumnInfo(name = "year_month")
    val yearMonth: String, // YYYY-MM

    @ColumnInfo(name = "note_bn")
    val noteBn: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "version")
    val version: Int = 1,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = SyncStatus.PENDING
)

object TxType {
    const val SALE = "SALE"
    const val BAKI_COLLECTION = "BAKI_COLLECTION"
    const val WHOLESALE_PURCHASE = "WHOLESALE_PURCHASE"
    const val VENDOR_BAKI_PAYMENT = "VENDOR_BAKI_PAYMENT"
    const val OPERATING_EXPENSE = "OPERATING_EXPENSE"
    const val ASSET_MAINTENANCE = "ASSET_MAINTENANCE"
    const val PERSONAL_HOME_EXPENSE = "PERSONAL_HOME_EXPENSE"
}
