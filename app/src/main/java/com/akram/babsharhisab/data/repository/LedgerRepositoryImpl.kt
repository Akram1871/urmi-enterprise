package com.akram.babsharhisab.data.repository

import androidx.room.withTransaction
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.data.local.AppDatabase
import com.akram.babsharhisab.data.local.dao.AssetDao
import com.akram.babsharhisab.data.local.dao.CategoryAggregationRow
import com.akram.babsharhisab.data.local.dao.CategoryDao
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.dao.VendorDao
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.SyncStatus
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import com.akram.babsharhisab.data.local.entity.TxType
import com.akram.babsharhisab.data.local.entity.VendorEntity
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.CategoryTrendPoint
import com.akram.babsharhisab.domain.model.DailySummary
import com.akram.babsharhisab.domain.model.MonthlyBarPoint
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.model.VendorProfile
import com.akram.babsharhisab.domain.repository.LedgerRepository
import com.akram.babsharhisab.domain.usecase.AccountingCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val categoryDao: CategoryDao,
    private val vendorDao: VendorDao,
    private val shopkeeperDao: ShopkeeperDao,
    private val assetDao: AssetDao,
    private val transactionDao: TransactionDao
) : LedgerRepository {

    override fun observeCategories(): Flow<List<CategoryEntity>> =
        categoryDao.observeAllCategories()

    override fun observeDailySummary(dateKey: String): Flow<DailySummary> {
        val yearMonthKey = dateKey.take(7)
        val marketBakiFlow = combine(
            shopkeeperDao.observeTotalMarketBaki(),
            shopkeeperDao.observeActiveBakiShopkeeperCount()
        ) { totalBaki, bakiCount -> totalBaki to bakiCount }

        val vendorBakiFlow = combine(
            vendorDao.observeTotalVendorBaki(),
            vendorDao.observeActiveBakiVendorCount()
        ) { totalVendorBaki, vendorBakiCount -> totalVendorBaki to vendorBakiCount }

        return combine(
            transactionDao.observeDailyAggregation(dateKey),
            assetDao.observeTotalDailyDepreciation(),
            marketBakiFlow,
            vendorBakiFlow,
            transactionDao.observeMonthlyHomeExpense(yearMonthKey)
        ) { agg, dailyDep, (totalBaki, bakiCount), (totalVendorBaki, vendorBakiCount), monthlyHome ->
            DailySummary(
                dateKey = dateKey,
                totalSalesBdt = agg.totalSalesBdt,
                cashSalesBdt = agg.cashSalesBdt,
                newBakiGivenBdt = agg.newBakiGivenBdt,
                bakiCollectedBdt = agg.bakiCollectedBdt,
                wholesalePurchaseBdt = agg.wholesalePurchaseBdt,
                cashPurchaseBdt = agg.cashPurchaseBdt,
                newVendorBakiBdt = agg.newVendorBakiBdt,
                vendorBakiPaidBdt = agg.vendorBakiPaidBdt,
                operatingExpenseBdt = agg.operatingExpenseBdt,
                assetMaintenanceBdt = agg.assetMaintenanceBdt,
                dailyAssetDepreciationBdt = dailyDep,
                totalActiveMarketBakiBdt = totalBaki,
                activeBakiShopkeeperCount = bakiCount,
                totalActiveVendorBakiBdt = totalVendorBaki,
                activeBakiVendorCount = vendorBakiCount,
                personalHomeExpenseBdt = agg.personalHomeExpenseBdt,
                monthlyHomeExpenseBdt = monthlyHome
            )
        }
    }

    override fun observeDailyExpenseBreakdown(dateKey: String): Flow<List<CategorySpendItem>> {
        return transactionDao.observeDailyExpenseByCategory(dateKey).map { rows ->
            mapCategoryRows(rows)
        }
    }

    override fun observeTransactionsByDate(dateKey: String): Flow<List<TransactionItem>> {
        return combine(
            transactionDao.observeTransactionsByDate(dateKey),
            categoryDao.observeAllCategories(),
            vendorDao.observeActiveVendors(),
            shopkeeperDao.observeActiveShopkeepers()
        ) { txs, cats, vendors, shopkeepers ->
            mapTransactionsToDomain(txs, cats, vendors, shopkeepers)
        }
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<TransactionItem>> {
        return combine(
            transactionDao.observeRecentTransactions(limit),
            categoryDao.observeAllCategories(),
            vendorDao.observeActiveVendors(),
            shopkeeperDao.observeActiveShopkeepers()
        ) { txs, cats, vendors, shopkeepers ->
            mapTransactionsToDomain(txs, cats, vendors, shopkeepers)
        }
    }

    override fun observeMonthlyIncomeExpense(yearMonths: List<String>): Flow<List<MonthlyBarPoint>> {
        return transactionDao.observeMonthlyIncomeExpense(yearMonths).map { rows ->
            val rowByYm = rows.associateBy { it.yearMonth }
            yearMonths.map { ym ->
                val r = rowByYm[ym]
                val sales = r?.totalSalesBdt ?: 0L
                val purchase = r?.totalPurchaseBdt ?: 0L
                val ops = (r?.totalOperatingExpenseBdt ?: 0L) + (r?.totalAssetExpenseBdt ?: 0L)
                val totalExp = purchase + ops
                val homeExp = r?.totalHomeExpenseBdt ?: 0L
                MonthlyBarPoint(
                    yearMonth = ym,
                    labelBn = BanglaDateUtils.formatYearMonthBangla(ym, short = true),
                    totalSalesBdt = sales,
                    totalExpenseBdt = totalExp,
                    wholesalePurchaseBdt = purchase,
                    operatingExpenseBdt = ops,
                    netProfitBdt = sales - totalExp,
                    personalHomeExpenseBdt = homeExp
                )
            }
        }
    }

    override fun observePeriodCategoryBreakdown(yearMonths: List<String>): Flow<List<CategorySpendItem>> {
        return transactionDao.observePeriodExpenseByCategory(yearMonths).map { rows ->
            mapCategoryRows(rows)
        }
    }

    override fun observeCategoryTrend(
        yearMonths: List<String>,
        categoryId: String
    ): Flow<List<CategoryTrendPoint>> {
        return transactionDao.observeMonthlyCategoryTrends(yearMonths).map { rows ->
            val filtered = rows.filter { it.categoryId == categoryId }.associateBy { it.yearMonth }
            yearMonths.map { ym ->
                CategoryTrendPoint(
                    yearMonth = ym,
                    labelBn = BanglaDateUtils.formatYearMonthBangla(ym, short = true),
                    amountBdt = filtered[ym]?.totalAmountBdt ?: 0L
                )
            }
        }
    }

    override fun observeVendors(): Flow<List<VendorProfile>> {
        return vendorDao.observeActiveVendors().map { list ->
            list.map { v ->
                VendorProfile(
                    vendorId = v.vendorId,
                    nameBn = v.nameBn,
                    marketLocationBn = v.marketLocationBn,
                    phoneNumber = v.phoneNumber,
                    totalPurchasedBdt = v.totalPurchasedBdt,
                    currentBakiBdt = v.currentBakiBdt
                )
            }
        }
    }

    override fun observeTotalVendorBaki(): Flow<Long> = vendorDao.observeTotalVendorBaki()

    override fun observeActiveBakiVendorCount(): Flow<Int> = vendorDao.observeActiveBakiVendorCount()

    override suspend fun createOrUpdateVendor(
        vendorId: String?,
        nameBn: String,
        marketLocationBn: String,
        phoneNumber: String,
        openingBakiBdt: Long,
        updatedCurrentBakiBdt: Long?
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val id = vendorId ?: UUID.randomUUID().toString()
        val existing = if (vendorId != null) vendorDao.getVendorById(vendorId) else null
        val currentBaki = updatedCurrentBakiBdt?.coerceAtLeast(0L)
            ?: existing?.currentBakiBdt
            ?: openingBakiBdt.coerceAtLeast(0L)
        val entity = VendorEntity(
            vendorId = id,
            nameBn = nameBn.trim(),
            marketLocationBn = marketLocationBn.trim(),
            phoneNumber = phoneNumber.trim(),
            totalPurchasedBdt = existing?.totalPurchasedBdt ?: openingBakiBdt.coerceAtLeast(0L),
            currentBakiBdt = currentBaki,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            isDeleted = false,
            syncStatus = SyncStatus.PENDING
        )
        vendorDao.upsertVendor(entity)
        return id
    }

    override suspend fun deleteVendor(vendorId: String) {
        val now = BanglaDateUtils.nowMillis()
        db.withTransaction {
            vendorDao.softDeleteVendor(vendorId, now)
        }
    }

    override suspend fun recordWholesalePurchase(
        vendorId: String?,
        amountBdt: Long,
        cashPaidBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String {
        if (!vendorId.isNullOrBlank() && amountBdt <= 0L && cashPaidBdt > 0L) {
            return payVendorBaki(
                vendorId = vendorId,
                paidCashBdt = cashPaidBdt,
                eventDateMillis = eventDateMillis,
                noteBn = noteBn.ifBlank { "মহাজনের বকেয়া পরিশোধ" }
            )
        }

        val now = BanglaDateUtils.nowMillis()
        val txId = UUID.randomUUID().toString()
        val effectiveCash = if (vendorId.isNullOrBlank()) {
            amountBdt
        } else {
            cashPaidBdt.coerceIn(0L, amountBdt)
        }
        val extraOldBakiPaid = if (!vendorId.isNullOrBlank() && cashPaidBdt > amountBdt) {
            cashPaidBdt - amountBdt
        } else {
            0L
        }
        val bakiDelta = if (!vendorId.isNullOrBlank()) {
            (amountBdt - effectiveCash).coerceAtLeast(0L)
        } else {
            0L
        }

        db.withTransaction {
            val tx = TransactionEntity(
                transactionId = txId,
                txType = TxType.WHOLESALE_PURCHASE,
                categoryId = CategoryEntity.CAT_PURCHASE,
                vendorId = vendorId,
                shopkeeperId = null,
                assetId = null,
                totalBillBdt = amountBdt,
                cashAmountBdt = effectiveCash,
                bakiDeltaBdt = bakiDelta,
                runningShopkeeperBakiBdt = 0L,
                eventDateMillis = eventDateMillis,
                dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
                yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
                noteBn = noteBn.trim(),
                createdAt = now,
                updatedAt = now,
                version = 1,
                isDeleted = false,
                syncStatus = SyncStatus.PENDING
            )
            transactionDao.upsertTransaction(tx)
            if (!vendorId.isNullOrBlank()) {
                vendorDao.incrementVendorPurchaseAndBaki(
                    vendorId = vendorId,
                    deltaBdt = amountBdt,
                    bakiDeltaBdt = bakiDelta,
                    updatedAt = now
                )
                if (extraOldBakiPaid > 0L) {
                    val vendor = vendorDao.getVendorById(vendorId)
                    if (vendor != null) {
                        val newBaki = (vendor.currentBakiBdt - extraOldBakiPaid).coerceAtLeast(0L)
                        vendorDao.updateVendorBakiBalance(
                            vendorId = vendorId,
                            newBakiBdt = newBaki,
                            updatedAt = now
                        )
                        val payTx = TransactionEntity(
                            transactionId = UUID.randomUUID().toString(),
                            txType = TxType.VENDOR_BAKI_PAYMENT,
                            categoryId = CategoryEntity.CAT_VENDOR_BAKI_PAY,
                            vendorId = vendorId,
                            shopkeeperId = null,
                            assetId = null,
                            totalBillBdt = extraOldBakiPaid,
                            cashAmountBdt = extraOldBakiPaid,
                            bakiDeltaBdt = -extraOldBakiPaid,
                            runningShopkeeperBakiBdt = newBaki,
                            eventDateMillis = eventDateMillis,
                            dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
                            yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
                            noteBn = noteBn.trim().ifEmpty { "মাল কেনার সাথে পূর্বের বকেয়া পরিশোধ" },
                            createdAt = now,
                            updatedAt = now,
                            version = 1,
                            isDeleted = false,
                            syncStatus = SyncStatus.PENDING
                        )
                        transactionDao.upsertTransaction(payTx)
                    }
                }
            }
        }
        return txId
    }

    override suspend fun payVendorBaki(
        vendorId: String,
        paidCashBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val txId = UUID.randomUUID().toString()
        db.withTransaction {
            val vendor = vendorDao.getVendorById(vendorId) ?: return@withTransaction
            val newBaki = (vendor.currentBakiBdt - paidCashBdt).coerceAtLeast(0L)
            vendorDao.updateVendorBakiBalance(
                vendorId = vendorId,
                newBakiBdt = newBaki,
                updatedAt = now
            )

            // VENDOR_BAKI_PAYMENT records cash outflow for paying off previous Mahajan Baki
            // without double-counting wholesalePurchaseBdt.
            val tx = TransactionEntity(
                transactionId = txId,
                txType = TxType.VENDOR_BAKI_PAYMENT,
                categoryId = CategoryEntity.CAT_VENDOR_BAKI_PAY,
                vendorId = vendorId,
                shopkeeperId = null,
                assetId = null,
                totalBillBdt = paidCashBdt,
                cashAmountBdt = paidCashBdt,
                bakiDeltaBdt = -paidCashBdt,
                runningShopkeeperBakiBdt = newBaki,
                eventDateMillis = eventDateMillis,
                dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
                yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
                noteBn = noteBn.trim().ifEmpty { "মহাজনের বকেয়া পরিশোধ" },
                createdAt = now,
                updatedAt = now,
                version = 1,
                isDeleted = false,
                syncStatus = SyncStatus.PENDING
            )
            transactionDao.upsertTransaction(tx)
        }
        return txId
    }

    override suspend fun recordOperatingExpense(
        categoryId: String,
        amountBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val txId = UUID.randomUUID().toString()
        val effectiveTxType = if (categoryId == CategoryEntity.CAT_HOME_EXPENSE) {
            TxType.PERSONAL_HOME_EXPENSE
        } else {
            TxType.OPERATING_EXPENSE
        }
        val tx = TransactionEntity(
            transactionId = txId,
            txType = effectiveTxType,
            categoryId = categoryId,
            vendorId = null,
            shopkeeperId = null,
            assetId = null,
            totalBillBdt = amountBdt,
            cashAmountBdt = amountBdt,
            bakiDeltaBdt = 0L,
            runningShopkeeperBakiBdt = 0L,
            eventDateMillis = eventDateMillis,
            dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
            yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
            noteBn = noteBn.trim(),
            createdAt = now,
            updatedAt = now,
            version = 1,
            isDeleted = false,
            syncStatus = SyncStatus.PENDING
        )
        transactionDao.upsertTransaction(tx)
        return txId
    }

    override suspend fun recordSaleWithBaki(
        shopkeeperId: String?,
        totalBillBdt: Long,
        cashReceivedBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val txId = UUID.randomUUID().toString()
        val bakiDelta = if (!shopkeeperId.isNullOrBlank()) {
            AccountingCalculator.computeSaleBakiDelta(totalBillBdt, cashReceivedBdt)
        } else {
            0L
        }
        val effectiveCash = if (shopkeeperId.isNullOrBlank()) totalBillBdt else cashReceivedBdt

        db.withTransaction {
            var newRunningBaki = 0L
            if (!shopkeeperId.isNullOrBlank()) {
                val shopkeeper = shopkeeperDao.getShopkeeperById(shopkeeperId)
                if (shopkeeper != null) {
                    newRunningBaki = (shopkeeper.currentBakiBdt + bakiDelta).coerceAtLeast(0L)
                    shopkeeperDao.updateShopkeeperBakiBalance(
                        shopkeeperId = shopkeeperId,
                        newBakiBdt = newRunningBaki,
                        eventMillis = eventDateMillis,
                        updatedAt = now
                    )
                }
            }

            val tx = TransactionEntity(
                transactionId = txId,
                txType = TxType.SALE,
                categoryId = CategoryEntity.CAT_SALE,
                vendorId = null,
                shopkeeperId = shopkeeperId,
                assetId = null,
                totalBillBdt = totalBillBdt,
                cashAmountBdt = effectiveCash,
                bakiDeltaBdt = bakiDelta,
                runningShopkeeperBakiBdt = newRunningBaki,
                eventDateMillis = eventDateMillis,
                dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
                yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
                noteBn = noteBn.trim(),
                createdAt = now,
                updatedAt = now,
                version = 1,
                isDeleted = false,
                syncStatus = SyncStatus.PENDING
            )
            transactionDao.upsertTransaction(tx)
        }
        return txId
    }

    override suspend fun updateTransaction(
        transactionId: String,
        newTotalBillBdt: Long,
        newCashAmountBdt: Long,
        newNoteBn: String
    ) {
        val now = BanglaDateUtils.nowMillis()
        db.withTransaction {
            val existing = transactionDao.getTransactionById(transactionId) ?: return@withTransaction
            if (existing.isDeleted) return@withTransaction

            val cleanBill = newTotalBillBdt.coerceAtLeast(0L)
            val effectiveCash: Long
            val newBakiDelta: Long

            when (existing.txType) {
                TxType.SALE -> {
                    if (!existing.shopkeeperId.isNullOrBlank()) {
                        effectiveCash = newCashAmountBdt.coerceIn(0L, cleanBill)
                        newBakiDelta = (cleanBill - effectiveCash).coerceAtLeast(0L)
                    } else {
                        effectiveCash = cleanBill
                        newBakiDelta = 0L
                    }
                }
                TxType.BAKI_COLLECTION -> {
                    effectiveCash = cleanBill
                    newBakiDelta = -cleanBill
                }
                TxType.WHOLESALE_PURCHASE -> {
                    if (!existing.vendorId.isNullOrBlank()) {
                        effectiveCash = newCashAmountBdt.coerceIn(0L, cleanBill)
                        newBakiDelta = (cleanBill - effectiveCash).coerceAtLeast(0L)
                    } else {
                        effectiveCash = cleanBill
                        newBakiDelta = 0L
                    }
                }
                TxType.VENDOR_BAKI_PAYMENT -> {
                    effectiveCash = cleanBill
                    newBakiDelta = -cleanBill
                }
                else -> {
                    effectiveCash = cleanBill
                    newBakiDelta = 0L
                }
            }

            var newRunningBaki = existing.runningShopkeeperBakiBdt
            if (!existing.shopkeeperId.isNullOrBlank()) {
                val shopkeeper = shopkeeperDao.getShopkeeperById(existing.shopkeeperId)
                if (shopkeeper != null) {
                    val deltaDiff = newBakiDelta - existing.bakiDeltaBdt
                    newRunningBaki = (shopkeeper.currentBakiBdt + deltaDiff).coerceAtLeast(0L)
                    shopkeeperDao.updateShopkeeperBakiBalance(
                        shopkeeperId = existing.shopkeeperId,
                        newBakiBdt = newRunningBaki,
                        eventMillis = now,
                        updatedAt = now
                    )
                }
            }

            if (!existing.vendorId.isNullOrBlank()) {
                if (existing.txType == TxType.WHOLESALE_PURCHASE) {
                    val billDiff = cleanBill - existing.totalBillBdt
                    val bakiDiff = newBakiDelta - existing.bakiDeltaBdt
                    vendorDao.incrementVendorPurchaseAndBaki(
                        vendorId = existing.vendorId,
                        deltaBdt = billDiff,
                        bakiDeltaBdt = bakiDiff,
                        updatedAt = now
                    )
                } else if (existing.txType == TxType.VENDOR_BAKI_PAYMENT) {
                    val vendor = vendorDao.getVendorById(existing.vendorId)
                    if (vendor != null) {
                        val deltaDiff = newBakiDelta - existing.bakiDeltaBdt
                        val updatedVendorBaki = (vendor.currentBakiBdt + deltaDiff).coerceAtLeast(0L)
                        newRunningBaki = updatedVendorBaki
                        vendorDao.updateVendorBakiBalance(
                            vendorId = existing.vendorId,
                            newBakiBdt = updatedVendorBaki,
                            updatedAt = now
                        )
                    }
                }
            }

            val updatedTx = existing.copy(
                totalBillBdt = cleanBill,
                cashAmountBdt = effectiveCash,
                bakiDeltaBdt = newBakiDelta,
                runningShopkeeperBakiBdt = newRunningBaki,
                noteBn = newNoteBn.trim(),
                updatedAt = now,
                version = existing.version + 1,
                syncStatus = SyncStatus.PENDING
            )
            transactionDao.upsertTransaction(updatedTx)
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        val now = BanglaDateUtils.nowMillis()
        db.withTransaction {
            val existing = transactionDao.getTransactionById(transactionId) ?: return@withTransaction
            if (existing.isDeleted) return@withTransaction

            // Reverse Shopkeeper Baki impact if linked
            if (!existing.shopkeeperId.isNullOrBlank() && existing.bakiDeltaBdt != 0L) {
                val shopkeeper = shopkeeperDao.getShopkeeperById(existing.shopkeeperId)
                if (shopkeeper != null) {
                    val revertedBaki = (shopkeeper.currentBakiBdt - existing.bakiDeltaBdt).coerceAtLeast(0L)
                    shopkeeperDao.updateShopkeeperBakiBalance(
                        shopkeeperId = existing.shopkeeperId,
                        newBakiBdt = revertedBaki,
                        eventMillis = now,
                        updatedAt = now
                    )
                }
            }

            // Reverse Vendor Purchase total and Vendor Baki if linked
            if (!existing.vendorId.isNullOrBlank()) {
                if (existing.txType == TxType.WHOLESALE_PURCHASE) {
                    vendorDao.incrementVendorPurchaseAndBaki(
                        vendorId = existing.vendorId,
                        deltaBdt = -existing.totalBillBdt,
                        bakiDeltaBdt = -existing.bakiDeltaBdt,
                        updatedAt = now
                    )
                } else if (existing.txType == TxType.VENDOR_BAKI_PAYMENT && existing.bakiDeltaBdt != 0L) {
                    val vendor = vendorDao.getVendorById(existing.vendorId)
                    if (vendor != null) {
                        val revertedBaki = (vendor.currentBakiBdt - existing.bakiDeltaBdt).coerceAtLeast(0L)
                        vendorDao.updateVendorBakiBalance(
                            vendorId = existing.vendorId,
                            newBakiBdt = revertedBaki,
                            updatedAt = now
                        )
                    }
                }
            }

            transactionDao.softDeleteTransaction(transactionId, now)
        }
    }

    private fun mapCategoryRows(rows: List<CategoryAggregationRow>): List<CategorySpendItem> {
        val grandTotal = rows.sumOf { it.totalAmountBdt }.coerceAtLeast(1L)
        return rows.map { row ->
            CategorySpendItem(
                categoryId = row.categoryId,
                nameBn = row.nameBn,
                colorHex = row.colorHex,
                iconName = row.iconName,
                totalAmountBdt = row.totalAmountBdt,
                txCount = row.txCount,
                shareFraction = (row.totalAmountBdt.toFloat() / grandTotal.toFloat()).coerceIn(0f, 1f)
            )
        }
    }

    private fun mapTransactionsToDomain(
        txs: List<TransactionEntity>,
        cats: List<CategoryEntity>,
        vendors: List<VendorEntity>,
        shopkeepers: List<com.akram.babsharhisab.data.local.entity.ShopkeeperEntity>
    ): List<TransactionItem> {
        val catMap = cats.associateBy { it.categoryId }
        val vendorMap = vendors.associateBy { it.vendorId }
        val shopMap = shopkeepers.associateBy { it.shopkeeperId }

        return txs.map { tx ->
            val cat = catMap[tx.categoryId]
            val vendor = tx.vendorId?.let { vendorMap[it] }
            val shopkeeper = tx.shopkeeperId?.let { shopMap[it] }
            TransactionItem(
                transactionId = tx.transactionId,
                txType = tx.txType,
                categoryId = tx.categoryId,
                categoryNameBn = cat?.nameBn ?: "হিসাব",
                categoryColorHex = cat?.colorHex ?: "#047857",
                vendorId = tx.vendorId,
                vendorNameBn = vendor?.nameBn,
                shopkeeperId = tx.shopkeeperId,
                shopkeeperNameBn = shopkeeper?.let {
                    if (it.shopNameBn.isNotBlank()) "${it.shopNameBn} (${it.nameBn})" else it.nameBn
                },
                assetId = tx.assetId,
                totalBillBdt = tx.totalBillBdt,
                cashAmountBdt = tx.cashAmountBdt,
                bakiDeltaBdt = tx.bakiDeltaBdt,
                runningShopkeeperBakiBdt = tx.runningShopkeeperBakiBdt,
                eventDateMillis = tx.eventDateMillis,
                dateKey = tx.dateKey,
                noteBn = tx.noteBn,
                syncStatus = tx.syncStatus
            )
        }
    }
}
