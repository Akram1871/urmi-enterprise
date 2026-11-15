package com.akram.babsharhisab.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SheetsBackupPayloadDto(
    val action: String = "PUSH",
    val secretToken: String,
    val deviceTimestampMillis: Long,
    val dateKey: String,
    val dailySummary: DailySummarySheetRowDto,
    val transactions: List<TransactionSheetRowDto>,
    val shopkeepers: List<ShopkeeperSheetRowDto>,
    val assets: List<AssetSheetRowDto>,
    val vendors: List<VendorSheetRowDto>
)

@Serializable
data class SheetsPullRequestDto(
    val action: String = "PULL_ALL",
    val secretToken: String = ""
)

@Serializable
data class DailySummarySheetRowDto(
    val dateKey: String,
    val totalSalesBdt: Long,
    val cashSalesBdt: Long,
    val newBakiGivenBdt: Long,
    val bakiCollectedBdt: Long,
    val wholesalePurchaseBdt: Long,
    val operatingExpenseBdt: Long,
    val assetMaintenanceBdt: Long,
    val dailyDepreciationBdt: Long,
    val netOperationalProfitBdt: Long,
    val adjustedNetProfitBdt: Long,
    val netCashFlowBdt: Long,
    val totalActiveMarketBakiBdt: Long,
    val totalActiveVendorBakiBdt: Long = 0L,
    val personalHomeExpenseBdt: Long = 0L
)

@Serializable
data class TransactionSheetRowDto(
    val transactionId: String,
    val dateKey: String,
    val eventDateMillis: Long = 0L,
    val txType: String,
    val categoryId: String,
    val vendorId: String = "",
    val vendorNameBn: String = "",
    val shopkeeperId: String = "",
    val shopkeeperNameBn: String = "",
    val assetId: String = "",
    val totalBillBdt: Long = 0L,
    val cashAmountBdt: Long = 0L,
    val bakiDeltaBdt: Long = 0L,
    val runningShopkeeperBakiBdt: Long = 0L,
    val noteBn: String = "",
    val version: Int = 1,
    val isDeleted: Boolean = false
)

@Serializable
data class ShopkeeperSheetRowDto(
    val shopkeeperId: String,
    val nameBn: String,
    val shopNameBn: String = "",
    val marketAreaBn: String = "",
    val phoneNumber: String = "",
    val currentBakiBdt: Long = 0L,
    val lastTransactionAt: Long = 0L,
    val isDeleted: Boolean = false
)

@Serializable
data class AssetSheetRowDto(
    val assetId: String,
    val assetNameBn: String,
    val purchaseCostBdt: Long = 0L,
    val lifespanMonths: Int = 24,
    val purchaseDateKey: String = "",
    val nextDueDateKey: String = "",
    val dailyDepreciationBdt: Long = 0L,
    val monthlyDepreciationBdt: Long = 0L,
    val notesBn: String = "",
    val isActive: Boolean = true,
    val isDeleted: Boolean = false
)

@Serializable
data class VendorSheetRowDto(
    val vendorId: String,
    val nameBn: String,
    val marketLocationBn: String = "",
    val phoneNumber: String = "",
    val totalPurchasedBdt: Long = 0L,
    val currentBakiBdt: Long = 0L,
    val isDeleted: Boolean = false
)

@Serializable
data class SheetsBackupResponseDto(
    val status: String = "",
    val message: String = "",
    val syncedCount: Int = 0,
    val transactions: List<TransactionSheetRowDto> = emptyList(),
    val shopkeepers: List<ShopkeeperSheetRowDto> = emptyList(),
    val vendors: List<VendorSheetRowDto> = emptyList(),
    val assets: List<AssetSheetRowDto> = emptyList()
)
