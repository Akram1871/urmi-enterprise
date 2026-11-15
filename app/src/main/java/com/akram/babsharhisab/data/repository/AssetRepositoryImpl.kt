package com.akram.babsharhisab.data.repository

import androidx.room.withTransaction
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.data.local.AppDatabase
import com.akram.babsharhisab.data.local.dao.AssetDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.entity.AssetMaintenanceEntity
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.SyncStatus
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import com.akram.babsharhisab.data.local.entity.TxType
import com.akram.babsharhisab.domain.model.AssetItem
import com.akram.babsharhisab.domain.repository.AssetRepository
import com.akram.babsharhisab.domain.usecase.AccountingCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val assetDao: AssetDao,
    private val transactionDao: TransactionDao
) : AssetRepository {

    override fun observeActiveAssets(): Flow<List<AssetItem>> =
        assetDao.observeActiveAssets().map { list -> list.map { it.toDomain() } }

    override fun observeAllAssets(): Flow<List<AssetItem>> =
        assetDao.observeAllAssets().map { list -> list.map { it.toDomain() } }

    override fun observeTotalDailyDepreciation(): Flow<Long> =
        assetDao.observeTotalDailyDepreciation()

    override fun observeTotalMonthlyDepreciation(): Flow<Long> =
        assetDao.observeTotalMonthlyDepreciation()

    override suspend fun saveOrReplaceAsset(
        assetId: String?,
        assetNameBn: String,
        purchaseCostBdt: Long,
        lifespanMonths: Int,
        purchaseDateMillis: Long,
        notesBn: String,
        recordAsTodayCashExpense: Boolean
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val id = assetId ?: UUID.randomUUID().toString()
        val math = AccountingCalculator.calculateAssetMetrics(
            purchaseCostBdt = purchaseCostBdt,
            lifespanMonths = lifespanMonths,
            purchaseDateMillis = purchaseDateMillis,
            currentMillis = now
        )

        db.withTransaction {
            val entity = AssetMaintenanceEntity(
                assetId = id,
                assetNameBn = assetNameBn.trim(),
                categoryId = CategoryEntity.CAT_ASSET,
                purchaseCostBdt = purchaseCostBdt,
                lifespanMonths = lifespanMonths.coerceAtLeast(1),
                purchaseDateMillis = purchaseDateMillis,
                nextDueDateMillis = math.nextDueDateMillis,
                dailyDepreciationBdt = math.dailyDepreciationBdt,
                monthlyDepreciationBdt = math.monthlyDepreciationBdt,
                notesBn = notesBn.trim(),
                isActive = true,
                updatedAt = now,
                isDeleted = false,
                syncStatus = SyncStatus.PENDING
            )
            assetDao.upsertAsset(entity)

            if (recordAsTodayCashExpense && purchaseCostBdt > 0L) {
                val tx = TransactionEntity(
                    transactionId = UUID.randomUUID().toString(),
                    txType = TxType.ASSET_MAINTENANCE,
                    categoryId = CategoryEntity.CAT_ASSET,
                    vendorId = null,
                    shopkeeperId = null,
                    assetId = id,
                    totalBillBdt = purchaseCostBdt,
                    cashAmountBdt = purchaseCostBdt,
                    bakiDeltaBdt = 0L,
                    runningShopkeeperBakiBdt = 0L,
                    eventDateMillis = purchaseDateMillis,
                    dateKey = BanglaDateUtils.toDateKey(purchaseDateMillis),
                    yearMonth = BanglaDateUtils.toYearMonthKey(purchaseDateMillis),
                    noteBn = "সম্পদ/ব্যাটারি পরিবর্তন: ${assetNameBn.trim()}",
                    createdAt = now,
                    updatedAt = now,
                    version = 1,
                    isDeleted = false,
                    syncStatus = SyncStatus.PENDING
                )
                transactionDao.upsertTransaction(tx)
            }
        }
        return id
    }

    override suspend fun archiveAsset(assetId: String) {
        val existing = assetDao.getAssetById(assetId) ?: return
        assetDao.updateAsset(
            existing.copy(
                isActive = false,
                isDeleted = true,
                updatedAt = BanglaDateUtils.nowMillis(),
                syncStatus = SyncStatus.PENDING
            )
        )
    }

    private fun AssetMaintenanceEntity.toDomain(): AssetItem {
        val math = AccountingCalculator.calculateAssetMetrics(
            purchaseCostBdt = purchaseCostBdt,
            lifespanMonths = lifespanMonths,
            purchaseDateMillis = purchaseDateMillis
        )
        return AssetItem(
            assetId = assetId,
            assetNameBn = assetNameBn,
            purchaseCostBdt = purchaseCostBdt,
            lifespanMonths = lifespanMonths,
            purchaseDateMillis = purchaseDateMillis,
            nextDueDateMillis = nextDueDateMillis,
            dailyDepreciationBdt = dailyDepreciationBdt,
            monthlyDepreciationBdt = monthlyDepreciationBdt,
            notesBn = notesBn,
            isActive = isActive,
            elapsedFraction = math.elapsedFraction,
            remainingDays = math.remainingDays
        )
    }
}
