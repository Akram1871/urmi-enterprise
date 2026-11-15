package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopkeepers",
    indices = [
        Index(value = ["is_deleted", "current_baki_bdt"], name = "index_shopkeepers_baki_balance"),
        Index(value = ["sync_status"], name = "index_shopkeepers_sync_status")
    ]
)
data class ShopkeeperEntity(
    @PrimaryKey
    @ColumnInfo(name = "shopkeeper_id")
    val shopkeeperId: String,

    @ColumnInfo(name = "name_bn")
    val nameBn: String,

    @ColumnInfo(name = "shop_name_bn")
    val shopNameBn: String = "",

    @ColumnInfo(name = "market_area_bn")
    val marketAreaBn: String = "",

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String = "",

    @ColumnInfo(name = "opening_baki_bdt")
    val openingBakiBdt: Long = 0L,

    @ColumnInfo(name = "current_baki_bdt")
    val currentBakiBdt: Long = 0L,

    @ColumnInfo(name = "last_transaction_at")
    val lastTransactionAt: Long = 0L,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = SyncStatus.PENDING
)
