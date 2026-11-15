package com.akram.babsharhisab.data.repository

import androidx.room.withTransaction
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.core.security.SecureConfigStore
import com.akram.babsharhisab.data.local.AppDatabase
import com.akram.babsharhisab.data.local.dao.AssetDao
import com.akram.babsharhisab.data.local.dao.CategoryDao
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao
import com.akram.babsharhisab.data.local.dao.SyncAuditDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.dao.VendorDao
import com.akram.babsharhisab.data.local.entity.AssetMaintenanceEntity
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.ShopkeeperEntity
import com.akram.babsharhisab.data.local.entity.SyncLogEntity
import com.akram.babsharhisab.data.local.entity.SyncStatus
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import com.akram.babsharhisab.data.local.entity.TxType
import com.akram.babsharhisab.data.local.entity.VendorEntity
import com.akram.babsharhisab.data.remote.SheetsWebhookClient
import com.akram.babsharhisab.data.remote.dto.AssetSheetRowDto
import com.akram.babsharhisab.data.remote.dto.DailySummarySheetRowDto
import com.akram.babsharhisab.data.remote.dto.SheetsBackupPayloadDto
import com.akram.babsharhisab.data.remote.dto.ShopkeeperSheetRowDto
import com.akram.babsharhisab.data.remote.dto.TransactionSheetRowDto
import com.akram.babsharhisab.data.remote.dto.VendorSheetRowDto
import com.akram.babsharhisab.domain.repository.BackupSyncRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupSyncRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val transactionDao: TransactionDao,
    private val shopkeeperDao: ShopkeeperDao,
    private val vendorDao: VendorDao,
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao,
    private val syncAuditDao: SyncAuditDao,
    private val secureConfigStore: SecureConfigStore,
    private val webhookClient: SheetsWebhookClient
) : BackupSyncRepository {

    override fun observePendingSyncCount(): Flow<Int> =
        transactionDao.observePendingSyncTransactionCount()

    override fun observeRecentSyncLogs(limit: Int): Flow<List<SyncLogEntity>> =
        syncAuditDao.observeRecentSyncLogs(limit)

    override fun observeLastSuccessfulSync(): Flow<SyncLogEntity?> =
        syncAuditDao.observeLastSuccessfulSync()

    override suspend fun performGoogleSheetsSync(): Result<Int> {
        val config = secureConfigStore.getCurrentConfig()
        val now = BanglaDateUtils.nowMillis()

        if (config.webAppUrl.isBlank()) {
            val msg = "গুগল শিট ওয়েব অ্যাপ লিংক এখনো সেট করা হয়নি"
            syncAuditDao.insertSyncLog(
                SyncLogEntity(
                    executedAt = now,
                    syncedRecordsCount = 0,
                    status = "FAILED",
                    messageBn = msg
                )
            )
            return Result.failure(IllegalStateException(msg))
        }

        val todayKey = BanglaDateUtils.toDateKey(now)
        val dailyAgg = transactionDao.getDailyAggregation(todayKey)
        val dailyDep = assetDao.getTotalDailyDepreciation()
        val totalMarketBaki = shopkeeperDao.getTotalMarketBaki()
        val totalVendorBaki = vendorDao.getTotalVendorBaki()

        val opProfit = dailyAgg.totalSalesBdt - (dailyAgg.wholesalePurchaseBdt + dailyAgg.operatingExpenseBdt)
        val adjProfit = opProfit - dailyDep
        val netCashFlow = (dailyAgg.cashSalesBdt + dailyAgg.bakiCollectedBdt) -
            (dailyAgg.cashPurchaseBdt + dailyAgg.vendorBakiPaidBdt + dailyAgg.operatingExpenseBdt + dailyAgg.assetMaintenanceBdt)

        val unsyncedTxs = transactionDao.getUnsyncedTransactions(batchSize = 300)
        val unsyncedShopkeepers = shopkeeperDao.getUnsyncedShopkeepers()
        val unsyncedAssets = assetDao.getUnsyncedAssets()
        val unsyncedVendors = vendorDao.getUnsyncedVendors()

        val activeVendorsList = vendorDao.getActiveVendors()
        val activeShopkeepersList = shopkeeperDao.getActiveShopkeepers()
        val allVendors = activeVendorsList.associateBy { it.vendorId }
        val allShopkeepers = activeShopkeepersList.associateBy { it.shopkeeperId }

        // Always push all active + unsynced vendors and shopkeepers so Google Sheets
        // Vendor_Baki and Shopkeeper_Baki tabs always reflect 100% accurate totals
        val vendorsToPush = (activeVendorsList + unsyncedVendors).distinctBy { it.vendorId }
        val shopkeepersToPush = (activeShopkeepersList + unsyncedShopkeepers).distinctBy { it.shopkeeperId }

        val payload = SheetsBackupPayloadDto(
            secretToken = config.secretToken,
            deviceTimestampMillis = now,
            dateKey = todayKey,
            dailySummary = DailySummarySheetRowDto(
                dateKey = todayKey,
                totalSalesBdt = dailyAgg.totalSalesBdt,
                cashSalesBdt = dailyAgg.cashSalesBdt,
                newBakiGivenBdt = dailyAgg.newBakiGivenBdt,
                bakiCollectedBdt = dailyAgg.bakiCollectedBdt,
                wholesalePurchaseBdt = dailyAgg.wholesalePurchaseBdt,
                operatingExpenseBdt = dailyAgg.operatingExpenseBdt,
                assetMaintenanceBdt = dailyAgg.assetMaintenanceBdt,
                dailyDepreciationBdt = dailyDep,
                netOperationalProfitBdt = opProfit,
                adjustedNetProfitBdt = adjProfit,
                netCashFlowBdt = netCashFlow,
                totalActiveMarketBakiBdt = totalMarketBaki,
                totalActiveVendorBakiBdt = totalVendorBaki,
                personalHomeExpenseBdt = dailyAgg.personalHomeExpenseBdt
            ),
            transactions = unsyncedTxs.map { tx ->
                TransactionSheetRowDto(
                    transactionId = tx.transactionId,
                    dateKey = tx.dateKey,
                    eventDateMillis = tx.eventDateMillis,
                    txType = tx.txType,
                    categoryId = tx.categoryId,
                    vendorId = tx.vendorId.orEmpty(),
                    vendorNameBn = tx.vendorId?.let { allVendors[it]?.nameBn }.orEmpty(),
                    shopkeeperId = tx.shopkeeperId.orEmpty(),
                    shopkeeperNameBn = tx.shopkeeperId?.let { allShopkeepers[it]?.nameBn }.orEmpty(),
                    assetId = tx.assetId.orEmpty(),
                    totalBillBdt = tx.totalBillBdt,
                    cashAmountBdt = tx.cashAmountBdt,
                    bakiDeltaBdt = tx.bakiDeltaBdt,
                    runningShopkeeperBakiBdt = tx.runningShopkeeperBakiBdt,
                    noteBn = tx.noteBn,
                    version = tx.version,
                    isDeleted = tx.isDeleted
                )
            },
            shopkeepers = shopkeepersToPush.map { s ->
                ShopkeeperSheetRowDto(
                    shopkeeperId = s.shopkeeperId,
                    nameBn = s.nameBn,
                    shopNameBn = s.shopNameBn,
                    marketAreaBn = s.marketAreaBn,
                    phoneNumber = s.phoneNumber,
                    currentBakiBdt = s.currentBakiBdt,
                    lastTransactionAt = s.lastTransactionAt,
                    isDeleted = s.isDeleted
                )
            },
            assets = unsyncedAssets.map { a ->
                AssetSheetRowDto(
                    assetId = a.assetId,
                    assetNameBn = a.assetNameBn,
                    purchaseCostBdt = a.purchaseCostBdt,
                    lifespanMonths = a.lifespanMonths,
                    purchaseDateKey = BanglaDateUtils.toDateKey(a.purchaseDateMillis),
                    nextDueDateKey = BanglaDateUtils.toDateKey(a.nextDueDateMillis),
                    dailyDepreciationBdt = a.dailyDepreciationBdt,
                    monthlyDepreciationBdt = a.monthlyDepreciationBdt,
                    notesBn = a.notesBn,
                    isActive = a.isActive,
                    isDeleted = a.isDeleted
                )
            },
            vendors = vendorsToPush.map { v ->
                VendorSheetRowDto(
                    vendorId = v.vendorId,
                    nameBn = v.nameBn,
                    marketLocationBn = v.marketLocationBn,
                    phoneNumber = v.phoneNumber,
                    totalPurchasedBdt = v.totalPurchasedBdt,
                    currentBakiBdt = v.currentBakiBdt,
                    isDeleted = v.isDeleted
                )
            }
        )

        val totalRecordsAttempted =
            unsyncedTxs.size + unsyncedShopkeepers.size + unsyncedAssets.size + unsyncedVendors.size + 1

        val networkResult = webhookClient.postBackupPayload(config.webAppUrl, payload)
        return networkResult.fold(
            onSuccess = {
                db.withTransaction {
                    if (unsyncedTxs.isNotEmpty()) {
                        transactionDao.markTransactionsSynced(unsyncedTxs.map { it.transactionId })
                    }
                    if (unsyncedShopkeepers.isNotEmpty()) {
                        shopkeeperDao.markShopkeepersSynced(unsyncedShopkeepers.map { it.shopkeeperId })
                    }
                    if (unsyncedAssets.isNotEmpty()) {
                        assetDao.markAssetsSynced(unsyncedAssets.map { it.assetId })
                    }
                    if (unsyncedVendors.isNotEmpty()) {
                        vendorDao.markVendorsSynced(unsyncedVendors.map { it.vendorId })
                    }
                    val countBn = BanglaNumberFormatter.toBanglaDigits(totalRecordsAttempted)
                    syncAuditDao.insertSyncLog(
                        SyncLogEntity(
                            executedAt = now,
                            syncedRecordsCount = totalRecordsAttempted,
                            status = "SUCCESS",
                            messageBn = "সফলভাবে গুগল শিটে ব্যাকআপ সম্পন্ন ($countBn টি রেকর্ড আপডেট হয়েছে)"
                        )
                    )
                }
                Result.success(totalRecordsAttempted)
            },
            onFailure = { err ->
                val errMsg = err.localizedMessage ?: "নেটওয়ার্ক সংযোগ ত্রুটি"
                syncAuditDao.insertSyncLog(
                    SyncLogEntity(
                        executedAt = now,
                        syncedRecordsCount = 0,
                        status = "FAILED",
                        messageBn = "ব্যাকআপ ব্যর্থ: $errMsg"
                    )
                )
                Result.failure(err)
            }
        )
    }

    override suspend fun restoreFromGoogleSheets(): Result<Int> {
        val config = secureConfigStore.getCurrentConfig()
        val now = BanglaDateUtils.nowMillis()

        if (config.webAppUrl.isBlank()) {
            val msg = "গুগল শিট ওয়েব অ্যাপ লিংক এখনো সেট করা হয়নি"
            return Result.failure(IllegalStateException(msg))
        }

        val networkResult = webhookClient.fetchRestorePayload(config.webAppUrl, config.secretToken)
        return networkResult.fold(
            onSuccess = { resp ->
                var restoredCount = 0
                db.withTransaction {
                    // 0. Ensure system categories exist
                    categoryDao.insertCategoriesIgnore(CategoryEntity.defaultSystemCategories())
                    val validCategoryIds = categoryDao.getAllCategories().map { it.categoryId }.toSet()

                    // 1. Upsert Vendors (Mahajans)
                    val vendorIdByName = mutableMapOf<String, String>()
                    for (existingV in vendorDao.getActiveVendors()) {
                        vendorIdByName[existingV.nameBn.trim()] = existingV.vendorId
                    }
                    for (v in resp.vendors) {
                        if (v.vendorId.isBlank() && v.nameBn.isBlank()) continue
                        val vId = v.vendorId.ifBlank {
                            vendorIdByName[v.nameBn.trim()] ?: UUID.randomUUID().toString()
                        }
                        val paidForVendor = resp.transactions
                            .filter { !it.isDeleted && it.txType == TxType.VENDOR_BAKI_PAYMENT && (it.vendorId == vId || it.vendorNameBn.trim() == v.nameBn.trim()) }
                            .sumOf { it.cashAmountBdt }
                        val effectiveVendorBaki = if (v.currentBakiBdt > 0L) {
                            v.currentBakiBdt
                        } else if (v.totalPurchasedBdt > 0L && paidForVendor == 0L) {
                            v.totalPurchasedBdt
                        } else {
                            0L
                        }
                        vendorDao.upsertVendor(
                            VendorEntity(
                                vendorId = vId,
                                nameBn = v.nameBn.trim().ifBlank { "মহাজন" },
                                marketLocationBn = v.marketLocationBn.trim(),
                                phoneNumber = v.phoneNumber.trim(),
                                totalPurchasedBdt = v.totalPurchasedBdt.coerceAtLeast(effectiveVendorBaki),
                                currentBakiBdt = effectiveVendorBaki,
                                createdAt = now,
                                updatedAt = now,
                                isDeleted = v.isDeleted,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        if (v.nameBn.isNotBlank()) {
                            vendorIdByName[v.nameBn.trim()] = vId
                        }
                        restoredCount++
                    }

                    // 2. Upsert Shopkeepers (Dokandars)
                    val shopkeeperIdByName = mutableMapOf<String, String>()
                    for (existingS in shopkeeperDao.getActiveShopkeepers()) {
                        shopkeeperIdByName[existingS.nameBn.trim()] = existingS.shopkeeperId
                    }
                    for (s in resp.shopkeepers) {
                        if (s.shopkeeperId.isBlank() && s.nameBn.isBlank()) continue
                        val sId = s.shopkeeperId.ifBlank {
                            shopkeeperIdByName[s.nameBn.trim()] ?: UUID.randomUUID().toString()
                        }
                        shopkeeperDao.upsertShopkeeper(
                            ShopkeeperEntity(
                                shopkeeperId = sId,
                                nameBn = s.nameBn.trim().ifBlank { "দোকানদার" },
                                shopNameBn = s.shopNameBn.trim(),
                                marketAreaBn = s.marketAreaBn.trim(),
                                phoneNumber = s.phoneNumber.trim(),
                                openingBakiBdt = 0L,
                                currentBakiBdt = s.currentBakiBdt,
                                lastTransactionAt = if (s.lastTransactionAt > 0L) s.lastTransactionAt else now,
                                createdAt = now,
                                updatedAt = now,
                                isDeleted = s.isDeleted,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        if (s.nameBn.isNotBlank()) {
                            shopkeeperIdByName[s.nameBn.trim()] = sId
                        }
                        restoredCount++
                    }

                    // 3. Upsert Assets
                    val validAssetIds = mutableSetOf<String>()
                    for (a in resp.assets) {
                        if (a.assetId.isBlank()) continue
                        val pMillis = runCatching {
                            BanglaDateUtils.localDateToNoonMillis(LocalDate.parse(a.purchaseDateKey))
                        }.getOrDefault(now)
                        val nMillis = runCatching {
                            BanglaDateUtils.localDateToNoonMillis(LocalDate.parse(a.nextDueDateKey))
                        }.getOrDefault(now)
                        assetDao.upsertAsset(
                            AssetMaintenanceEntity(
                                assetId = a.assetId,
                                assetNameBn = a.assetNameBn.ifBlank { "গাড়ির ব্যাটারি" },
                                purchaseCostBdt = a.purchaseCostBdt,
                                lifespanMonths = a.lifespanMonths.coerceAtLeast(1),
                                purchaseDateMillis = pMillis,
                                nextDueDateMillis = nMillis,
                                dailyDepreciationBdt = a.dailyDepreciationBdt,
                                monthlyDepreciationBdt = a.monthlyDepreciationBdt,
                                notesBn = a.notesBn,
                                isActive = a.isActive,
                                updatedAt = now,
                                isDeleted = a.isDeleted,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        validAssetIds.add(a.assetId)
                        restoredCount++
                    }

                    // 4. Upsert Transactions (auto-creating missing vendor/shopkeeper by name if needed)
                    val knownVendorIds = vendorIdByName.values.toMutableSet()
                    val knownShopkeeperIds = shopkeeperIdByName.values.toMutableSet()

                    for (tx in resp.transactions) {
                        if (tx.transactionId.isBlank()) continue

                        // Resolve Vendor ID safely for foreign key
                        var resolvedVendorId: String? = tx.vendorId.takeIf { it.isNotBlank() && it in knownVendorIds }
                        if (resolvedVendorId == null && tx.vendorNameBn.isNotBlank()) {
                            val trimmedVName = tx.vendorNameBn.trim()
                            resolvedVendorId = vendorIdByName[trimmedVName] ?: run {
                                val newVId = UUID.randomUUID().toString()
                                vendorDao.upsertVendor(
                                    VendorEntity(
                                        vendorId = newVId,
                                        nameBn = trimmedVName,
                                        createdAt = now,
                                        updatedAt = now,
                                        syncStatus = SyncStatus.SYNCED
                                    )
                                )
                                vendorIdByName[trimmedVName] = newVId
                                knownVendorIds.add(newVId)
                                newVId
                            }
                        }

                        // Resolve Shopkeeper ID safely for foreign key
                        var resolvedShopkeeperId: String? = tx.shopkeeperId.takeIf { it.isNotBlank() && it in knownShopkeeperIds }
                        if (resolvedShopkeeperId == null && tx.shopkeeperNameBn.isNotBlank()) {
                            val trimmedSName = tx.shopkeeperNameBn.trim()
                            resolvedShopkeeperId = shopkeeperIdByName[trimmedSName] ?: run {
                                val newSId = UUID.randomUUID().toString()
                                shopkeeperDao.upsertShopkeeper(
                                    ShopkeeperEntity(
                                        shopkeeperId = newSId,
                                        nameBn = trimmedSName,
                                        currentBakiBdt = tx.runningShopkeeperBakiBdt.coerceAtLeast(0L),
                                        lastTransactionAt = now,
                                        createdAt = now,
                                        updatedAt = now,
                                        syncStatus = SyncStatus.SYNCED
                                    )
                                )
                                shopkeeperIdByName[trimmedSName] = newSId
                                knownShopkeeperIds.add(newSId)
                                newSId
                            }
                        }

                        val safeCategoryId = if (tx.categoryId in validCategoryIds) {
                            tx.categoryId
                        } else {
                            CategoryEntity.CAT_OTHER
                        }
                        val safeDateKey = tx.dateKey.ifBlank { BanglaDateUtils.toDateKey(now) }
                        val eventMillis = if (tx.eventDateMillis > 0L) {
                            tx.eventDateMillis
                        } else {
                            runCatching {
                                BanglaDateUtils.localDateToNoonMillis(LocalDate.parse(safeDateKey))
                            }.getOrDefault(now)
                        }

                        transactionDao.upsertTransaction(
                            TransactionEntity(
                                transactionId = tx.transactionId,
                                txType = tx.txType.ifBlank { "EXPENSE" },
                                categoryId = safeCategoryId,
                                vendorId = resolvedVendorId,
                                shopkeeperId = resolvedShopkeeperId,
                                assetId = tx.assetId.takeIf { it in validAssetIds },
                                totalBillBdt = tx.totalBillBdt,
                                cashAmountBdt = tx.cashAmountBdt,
                                bakiDeltaBdt = tx.bakiDeltaBdt,
                                runningShopkeeperBakiBdt = tx.runningShopkeeperBakiBdt,
                                eventDateMillis = eventMillis,
                                dateKey = safeDateKey,
                                yearMonth = safeDateKey.take(7),
                                noteBn = tx.noteBn,
                                createdAt = eventMillis,
                                updatedAt = now,
                                version = tx.version.coerceAtLeast(1),
                                isDeleted = tx.isDeleted,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                        restoredCount++
                    }

                    val countBn = BanglaNumberFormatter.toBanglaDigits(restoredCount)
                    syncAuditDao.insertSyncLog(
                        SyncLogEntity(
                            executedAt = now,
                            syncedRecordsCount = restoredCount,
                            status = "SUCCESS",
                            messageBn = "গুগল শিট থেকে সফলভাবে $countBn টি রেকর্ড এই ফোনে রিস্টোর হয়েছে"
                        )
                    )
                }
                Result.success(restoredCount)
            },
            onFailure = { err ->
                val errMsg = err.localizedMessage ?: "নেটওয়ার্ক বা স্ক্রিপ্ট ত্রুটি"
                syncAuditDao.insertSyncLog(
                    SyncLogEntity(
                        executedAt = now,
                        syncedRecordsCount = 0,
                        status = "FAILED",
                        messageBn = "ডাটা রিস্টোর ব্যর্থ: $errMsg"
                    )
                )
                Result.failure(err)
            }
        )
    }

    override suspend fun generateCsvExportContent(): String {
        val categories = categoryDao.getAllCategories().associateBy { it.categoryId }
        val vendors = vendorDao.getActiveVendors().associateBy { it.vendorId }
        val shopkeepers = shopkeeperDao.getActiveShopkeepers().associateBy { it.shopkeeperId }
        val txs = transactionDao.getAllActiveTransactionsForExport()

        val sb = StringBuilder()
        sb.appendLine("তারিখ (Date),ধরন (Type),খাত (Category),মহাজন (Vendor),দোকানদার (Shopkeeper),মোট বিল (Total BDT),নগদ (Cash BDT),বাকি পরিবর্তন (Baki Delta),নোট (Notes)")
        for (tx in txs) {
            val catName = categories[tx.categoryId]?.nameBn ?: tx.categoryId
            val vendorName = tx.vendorId?.let { vendors[it]?.nameBn }.orEmpty()
            val shopName = tx.shopkeeperId?.let { shopkeepers[it]?.nameBn }.orEmpty()
            val safeNote = tx.noteBn.replace(",", " ").replace("\n", " ")
            sb.appendLine(
                "${tx.dateKey},${tx.txType},$catName,$vendorName,$shopName,${tx.totalBillBdt},${tx.cashAmountBdt},${tx.bakiDeltaBdt},$safeNote"
            )
        }
        return sb.toString()
    }
}
