package com.akram.babsharhisab.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

data class CategoryAggregationRow(
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "name_bn") val nameBn: String,
    @ColumnInfo(name = "color_hex") val colorHex: String,
    @ColumnInfo(name = "icon_name") val iconName: String,
    @ColumnInfo(name = "total_amount_bdt") val totalAmountBdt: Long,
    @ColumnInfo(name = "tx_count") val txCount: Int
)

data class MonthlyIncomeExpenseRow(
    @ColumnInfo(name = "year_month") val yearMonth: String,
    @ColumnInfo(name = "total_sales_bdt") val totalSalesBdt: Long,
    @ColumnInfo(name = "total_purchase_bdt") val totalPurchaseBdt: Long,
    @ColumnInfo(name = "total_operating_expense_bdt") val totalOperatingExpenseBdt: Long,
    @ColumnInfo(name = "total_asset_expense_bdt") val totalAssetExpenseBdt: Long,
    @ColumnInfo(name = "total_cash_in_bdt") val totalCashInBdt: Long,
    @ColumnInfo(name = "total_home_expense_bdt") val totalHomeExpenseBdt: Long = 0L
)

data class MonthlyCategoryTrendRow(
    @ColumnInfo(name = "year_month") val yearMonth: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "total_amount_bdt") val totalAmountBdt: Long
)

data class DailyAggregationSnapshot(
    @ColumnInfo(name = "date_key") val dateKey: String,
    @ColumnInfo(name = "total_sales_bdt") val totalSalesBdt: Long,
    @ColumnInfo(name = "cash_sales_bdt") val cashSalesBdt: Long,
    @ColumnInfo(name = "new_baki_given_bdt") val newBakiGivenBdt: Long,
    @ColumnInfo(name = "baki_collected_bdt") val bakiCollectedBdt: Long,
    @ColumnInfo(name = "wholesale_purchase_bdt") val wholesalePurchaseBdt: Long,
    @ColumnInfo(name = "cash_purchase_bdt") val cashPurchaseBdt: Long = 0L,
    @ColumnInfo(name = "new_vendor_baki_bdt") val newVendorBakiBdt: Long = 0L,
    @ColumnInfo(name = "vendor_baki_paid_bdt") val vendorBakiPaidBdt: Long = 0L,
    @ColumnInfo(name = "operating_expense_bdt") val operatingExpenseBdt: Long,
    @ColumnInfo(name = "asset_maintenance_bdt") val assetMaintenanceBdt: Long,
    @ColumnInfo(name = "personal_home_expense_bdt") val personalHomeExpenseBdt: Long = 0L
)

@Dao
interface TransactionDao {

    @Query("""
        SELECT * FROM transactions
        WHERE is_deleted = 0 AND date_key = :dateKey
        ORDER BY event_date_millis DESC, created_at DESC
    """)
    fun observeTransactionsByDate(dateKey: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE is_deleted = 0
        ORDER BY event_date_millis DESC, created_at DESC
        LIMIT :limit
    """)
    fun observeRecentTransactions(limit: Int = 100): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE is_deleted = 0 AND shopkeeper_id = :shopkeeperId
        ORDER BY event_date_millis DESC, created_at DESC
    """)
    fun observeTransactionsForShopkeeper(shopkeeperId: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE is_deleted = 0 AND vendor_id = :vendorId
        ORDER BY event_date_millis DESC, created_at DESC
    """)
    fun observeTransactionsForVendor(vendorId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE transaction_id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    // --- Real-Time Operational Dashboard Aggregations (05_Schema.md Section 3) ---

    @Query("""
        SELECT
            :dateKey AS date_key,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_sales_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_baki_given_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'BAKI_COLLECTION' THEN cash_amount_bdt ELSE 0 END), 0) AS baki_collected_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS wholesale_purchase_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_purchase_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_vendor_baki_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'VENDOR_BAKI_PAYMENT' THEN cash_amount_bdt ELSE 0 END), 0) AS vendor_baki_paid_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' AND category_id != 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS operating_expense_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS asset_maintenance_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' OR category_id = 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS personal_home_expense_bdt
        FROM transactions
        WHERE is_deleted = 0 AND date_key = :dateKey
    """)
    fun observeDailyAggregation(dateKey: String): Flow<DailyAggregationSnapshot>

    @Query("""
        SELECT
            :dateKey AS date_key,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_sales_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_baki_given_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'BAKI_COLLECTION' THEN cash_amount_bdt ELSE 0 END), 0) AS baki_collected_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS wholesale_purchase_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN cash_amount_bdt ELSE 0 END), 0) AS cash_purchase_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN baki_delta_bdt ELSE 0 END), 0) AS new_vendor_baki_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'VENDOR_BAKI_PAYMENT' THEN cash_amount_bdt ELSE 0 END), 0) AS vendor_baki_paid_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' AND category_id != 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS operating_expense_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS asset_maintenance_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' OR category_id = 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS personal_home_expense_bdt
        FROM transactions
        WHERE is_deleted = 0 AND date_key = :dateKey
    """)
    suspend fun getDailyAggregation(dateKey: String): DailyAggregationSnapshot

    @Query("""
        SELECT COALESCE(SUM(total_bill_bdt), 0)
        FROM transactions
        WHERE is_deleted = 0 AND year_month = :yearMonth AND (tx_type = 'PERSONAL_HOME_EXPENSE' OR category_id = 'cat_home_expense')
    """)
    fun observeMonthlyHomeExpense(yearMonth: String): Flow<Long>

    @Query("""
        SELECT
            c.category_id AS category_id,
            c.name_bn AS name_bn,
            c.color_hex AS color_hex,
            c.icon_name AS icon_name,
            COALESCE(SUM(t.total_bill_bdt), 0) AS total_amount_bdt,
            COUNT(t.transaction_id) AS tx_count
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.category_id
        WHERE t.is_deleted = 0
          AND t.date_key = :dateKey
          AND t.tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE')
        GROUP BY c.category_id
        ORDER BY total_amount_bdt DESC
    """)
    fun observeDailyExpenseByCategory(dateKey: String): Flow<List<CategoryAggregationRow>>

    // --- Monthly Analytical Dashboard Aggregations ---

    @Query("""
        SELECT
            year_month,
            COALESCE(SUM(CASE WHEN tx_type = 'SALE' THEN total_bill_bdt ELSE 0 END), 0) AS total_sales_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'WHOLESALE_PURCHASE' THEN total_bill_bdt ELSE 0 END), 0) AS total_purchase_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'OPERATING_EXPENSE' AND category_id != 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS total_operating_expense_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'ASSET_MAINTENANCE' THEN total_bill_bdt ELSE 0 END), 0) AS total_asset_expense_bdt,
            COALESCE(SUM(CASE WHEN tx_type IN ('SALE', 'BAKI_COLLECTION') THEN cash_amount_bdt ELSE 0 END), 0) AS total_cash_in_bdt,
            COALESCE(SUM(CASE WHEN tx_type = 'PERSONAL_HOME_EXPENSE' OR category_id = 'cat_home_expense' THEN total_bill_bdt ELSE 0 END), 0) AS total_home_expense_bdt
        FROM transactions
        WHERE is_deleted = 0 AND year_month IN (:yearMonths)
        GROUP BY year_month
        ORDER BY year_month ASC
    """)
    fun observeMonthlyIncomeExpense(yearMonths: List<String>): Flow<List<MonthlyIncomeExpenseRow>>

    @Query("""
        SELECT
            c.category_id AS category_id,
            c.name_bn AS name_bn,
            c.color_hex AS color_hex,
            c.icon_name AS icon_name,
            COALESCE(SUM(t.total_bill_bdt), 0) AS total_amount_bdt,
            COUNT(t.transaction_id) AS tx_count
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.category_id
        WHERE t.is_deleted = 0
          AND t.year_month IN (:yearMonths)
          AND t.tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE')
        GROUP BY c.category_id
        ORDER BY total_amount_bdt DESC
    """)
    fun observePeriodExpenseByCategory(yearMonths: List<String>): Flow<List<CategoryAggregationRow>>

    @Query("""
        SELECT
            year_month,
            category_id,
            COALESCE(SUM(total_bill_bdt), 0) AS total_amount_bdt
        FROM transactions
        WHERE is_deleted = 0
          AND year_month IN (:yearMonths)
          AND tx_type IN ('WHOLESALE_PURCHASE', 'OPERATING_EXPENSE', 'ASSET_MAINTENANCE', 'PERSONAL_HOME_EXPENSE')
        GROUP BY year_month, category_id
        ORDER BY year_month ASC
    """)
    fun observeMonthlyCategoryTrends(yearMonths: List<String>): Flow<List<MonthlyCategoryTrendRow>>

    // --- Mutations & Sync Queue ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("""
        UPDATE transactions
        SET is_deleted = 1,
            updated_at = :updatedAt,
            version = version + 1,
            sync_status = 'PENDING'
        WHERE transaction_id = :transactionId
    """)
    suspend fun softDeleteTransaction(transactionId: String, updatedAt: Long)

    @Query("""
        UPDATE transactions
        SET is_deleted = 1,
            updated_at = :updatedAt,
            version = version + 1,
            sync_status = 'PENDING'
        WHERE shopkeeper_id = :shopkeeperId AND is_deleted = 0
    """)
    suspend fun softDeleteTransactionsByShopkeeper(shopkeeperId: String, updatedAt: Long)

    @Query("""
        UPDATE transactions
        SET is_deleted = 1,
            updated_at = :updatedAt,
            version = version + 1,
            sync_status = 'PENDING'
        WHERE vendor_id = :vendorId AND is_deleted = 0
    """)
    suspend fun softDeleteTransactionsByVendor(vendorId: String, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM transactions WHERE sync_status != 'SYNCED'")
    fun observePendingSyncTransactionCount(): Flow<Int>

    @Query("SELECT * FROM transactions WHERE sync_status != 'SYNCED' ORDER BY updated_at ASC LIMIT :batchSize")
    suspend fun getUnsyncedTransactions(batchSize: Int = 250): List<TransactionEntity>

    @Query("UPDATE transactions SET sync_status = 'SYNCED' WHERE transaction_id IN (:transactionIds)")
    suspend fun markTransactionsSynced(transactionIds: List<String>)

    @Query("SELECT * FROM transactions WHERE is_deleted = 0 ORDER BY event_date_millis DESC")
    suspend fun getAllActiveTransactionsForExport(): List<TransactionEntity>
}
