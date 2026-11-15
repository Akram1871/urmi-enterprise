package com.akram.babsharhisab.domain.model

data class DailySummary(
    val dateKey: String,
    val totalSalesBdt: Long = 0L,
    val cashSalesBdt: Long = 0L,
    val newBakiGivenBdt: Long = 0L,
    val bakiCollectedBdt: Long = 0L,
    val wholesalePurchaseBdt: Long = 0L,
    val cashPurchaseBdt: Long = wholesalePurchaseBdt,
    val newVendorBakiBdt: Long = 0L,
    val vendorBakiPaidBdt: Long = 0L,
    val operatingExpenseBdt: Long = 0L,
    val assetMaintenanceBdt: Long = 0L,
    val dailyAssetDepreciationBdt: Long = 0L,
    val totalActiveMarketBakiBdt: Long = 0L,
    val activeBakiShopkeeperCount: Int = 0,
    val totalActiveVendorBakiBdt: Long = 0L,
    val activeBakiVendorCount: Int = 0,
    val personalHomeExpenseBdt: Long = 0L,
    val monthlyHomeExpenseBdt: Long = 0L
) {
    /**
     * Total realized cash inflow today (Cash from today's sales + previous Baki collected today).
     */
    val todayCashInflowBdt: Long
        get() = cashSalesBdt + bakiCollectedBdt

    /**
     * Total cash outflow today (Cash paid on today's wholesale purchases + previous Mahajan Baki paid today
     * + operational expenses + capital asset expenses).
     */
    val todayCashOutflowBdt: Long
        get() = cashPurchaseBdt + vendorBakiPaidBdt + operatingExpenseBdt + assetMaintenanceBdt

    /**
     * Net cash in hand generated today.
     */
    val todayNetCashFlowBdt: Long
        get() = todayCashInflowBdt - todayCashOutflowBdt

    /**
     * Operational Net Profit/Loss today:
     * Total Sales Revenue (Cash + New Baki) - Wholesale Purchases - Operating Expenses.
     */
    val todayOperationalProfitBdt: Long
        get() = totalSalesBdt - (wholesalePurchaseBdt + operatingExpenseBdt)

    /**
     * Adjusted Net Profit after deducting daily periodic asset depreciation reserve (e.g., 2-year battery reserve).
     */
    val todayAdjustedProfitBdt: Long
        get() = todayOperationalProfitBdt - dailyAssetDepreciationBdt
}

data class CategorySpendItem(
    val categoryId: String,
    val nameBn: String,
    val colorHex: String,
    val iconName: String,
    val totalAmountBdt: Long,
    val txCount: Int,
    val shareFraction: Float
)

data class TransactionItem(
    val transactionId: String,
    val txType: String,
    val categoryId: String,
    val categoryNameBn: String,
    val categoryColorHex: String,
    val vendorId: String? = null,
    val vendorNameBn: String? = null,
    val shopkeeperId: String? = null,
    val shopkeeperNameBn: String? = null,
    val assetId: String? = null,
    val totalBillBdt: Long,
    val cashAmountBdt: Long,
    val bakiDeltaBdt: Long,
    val runningShopkeeperBakiBdt: Long = 0L,
    val eventDateMillis: Long,
    val dateKey: String,
    val noteBn: String,
    val syncStatus: String
)

data class ShopkeeperAccount(
    val shopkeeperId: String,
    val nameBn: String,
    val shopNameBn: String,
    val marketAreaBn: String,
    val phoneNumber: String,
    val openingBakiBdt: Long,
    val currentBakiBdt: Long,
    val lastTransactionAt: Long,
    val syncStatus: String
)

data class VendorProfile(
    val vendorId: String,
    val nameBn: String,
    val marketLocationBn: String,
    val phoneNumber: String,
    val totalPurchasedBdt: Long,
    val currentBakiBdt: Long = 0L
)

data class AssetItem(
    val assetId: String,
    val assetNameBn: String,
    val purchaseCostBdt: Long,
    val lifespanMonths: Int,
    val purchaseDateMillis: Long,
    val nextDueDateMillis: Long,
    val dailyDepreciationBdt: Long,
    val monthlyDepreciationBdt: Long,
    val notesBn: String,
    val isActive: Boolean,
    val elapsedFraction: Float,
    val remainingDays: Long
)

data class MonthlyBarPoint(
    val yearMonth: String,
    val labelBn: String,
    val totalSalesBdt: Long,
    val totalExpenseBdt: Long,
    val wholesalePurchaseBdt: Long,
    val operatingExpenseBdt: Long,
    val netProfitBdt: Long,
    val personalHomeExpenseBdt: Long = 0L
)

data class CategoryTrendPoint(
    val yearMonth: String,
    val labelBn: String,
    val amountBdt: Long
)
