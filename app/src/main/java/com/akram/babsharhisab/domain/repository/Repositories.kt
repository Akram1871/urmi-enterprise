package com.akram.babsharhisab.domain.repository

import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.SyncLogEntity
import com.akram.babsharhisab.domain.model.AssetItem
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.CategoryTrendPoint
import com.akram.babsharhisab.domain.model.DailySummary
import com.akram.babsharhisab.domain.model.MonthlyBarPoint
import com.akram.babsharhisab.domain.model.ShopkeeperAccount
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.model.VendorProfile
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    fun observeCategories(): Flow<List<CategoryEntity>>
    fun observeDailySummary(dateKey: String): Flow<DailySummary>
    fun observeDailyExpenseBreakdown(dateKey: String): Flow<List<CategorySpendItem>>
    fun observeTransactionsByDate(dateKey: String): Flow<List<TransactionItem>>
    fun observeRecentTransactions(limit: Int = 100): Flow<List<TransactionItem>>

    fun observeMonthlyIncomeExpense(yearMonths: List<String>): Flow<List<MonthlyBarPoint>>
    fun observePeriodCategoryBreakdown(yearMonths: List<String>): Flow<List<CategorySpendItem>>
    fun observeCategoryTrend(yearMonths: List<String>, categoryId: String): Flow<List<CategoryTrendPoint>>

    fun observeVendors(): Flow<List<VendorProfile>>
    fun observeTotalVendorBaki(): Flow<Long>
    fun observeActiveBakiVendorCount(): Flow<Int>

    suspend fun createOrUpdateVendor(
        vendorId: String? = null,
        nameBn: String,
        marketLocationBn: String,
        phoneNumber: String,
        openingBakiBdt: Long = 0L,
        updatedCurrentBakiBdt: Long? = null
    ): String

    suspend fun deleteVendor(vendorId: String)

    suspend fun recordWholesalePurchase(
        vendorId: String?,
        amountBdt: Long,
        cashPaidBdt: Long = amountBdt,
        eventDateMillis: Long,
        noteBn: String
    ): String

    suspend fun payVendorBaki(
        vendorId: String,
        paidCashBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String

    suspend fun recordOperatingExpense(
        categoryId: String,
        amountBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String

    suspend fun recordSaleWithBaki(
        shopkeeperId: String?,
        totalBillBdt: Long,
        cashReceivedBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String

    suspend fun updateTransaction(
        transactionId: String,
        newTotalBillBdt: Long,
        newCashAmountBdt: Long,
        newNoteBn: String
    )

    suspend fun deleteTransaction(transactionId: String)
}

interface BakiRepository {
    fun observeShopkeepers(): Flow<List<ShopkeeperAccount>>
    fun observeShopkeeperById(shopkeeperId: String): Flow<ShopkeeperAccount?>
    fun observeShopkeeperLedger(shopkeeperId: String): Flow<List<TransactionItem>>
    fun observeTotalMarketBaki(): Flow<Long>
    fun observeActiveBakiShopkeeperCount(): Flow<Int>

    suspend fun createOrUpdateShopkeeper(
        shopkeeperId: String? = null,
        nameBn: String,
        shopNameBn: String,
        marketAreaBn: String,
        phoneNumber: String,
        openingBakiBdt: Long = 0L,
        updatedCurrentBakiBdt: Long? = null
    ): String

    suspend fun deleteShopkeeper(shopkeeperId: String)

    suspend fun collectShopkeeperBaki(
        shopkeeperId: String,
        collectedCashBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String
}

interface AssetRepository {
    fun observeActiveAssets(): Flow<List<AssetItem>>
    fun observeAllAssets(): Flow<List<AssetItem>>
    fun observeTotalDailyDepreciation(): Flow<Long>
    fun observeTotalMonthlyDepreciation(): Flow<Long>

    suspend fun saveOrReplaceAsset(
        assetId: String? = null,
        assetNameBn: String,
        purchaseCostBdt: Long,
        lifespanMonths: Int,
        purchaseDateMillis: Long,
        notesBn: String,
        recordAsTodayCashExpense: Boolean
    ): String

    suspend fun archiveAsset(assetId: String)
}

interface BackupSyncRepository {
    fun observePendingSyncCount(): Flow<Int>
    fun observeRecentSyncLogs(limit: Int = 20): Flow<List<SyncLogEntity>>
    fun observeLastSuccessfulSync(): Flow<SyncLogEntity?>
    suspend fun performGoogleSheetsSync(): Result<Int>
    suspend fun restoreFromGoogleSheets(): Result<Int>
    suspend fun generateCsvExportContent(): String
}
