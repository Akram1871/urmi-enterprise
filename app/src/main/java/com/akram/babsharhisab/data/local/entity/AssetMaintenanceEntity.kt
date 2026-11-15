package com.akram.babsharhisab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets_maintenance",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["category_id"], name = "index_assets_category_id"),
        Index(value = ["is_deleted", "is_active", "next_due_date_millis"], name = "index_assets_active_due"),
        Index(value = ["sync_status"], name = "index_assets_sync_status")
    ]
)
data class AssetMaintenanceEntity(
    @PrimaryKey
    @ColumnInfo(name = "asset_id")
    val assetId: String,

    @ColumnInfo(name = "asset_name_bn")
    val assetNameBn: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String = CategoryEntity.CAT_ASSET,

    @ColumnInfo(name = "purchase_cost_bdt")
    val purchaseCostBdt: Long,

    @ColumnInfo(name = "lifespan_months")
    val lifespanMonths: Int,

    @ColumnInfo(name = "purchase_date_millis")
    val purchaseDateMillis: Long,

    @ColumnInfo(name = "next_due_date_millis")
    val nextDueDateMillis: Long,

    @ColumnInfo(name = "daily_depreciation_bdt")
    val dailyDepreciationBdt: Long,

    @ColumnInfo(name = "monthly_depreciation_bdt")
    val monthlyDepreciationBdt: Long,

    @ColumnInfo(name = "notes_bn")
    val notesBn: String = "",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = SyncStatus.PENDING
)
