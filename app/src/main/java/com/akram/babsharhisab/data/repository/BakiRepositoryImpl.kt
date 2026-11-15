package com.akram.babsharhisab.data.repository

import androidx.room.withTransaction
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.data.local.AppDatabase
import com.akram.babsharhisab.data.local.dao.CategoryDao
import com.akram.babsharhisab.data.local.dao.ShopkeeperDao
import com.akram.babsharhisab.data.local.dao.TransactionDao
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.local.entity.ShopkeeperEntity
import com.akram.babsharhisab.data.local.entity.SyncStatus
import com.akram.babsharhisab.data.local.entity.TransactionEntity
import com.akram.babsharhisab.data.local.entity.TxType
import com.akram.babsharhisab.domain.model.ShopkeeperAccount
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.repository.BakiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BakiRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val shopkeeperDao: ShopkeeperDao,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : BakiRepository {

    override fun observeShopkeepers(): Flow<List<ShopkeeperAccount>> {
        return shopkeeperDao.observeActiveShopkeepers().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeShopkeeperById(shopkeeperId: String): Flow<ShopkeeperAccount?> {
        return shopkeeperDao.observeShopkeeperById(shopkeeperId).map { it?.toDomain() }
    }

    override fun observeShopkeeperLedger(shopkeeperId: String): Flow<List<TransactionItem>> {
        return combine(
            transactionDao.observeTransactionsForShopkeeper(shopkeeperId),
            categoryDao.observeAllCategories(),
            shopkeeperDao.observeShopkeeperById(shopkeeperId)
        ) { txs, cats, shopkeeper ->
            val catMap = cats.associateBy { it.categoryId }
            val shopDisplayName = shopkeeper?.let {
                if (it.shopNameBn.isNotBlank()) "${it.shopNameBn} (${it.nameBn})" else it.nameBn
            }
            txs.map { tx ->
                val cat = catMap[tx.categoryId]
                TransactionItem(
                    transactionId = tx.transactionId,
                    txType = tx.txType,
                    categoryId = tx.categoryId,
                    categoryNameBn = cat?.nameBn ?: "বাকি খাতা",
                    categoryColorHex = cat?.colorHex ?: "#D97706",
                    vendorId = tx.vendorId,
                    vendorNameBn = null,
                    shopkeeperId = tx.shopkeeperId,
                    shopkeeperNameBn = shopDisplayName,
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

    override fun observeTotalMarketBaki(): Flow<Long> = shopkeeperDao.observeTotalMarketBaki()

    override fun observeActiveBakiShopkeeperCount(): Flow<Int> =
        shopkeeperDao.observeActiveBakiShopkeeperCount()

    override suspend fun createOrUpdateShopkeeper(
        shopkeeperId: String?,
        nameBn: String,
        shopNameBn: String,
        marketAreaBn: String,
        phoneNumber: String,
        openingBakiBdt: Long,
        updatedCurrentBakiBdt: Long?
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val id = shopkeeperId ?: UUID.randomUUID().toString()
        val existing = if (shopkeeperId != null) shopkeeperDao.getShopkeeperById(shopkeeperId) else null
        val currentBaki = updatedCurrentBakiBdt?.coerceAtLeast(0L)
            ?: existing?.currentBakiBdt
            ?: openingBakiBdt.coerceAtLeast(0L)

        val entity = ShopkeeperEntity(
            shopkeeperId = id,
            nameBn = nameBn.trim(),
            shopNameBn = shopNameBn.trim(),
            marketAreaBn = marketAreaBn.trim(),
            phoneNumber = phoneNumber.trim(),
            openingBakiBdt = existing?.openingBakiBdt ?: openingBakiBdt.coerceAtLeast(0L),
            currentBakiBdt = currentBaki,
            lastTransactionAt = existing?.lastTransactionAt ?: now,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
            isDeleted = false,
            syncStatus = SyncStatus.PENDING
        )
        shopkeeperDao.upsertShopkeeper(entity)
        return id
    }

    override suspend fun deleteShopkeeper(shopkeeperId: String) {
        val now = BanglaDateUtils.nowMillis()
        db.withTransaction {
            shopkeeperDao.softDeleteShopkeeper(shopkeeperId, now)
            transactionDao.softDeleteTransactionsByShopkeeper(shopkeeperId, now)
        }
    }

    override suspend fun collectShopkeeperBaki(
        shopkeeperId: String,
        collectedCashBdt: Long,
        eventDateMillis: Long,
        noteBn: String
    ): String {
        val now = BanglaDateUtils.nowMillis()
        val txId = UUID.randomUUID().toString()

        db.withTransaction {
            val shopkeeper = shopkeeperDao.getShopkeeperById(shopkeeperId) ?: return@withTransaction
            val newBaki = (shopkeeper.currentBakiBdt - collectedCashBdt).coerceAtLeast(0L)

            shopkeeperDao.updateShopkeeperBakiBalance(
                shopkeeperId = shopkeeperId,
                newBakiBdt = newBaki,
                eventMillis = eventDateMillis,
                updatedAt = now
            )

            // BAKI_COLLECTION increments cash_amount_bdt (Realized Cash Inflow)
            // and sets baki_delta_bdt = -collectedCashBdt.
            // Because tx_type = 'BAKI_COLLECTION', it NEVER double-counts as SALE revenue.
            val tx = TransactionEntity(
                transactionId = txId,
                txType = TxType.BAKI_COLLECTION,
                categoryId = CategoryEntity.CAT_BAKI_COLLECT,
                vendorId = null,
                shopkeeperId = shopkeeperId,
                assetId = null,
                totalBillBdt = collectedCashBdt,
                cashAmountBdt = collectedCashBdt,
                bakiDeltaBdt = -collectedCashBdt,
                runningShopkeeperBakiBdt = newBaki,
                eventDateMillis = eventDateMillis,
                dateKey = BanglaDateUtils.toDateKey(eventDateMillis),
                yearMonth = BanglaDateUtils.toYearMonthKey(eventDateMillis),
                noteBn = noteBn.trim().ifEmpty { "বাকি আদায় জমা" },
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

    private fun ShopkeeperEntity.toDomain(): ShopkeeperAccount = ShopkeeperAccount(
        shopkeeperId = shopkeeperId,
        nameBn = nameBn,
        shopNameBn = shopNameBn,
        marketAreaBn = marketAreaBn,
        phoneNumber = phoneNumber,
        openingBakiBdt = openingBakiBdt,
        currentBakiBdt = currentBakiBdt,
        lastTransactionAt = lastTransactionAt,
        syncStatus = syncStatus
    )
}
