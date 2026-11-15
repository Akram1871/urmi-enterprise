package com.akram.babsharhisab.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.data.worker.BackupWorkScheduler
import com.akram.babsharhisab.domain.model.ShopkeeperAccount
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.model.VendorProfile
import com.akram.babsharhisab.domain.repository.BakiRepository
import com.akram.babsharhisab.domain.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EntryMode(val titleBn: String) {
    PURCHASE("মাল কেনা (মহাজন)"),
    EXPENSE("গাড়ি ও দৈনিক খরচ"),
    SALE("বিক্রি ও বাকি")
}

enum class PurchaseSubMode(val labelBn: String) {
    QUICK_CASH("নগদ কেনা"),
    VENDOR_BAKI_PURCHASE("মহাজনের বাকি"),
    VENDOR_BAKI_PAY("মহাজন পরিশোধ")
}

enum class SaleSubMode(val labelBn: String) {
    QUICK_CASH("নগদ বিক্রি"),
    SHOPKEEPER_SALE("দোকানদারকে বাকি"),
    BAKI_COLLECT("বাকি আদায়")
}

/**
 * Holds independent draft inputs per mode, per expense category, per vendor, and per shopkeeper
 * so a trader can:
 * 1. Enter separate values for Purchase, each Expense Category, and Sale.
 * 2. Buy from Mahajans in Cash, or on Baki with partial Nagad, and pay Mahajan Baki later.
 * 3. Give Baki to multiple shopkeepers in the same day and collect previous Baki from other shopkeepers.
 */
data class TransactionFormState(
    val mode: EntryMode = EntryMode.EXPENSE,
    val purchaseSubMode: PurchaseSubMode = PurchaseSubMode.QUICK_CASH,
    val saleSubMode: SaleSubMode = SaleSubMode.QUICK_CASH,
    val useYesterdayDate: Boolean = false,
    val selectedCategoryId: String = CategoryEntity.CAT_FUEL,
    val selectedVendorId: String? = null,
    val selectedVendorIds: Set<String> = emptySet(),
    val selectedShopkeeperId: String? = null,
    val selectedShopkeeperIds: Set<String> = emptySet(),

    // 1. Purchase & Mahajan Baki Drafts (Single + Multi-Mahajan)
    val purchaseAmountInput: String = "",
    val purchaseCashPaidInput: String = "",
    val purchaseNoteBn: String = "",
    val vendorBakiPayAmountInput: String = "",
    val vendorBakiPayNoteBn: String = "",
    val vendorPurchaseAmountById: Map<String, String> = emptyMap(),
    val vendorCashPaidById: Map<String, String> = emptyMap(),
    val vendorBakiPayById: Map<String, String> = emptyMap(),

    // 2. Expense Drafts (Per Category ID)
    val expenseAmountByCategory: Map<String, String> = emptyMap(),
    val expenseNoteByCategory: Map<String, String> = emptyMap(),

    // 3. Sale & Multi-Shopkeeper Baki Drafts
    val saleTotalBillInput: String = "",
    val saleCashReceivedInput: String = "",
    val saleNoteBn: String = "",
    val shopkeeperBakiAmountById: Map<String, String> = emptyMap(),

    // 4. Baki Collection Draft (Single + Multi-Shopkeeper)
    val bakiCollectAmountInput: String = "",
    val bakiCollectNoteBn: String = "",
    val shopkeeperCollectAmountById: Map<String, String> = emptyMap(),

    val errorMessageBn: String? = null
) {
    val isVendorBakiPurchase: Boolean
        get() = purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE

    val isVendorBakiPayment: Boolean
        get() = purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PAY

    val isShopkeeperSale: Boolean
        get() = saleSubMode == SaleSubMode.SHOPKEEPER_SALE

    val isBakiCollection: Boolean
        get() = saleSubMode == SaleSubMode.BAKI_COLLECT

    val effectiveSelectedVendorIds: Set<String>
        get() = if (selectedVendorIds.isNotEmpty()) {
            selectedVendorIds
        } else {
            listOfNotNull(selectedVendorId).toSet()
        }

    val effectiveSelectedShopkeeperIds: Set<String>
        get() = if (selectedShopkeeperIds.isNotEmpty()) {
            selectedShopkeeperIds
        } else {
            listOfNotNull(selectedShopkeeperId).toSet()
        }

    fun getVendorPurchaseInput(vendorId: String): String =
        vendorPurchaseAmountById[vendorId] ?: if (effectiveSelectedVendorIds.size == 1 && selectedVendorId == vendorId) purchaseAmountInput else ""

    fun getVendorCashPaidInput(vendorId: String): String =
        vendorCashPaidById[vendorId] ?: if (effectiveSelectedVendorIds.size == 1 && selectedVendorId == vendorId) purchaseCashPaidInput else ""

    fun getVendorBakiPayInput(vendorId: String): String =
        vendorBakiPayById[vendorId] ?: if (effectiveSelectedVendorIds.size == 1 && selectedVendorId == vendorId) vendorBakiPayAmountInput else ""

    fun getVendorPurchaseBdt(vendorId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(getVendorPurchaseInput(vendorId))

    fun getVendorCashPaidBdt(vendorId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(getVendorCashPaidInput(vendorId))

    fun getVendorBakiPayBdt(vendorId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(getVendorBakiPayInput(vendorId))

    fun getShopkeeperBakiInput(shopkeeperId: String): String =
        shopkeeperBakiAmountById[shopkeeperId].orEmpty()

    fun getShopkeeperBakiBdt(shopkeeperId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(getShopkeeperBakiInput(shopkeeperId))

    fun getShopkeeperCollectInput(shopkeeperId: String): String =
        shopkeeperCollectAmountById[shopkeeperId]
            ?: if (effectiveSelectedShopkeeperIds.size == 1 && selectedShopkeeperId == shopkeeperId) bakiCollectAmountInput else ""

    fun getShopkeeperCollectBdt(shopkeeperId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(getShopkeeperCollectInput(shopkeeperId))

    val totalMultiVendorPurchaseBdt: Long
        get() = effectiveSelectedVendorIds.sumOf { getVendorPurchaseBdt(it) }

    val totalMultiVendorCashPaidBdt: Long
        get() = effectiveSelectedVendorIds.sumOf { getVendorCashPaidBdt(it) }

    val totalMultiVendorBakiPayBdt: Long
        get() = effectiveSelectedVendorIds.sumOf { getVendorBakiPayBdt(it) }

    val totalMultiShopkeeperBakiBdt: Long
        get() = effectiveSelectedShopkeeperIds.sumOf { getShopkeeperBakiBdt(it) }

    val totalMultiShopkeeperCollectBdt: Long
        get() = effectiveSelectedShopkeeperIds.sumOf { getShopkeeperCollectBdt(it) }

    /**
     * Active primary amount string for the currently selected mode & sub-mode.
     */
    val totalBillInput: String
        get() = when (mode) {
            EntryMode.PURCHASE -> when (purchaseSubMode) {
                PurchaseSubMode.QUICK_CASH, PurchaseSubMode.VENDOR_BAKI_PURCHASE -> purchaseAmountInput
                PurchaseSubMode.VENDOR_BAKI_PAY -> vendorBakiPayAmountInput
            }
            EntryMode.EXPENSE -> expenseAmountByCategory[selectedCategoryId].orEmpty()
            EntryMode.SALE -> when (saleSubMode) {
                SaleSubMode.QUICK_CASH, SaleSubMode.SHOPKEEPER_SALE -> saleTotalBillInput
                SaleSubMode.BAKI_COLLECT -> bakiCollectAmountInput
            }
        }

    /**
     * Active cash paid/received string for split Cash + Baki modes (Mahajan Purchase or Shopkeeper Sale).
     */
    val cashReceivedInput: String
        get() = when (mode) {
            EntryMode.PURCHASE -> purchaseCashPaidInput
            else -> saleCashReceivedInput
        }

    /**
     * Active note string for the currently selected mode & sub-mode.
     */
    val noteBn: String
        get() = when (mode) {
            EntryMode.PURCHASE -> when (purchaseSubMode) {
                PurchaseSubMode.QUICK_CASH, PurchaseSubMode.VENDOR_BAKI_PURCHASE -> purchaseNoteBn
                PurchaseSubMode.VENDOR_BAKI_PAY -> vendorBakiPayNoteBn
            }
            EntryMode.EXPENSE -> expenseNoteByCategory[selectedCategoryId].orEmpty()
            EntryMode.SALE -> when (saleSubMode) {
                SaleSubMode.QUICK_CASH, SaleSubMode.SHOPKEEPER_SALE -> saleNoteBn
                SaleSubMode.BAKI_COLLECT -> bakiCollectNoteBn
            }
        }

    val parsedTotalBdt: Long
        get() = BanglaNumberFormatter.parseBdtAmount(totalBillInput)

    /**
     * Note: We do NOT clamp `parsedPurchaseCashPaidBdt` to `parsedTotalBdt` anymore because
     * a trader can buy `৳ ১০,০০০` of new goods AND pay `৳ ২৫,০০০` cash today (`৳ ১০,০০০` for new
     * goods + `৳ ১৫,০০০` towards previous Mahajan Baki).
     */
    val parsedPurchaseCashPaidBdt: Long
        get() = if (purchaseSubMode == PurchaseSubMode.QUICK_CASH) {
            parsedTotalBdt
        } else {
            BanglaNumberFormatter.parseBdtAmount(purchaseCashPaidInput).coerceAtLeast(0L)
        }

    val calculatedNewVendorBakiBdt: Long
        get() = if (mode == EntryMode.PURCHASE && purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE) {
            (parsedTotalBdt - parsedPurchaseCashPaidBdt).coerceAtLeast(0L)
        } else {
            0L
        }

    val extraOldVendorBakiPaidBdt: Long
        get() = if (mode == EntryMode.PURCHASE && purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE) {
            (parsedPurchaseCashPaidBdt - parsedTotalBdt).coerceAtLeast(0L)
        } else {
            0L
        }

    val parsedCashBdt: Long
        get() = if (saleCashReceivedInput.isBlank() && saleSubMode == SaleSubMode.QUICK_CASH) {
            parsedTotalBdt
        } else {
            BanglaNumberFormatter.parseBdtAmount(saleCashReceivedInput)
        }

    /**
     * Effective total selling amount in `SHOPKEEPER_SALE`:
     * If the user entered per-Dokandar Baki amounts (`totalMultiShopkeeperBakiBdt > 0`),
     * the total selling is `maxOf(parsedTotalBdt, totalMultiShopkeeperBakiBdt + parsedCashBdt)`.
     */
    val effectiveShopkeeperSaleTotalBdt: Long
        get() = if (totalMultiShopkeeperBakiBdt > 0L) {
            maxOf(parsedTotalBdt, totalMultiShopkeeperBakiBdt + parsedCashBdt)
        } else {
            parsedTotalBdt
        }

    /**
     * Remaining Cash Sales when multiple Dokandars' Baki is subtracted from Total Selling.
     */
    val calculatedRemainingCashSaleBdt: Long
        get() = if (totalMultiShopkeeperBakiBdt > 0L) {
            if (parsedTotalBdt >= totalMultiShopkeeperBakiBdt) {
                parsedTotalBdt - totalMultiShopkeeperBakiBdt
            } else {
                parsedCashBdt
            }
        } else {
            parsedCashBdt
        }

    val calculatedNewBakiBdt: Long
        get() = if (mode == EntryMode.SALE && saleSubMode == SaleSubMode.SHOPKEEPER_SALE) {
            if (totalMultiShopkeeperBakiBdt > 0L) {
                totalMultiShopkeeperBakiBdt
            } else {
                (parsedTotalBdt - parsedCashBdt).coerceAtLeast(0L)
            }
        } else {
            0L
        }

    fun getExpenseAmountForCategory(categoryId: String): Long =
        BanglaNumberFormatter.parseBdtAmount(expenseAmountByCategory[categoryId].orEmpty())
}

sealed interface TransactionUiEffect {
    data class ShowMessage(val messageBn: String) : TransactionUiEffect
    data object NavigateBack : TransactionUiEffect
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val bakiRepository: BakiRepository,
    private val backupWorkScheduler: BackupWorkScheduler
) : ViewModel() {

    private val _formState = MutableStateFlow(TransactionFormState())
    val formState: StateFlow<TransactionFormState> = _formState

    private val _effect = Channel<TransactionUiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    val categories: StateFlow<List<CategoryEntity>> = ledgerRepository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryEntity.defaultSystemCategories())

    val vendors: StateFlow<List<VendorProfile>> = ledgerRepository.observeVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shopkeepers: StateFlow<List<ShopkeeperAccount>> = bakiRepository.observeShopkeepers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<TransactionItem>> = ledgerRepository.observeRecentTransactions(200)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun initializeMode(modeArg: String?) {
        val upper = modeArg?.uppercase()
        val mode = when (upper) {
            "PURCHASE", "VENDOR_BAKI", "VENDOR_PAY" -> EntryMode.PURCHASE
            "SALE", "BAKI_SALE", "BAKI_COLLECT" -> EntryMode.SALE
            else -> EntryMode.EXPENSE
        }
        val purchaseSubMode = when (upper) {
            "VENDOR_BAKI" -> PurchaseSubMode.VENDOR_BAKI_PURCHASE
            "VENDOR_PAY" -> PurchaseSubMode.VENDOR_BAKI_PAY
            else -> _formState.value.purchaseSubMode
        }
        val saleSubMode = when (upper) {
            "BAKI_SALE" -> SaleSubMode.SHOPKEEPER_SALE
            "BAKI_COLLECT" -> SaleSubMode.BAKI_COLLECT
            else -> _formState.value.saleSubMode
        }
        _formState.update {
            it.copy(
                mode = mode,
                purchaseSubMode = purchaseSubMode,
                saleSubMode = saleSubMode,
                selectedCategoryId = when (mode) {
                    EntryMode.PURCHASE -> CategoryEntity.CAT_PURCHASE
                    EntryMode.SALE -> CategoryEntity.CAT_SALE
                    EntryMode.EXPENSE -> when {
                        upper == "HOME_EXPENSE" -> CategoryEntity.CAT_HOME_EXPENSE
                        it.selectedCategoryId == CategoryEntity.CAT_PURCHASE ||
                            it.selectedCategoryId == CategoryEntity.CAT_SALE -> CategoryEntity.CAT_FUEL
                        else -> it.selectedCategoryId
                    }
                },
                errorMessageBn = null
            )
        }
    }

    fun setEntryMode(mode: EntryMode) {
        _formState.update {
            it.copy(
                mode = mode,
                selectedCategoryId = when (mode) {
                    EntryMode.PURCHASE -> CategoryEntity.CAT_PURCHASE
                    EntryMode.SALE -> CategoryEntity.CAT_SALE
                    EntryMode.EXPENSE -> if (
                        it.selectedCategoryId == CategoryEntity.CAT_PURCHASE ||
                        it.selectedCategoryId == CategoryEntity.CAT_SALE
                    ) CategoryEntity.CAT_FUEL else it.selectedCategoryId
                },
                errorMessageBn = null
            )
        }
    }

    fun setPurchaseSubMode(subMode: PurchaseSubMode) {
        _formState.update {
            it.copy(
                purchaseSubMode = subMode,
                purchaseCashPaidInput = if (subMode == PurchaseSubMode.QUICK_CASH) it.purchaseAmountInput else it.purchaseCashPaidInput,
                errorMessageBn = null
            )
        }
    }

    fun setSaleSubMode(subMode: SaleSubMode) {
        _formState.update {
            it.copy(
                saleSubMode = subMode,
                saleCashReceivedInput = if (subMode == SaleSubMode.QUICK_CASH) it.saleTotalBillInput else it.saleCashReceivedInput,
                errorMessageBn = null
            )
        }
    }

    fun toggleDateYesterday(useYesterday: Boolean) {
        _formState.update { it.copy(useYesterdayDate = useYesterday) }
    }

    fun selectCategory(categoryId: String) {
        _formState.update { it.copy(selectedCategoryId = categoryId, errorMessageBn = null) }
    }

    fun selectVendor(vendorId: String?) {
        _formState.update { state ->
            if (vendorId == null) {
                state.copy(
                    selectedVendorId = null,
                    selectedVendorIds = emptySet(),
                    errorMessageBn = null
                )
            } else {
                state.copy(
                    selectedVendorId = vendorId,
                    selectedVendorIds = state.selectedVendorIds + vendorId,
                    errorMessageBn = null
                )
            }
        }
    }

    fun toggleVendorSelection(vendorId: String) {
        _formState.update { state ->
            val currentSet = state.effectiveSelectedVendorIds
            val nextSet = if (currentSet.contains(vendorId)) {
                currentSet - vendorId
            } else {
                currentSet + vendorId
            }
            val nextPrimary = if (nextSet.contains(vendorId)) vendorId else nextSet.lastOrNull()
            state.copy(
                selectedVendorId = nextPrimary,
                selectedVendorIds = nextSet,
                errorMessageBn = null
            )
        }
    }

    fun removeVendorSelection(vendorId: String) {
        _formState.update { state ->
            val nextSet = state.effectiveSelectedVendorIds - vendorId
            state.copy(
                selectedVendorId = nextSet.lastOrNull(),
                selectedVendorIds = nextSet,
                errorMessageBn = null
            )
        }
    }

    fun updateVendorPurchaseAmount(vendorId: String, rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            val nextMap = state.vendorPurchaseAmountById.toMutableMap()
            nextMap[vendorId] = ascii
            val isSingle = state.effectiveSelectedVendorIds.size <= 1
            state.copy(
                selectedVendorId = vendorId,
                selectedVendorIds = state.effectiveSelectedVendorIds + vendorId,
                vendorPurchaseAmountById = nextMap,
                purchaseAmountInput = if (isSingle) ascii else state.purchaseAmountInput,
                errorMessageBn = null
            )
        }
    }

    fun updateVendorCashPaidAmount(vendorId: String, rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            val nextMap = state.vendorCashPaidById.toMutableMap()
            nextMap[vendorId] = ascii
            val isSingle = state.effectiveSelectedVendorIds.size <= 1
            state.copy(
                selectedVendorId = vendorId,
                selectedVendorIds = state.effectiveSelectedVendorIds + vendorId,
                vendorCashPaidById = nextMap,
                purchaseCashPaidInput = if (isSingle) ascii else state.purchaseCashPaidInput,
                errorMessageBn = null
            )
        }
    }

    fun updateVendorBakiPayAmount(vendorId: String, rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            val nextMap = state.vendorBakiPayById.toMutableMap()
            nextMap[vendorId] = ascii
            val isSingle = state.effectiveSelectedVendorIds.size <= 1
            state.copy(
                selectedVendorId = vendorId,
                selectedVendorIds = state.effectiveSelectedVendorIds + vendorId,
                vendorBakiPayById = nextMap,
                vendorBakiPayAmountInput = if (isSingle) ascii else state.vendorBakiPayAmountInput,
                errorMessageBn = null
            )
        }
    }

    fun selectShopkeeper(shopkeeperId: String?) {
        _formState.update { state ->
            if (shopkeeperId == null) {
                state.copy(
                    selectedShopkeeperId = null,
                    selectedShopkeeperIds = emptySet(),
                    errorMessageBn = null
                )
            } else {
                state.copy(
                    selectedShopkeeperId = shopkeeperId,
                    selectedShopkeeperIds = state.selectedShopkeeperIds + shopkeeperId,
                    errorMessageBn = null
                )
            }
        }
    }

    fun toggleShopkeeperSelection(shopkeeperId: String) {
        _formState.update { state ->
            val currentSet = state.effectiveSelectedShopkeeperIds
            val nextSet = if (currentSet.contains(shopkeeperId)) {
                currentSet - shopkeeperId
            } else {
                currentSet + shopkeeperId
            }
            val nextPrimary = if (nextSet.contains(shopkeeperId)) shopkeeperId else nextSet.lastOrNull()
            state.copy(
                selectedShopkeeperId = nextPrimary,
                selectedShopkeeperIds = nextSet,
                errorMessageBn = null
            )
        }
    }

    fun removeShopkeeperSelection(shopkeeperId: String) {
        _formState.update { state ->
            val nextSet = state.effectiveSelectedShopkeeperIds - shopkeeperId
            state.copy(
                selectedShopkeeperId = nextSet.lastOrNull(),
                selectedShopkeeperIds = nextSet,
                errorMessageBn = null
            )
        }
    }

    fun updateShopkeeperBakiAmount(shopkeeperId: String, rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            val nextMap = state.shopkeeperBakiAmountById.toMutableMap()
            nextMap[shopkeeperId] = ascii
            state.copy(
                selectedShopkeeperId = shopkeeperId,
                selectedShopkeeperIds = state.effectiveSelectedShopkeeperIds + shopkeeperId,
                shopkeeperBakiAmountById = nextMap,
                errorMessageBn = null
            )
        }
    }

    fun updateShopkeeperCollectAmount(shopkeeperId: String, rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            val nextMap = state.shopkeeperCollectAmountById.toMutableMap()
            nextMap[shopkeeperId] = ascii
            val isSingle = state.effectiveSelectedShopkeeperIds.size <= 1
            state.copy(
                selectedShopkeeperId = shopkeeperId,
                selectedShopkeeperIds = state.effectiveSelectedShopkeeperIds + shopkeeperId,
                shopkeeperCollectAmountById = nextMap,
                bakiCollectAmountInput = if (isSingle) ascii else state.bakiCollectAmountInput,
                errorMessageBn = null
            )
        }
    }

    fun updateTotalAmount(rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            when (state.mode) {
                EntryMode.PURCHASE -> when (state.purchaseSubMode) {
                    PurchaseSubMode.QUICK_CASH -> state.copy(
                        purchaseAmountInput = ascii,
                        purchaseCashPaidInput = ascii,
                        errorMessageBn = null
                    )
                    PurchaseSubMode.VENDOR_BAKI_PURCHASE -> {
                        val nextVendorMap = state.vendorPurchaseAmountById.toMutableMap()
                        if (state.effectiveSelectedVendorIds.size == 1 && state.selectedVendorId != null) {
                            nextVendorMap[state.selectedVendorId] = ascii
                        }
                        state.copy(
                            purchaseAmountInput = ascii,
                            vendorPurchaseAmountById = nextVendorMap,
                            errorMessageBn = null
                        )
                    }
                    PurchaseSubMode.VENDOR_BAKI_PAY -> {
                        val nextPayMap = state.vendorBakiPayById.toMutableMap()
                        if (state.effectiveSelectedVendorIds.size == 1 && state.selectedVendorId != null) {
                            nextPayMap[state.selectedVendorId] = ascii
                        }
                        state.copy(
                            vendorBakiPayAmountInput = ascii,
                            vendorBakiPayById = nextPayMap,
                            errorMessageBn = null
                        )
                    }
                }
                EntryMode.EXPENSE -> {
                    val updatedMap = state.expenseAmountByCategory.toMutableMap()
                    updatedMap[state.selectedCategoryId] = ascii
                    state.copy(
                        expenseAmountByCategory = updatedMap,
                        errorMessageBn = null
                    )
                }
                EntryMode.SALE -> when (state.saleSubMode) {
                    SaleSubMode.QUICK_CASH -> state.copy(
                        saleTotalBillInput = ascii,
                        saleCashReceivedInput = ascii,
                        errorMessageBn = null
                    )
                    SaleSubMode.SHOPKEEPER_SALE -> state.copy(
                        saleTotalBillInput = ascii,
                        errorMessageBn = null
                    )
                    SaleSubMode.BAKI_COLLECT -> {
                        val nextCollectMap = state.shopkeeperCollectAmountById.toMutableMap()
                        if (state.effectiveSelectedShopkeeperIds.size == 1 && state.selectedShopkeeperId != null) {
                            nextCollectMap[state.selectedShopkeeperId] = ascii
                        }
                        state.copy(
                            bakiCollectAmountInput = ascii,
                            shopkeeperCollectAmountById = nextCollectMap,
                            errorMessageBn = null
                        )
                    }
                }
            }
        }
    }

    fun updateCashReceivedAmount(rawInput: String) {
        val ascii = BanglaNumberFormatter.toAsciiDigits(rawInput)
        _formState.update { state ->
            when (state.mode) {
                EntryMode.PURCHASE -> {
                    val nextCashMap = state.vendorCashPaidById.toMutableMap()
                    if (state.effectiveSelectedVendorIds.size == 1 && state.selectedVendorId != null) {
                        nextCashMap[state.selectedVendorId] = ascii
                    }
                    state.copy(
                        purchaseCashPaidInput = ascii,
                        vendorCashPaidById = nextCashMap,
                        errorMessageBn = null
                    )
                }
                else -> state.copy(saleCashReceivedInput = ascii, errorMessageBn = null)
            }
        }
    }

    fun setFullCashPurchase() {
        _formState.update { it.copy(purchaseCashPaidInput = it.purchaseAmountInput, errorMessageBn = null) }
    }

    fun setFullBakiPurchase() {
        _formState.update { it.copy(purchaseCashPaidInput = "0", errorMessageBn = null) }
    }

    fun setFullPreviousVendorBakiPayment(currentVendorDueBdt: Long) {
        if (currentVendorDueBdt > 0L) {
            _formState.update { state ->
                val nextPayMap = state.vendorBakiPayById.toMutableMap()
                state.selectedVendorId?.let { nextPayMap[it] = currentVendorDueBdt.toString() }
                state.copy(
                    vendorBakiPayAmountInput = currentVendorDueBdt.toString(),
                    vendorBakiPayById = nextPayMap,
                    errorMessageBn = null
                )
            }
        }
    }

    fun setFullCashSale() {
        _formState.update { it.copy(saleCashReceivedInput = it.saleTotalBillInput, errorMessageBn = null) }
    }

    fun setFullBakiSale() {
        _formState.update { it.copy(saleCashReceivedInput = "0", errorMessageBn = null) }
    }

    fun setFullPreviousBakiCollection(currentShopkeeperDueBdt: Long) {
        if (currentShopkeeperDueBdt > 0L) {
            _formState.update { state ->
                val nextCollectMap = state.shopkeeperCollectAmountById.toMutableMap()
                state.selectedShopkeeperId?.let { nextCollectMap[it] = currentShopkeeperDueBdt.toString() }
                state.copy(
                    bakiCollectAmountInput = currentShopkeeperDueBdt.toString(),
                    shopkeeperCollectAmountById = nextCollectMap,
                    errorMessageBn = null
                )
            }
        }
    }

    fun updateNote(note: String) {
        _formState.update { state ->
            when (state.mode) {
                EntryMode.PURCHASE -> when (state.purchaseSubMode) {
                    PurchaseSubMode.QUICK_CASH, PurchaseSubMode.VENDOR_BAKI_PURCHASE -> state.copy(purchaseNoteBn = note)
                    PurchaseSubMode.VENDOR_BAKI_PAY -> state.copy(vendorBakiPayNoteBn = note)
                }
                EntryMode.EXPENSE -> {
                    val updatedNotes = state.expenseNoteByCategory.toMutableMap()
                    updatedNotes[state.selectedCategoryId] = note
                    state.copy(expenseNoteByCategory = updatedNotes)
                }
                EntryMode.SALE -> when (state.saleSubMode) {
                    SaleSubMode.QUICK_CASH, SaleSubMode.SHOPKEEPER_SALE -> state.copy(saleNoteBn = note)
                    SaleSubMode.BAKI_COLLECT -> state.copy(bakiCollectNoteBn = note)
                }
            }
        }
    }

    fun createNewVendorInline(nameBn: String, marketBn: String, phone: String, openingBakiInput: String = "") {
        if (nameBn.isBlank()) return
        val openingBdt = BanglaNumberFormatter.parseBdtAmount(openingBakiInput)
        viewModelScope.launch {
            val id = ledgerRepository.createOrUpdateVendor(
                nameBn = nameBn,
                marketLocationBn = marketBn,
                phoneNumber = phone,
                openingBakiBdt = openingBdt
            )
            _formState.update {
                it.copy(
                    selectedVendorId = id,
                    selectedVendorIds = it.effectiveSelectedVendorIds + id
                )
            }
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun payVendorBakiInline(
        vendorId: String,
        amountInput: String,
        noteBn: String,
        onComplete: () -> Unit = {}
    ) {
        val paidBdt = BanglaNumberFormatter.parseBdtAmount(amountInput)
        if (paidBdt <= 0L) return
        viewModelScope.launch {
            ledgerRepository.payVendorBaki(
                vendorId = vendorId,
                paidCashBdt = paidBdt,
                eventDateMillis = BanglaDateUtils.nowMillis(),
                noteBn = noteBn
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
            onComplete()
        }
    }

    fun createNewShopkeeperInline(nameBn: String, shopNameBn: String, marketBn: String, phone: String) {
        if (nameBn.isBlank()) return
        viewModelScope.launch {
            val id = bakiRepository.createOrUpdateShopkeeper(
                nameBn = nameBn,
                shopNameBn = shopNameBn,
                marketAreaBn = marketBn,
                phoneNumber = phone
            )
            _formState.update {
                it.copy(
                    selectedShopkeeperId = id,
                    selectedShopkeeperIds = it.effectiveSelectedShopkeeperIds + id
                )
            }
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun saveTransaction(stayOnScreen: Boolean = false) {
        val current = _formState.value
        val eventMillis = if (current.useYesterdayDate) {
            BanglaDateUtils.localDateToNoonMillis(BanglaDateUtils.todayLocalDate().minusDays(1))
        } else {
            BanglaDateUtils.nowMillis()
        }

        when (current.mode) {
            EntryMode.PURCHASE -> {
                when (current.purchaseSubMode) {
                    PurchaseSubMode.QUICK_CASH -> {
                        val totalBdt = current.parsedTotalBdt
                        if (totalBdt <= 0L) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে সঠিক টাকার পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }
                        viewModelScope.launch {
                            ledgerRepository.recordWholesalePurchase(
                                vendorId = current.selectedVendorId,
                                amountBdt = totalBdt,
                                cashPaidBdt = totalBdt,
                                eventDateMillis = eventMillis,
                                noteBn = current.noteBn.ifBlank { "নগদ পাইকারি মাল কেনা" }
                            )
                            _formState.update {
                                it.copy(
                                    purchaseAmountInput = "",
                                    purchaseCashPaidInput = "",
                                    purchaseNoteBn = "",
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(stayOnScreen, "নগদ কেনা সেভ হয়েছে! পরবর্তী কেনা লিখুন")
                        }
                    }

                    PurchaseSubMode.VENDOR_BAKI_PURCHASE -> {
                        val vendorIds = current.effectiveSelectedVendorIds
                        if (vendorIds.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে তালিকায় থাকা অন্তত একজন মহাজন নির্বাচন করুন অথবা '+ নতুন মহাজন' যোগ করুন") }
                            return
                        }

                        // Build per-vendor (purchaseBdt, cashPaidBdt) entries
                        val entries = vendorIds.map { vid ->
                            val pBdt = current.getVendorPurchaseBdt(vid)
                            val cBdt = current.getVendorCashPaidBdt(vid)
                            Triple(vid, pBdt, cBdt)
                        }.filter { (_, pBdt, cBdt) -> pBdt > 0L || cBdt > 0L }

                        if (entries.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে মহাজনের মাল কেনা বা নগদ পরিশোধের টাকার পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }

                        viewModelScope.launch {
                            for ((vid, pBdt, cBdt) in entries) {
                                ledgerRepository.recordWholesalePurchase(
                                    vendorId = vid,
                                    amountBdt = pBdt,
                                    cashPaidBdt = cBdt,
                                    eventDateMillis = eventMillis,
                                    noteBn = current.noteBn.ifBlank { "মহাজন থেকে বাকি/নগদ কেনা ও পরিশোধ" }
                                )
                            }
                            _formState.update {
                                it.copy(
                                    purchaseAmountInput = "",
                                    purchaseCashPaidInput = "",
                                    purchaseNoteBn = "",
                                    vendorPurchaseAmountById = emptyMap(),
                                    vendorCashPaidById = emptyMap(),
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(
                                stayOnScreen,
                                "${BanglaNumberFormatter.toBanglaDigits(entries.size.toString())} জন মহাজনের মাল কেনা ও পরিশোধ সেভ হয়েছে!"
                            )
                        }
                    }

                    PurchaseSubMode.VENDOR_BAKI_PAY -> {
                        val vendorIds = current.effectiveSelectedVendorIds
                        if (vendorIds.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে তালিকায় থাকা অন্তত একজন মহাজন নির্বাচন করুন") }
                            return
                        }

                        val payEntries = vendorIds.map { vid ->
                            vid to current.getVendorBakiPayBdt(vid)
                        }.filter { it.second > 0L }

                        if (payEntries.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে পরিশোধের টাকার পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }

                        viewModelScope.launch {
                            for ((vid, paidBdt) in payEntries) {
                                ledgerRepository.payVendorBaki(
                                    vendorId = vid,
                                    paidCashBdt = paidBdt,
                                    eventDateMillis = eventMillis,
                                    noteBn = current.noteBn.ifBlank { "মহাজনের বকেয়া পরিশোধ" }
                                )
                            }
                            _formState.update {
                                it.copy(
                                    vendorBakiPayAmountInput = "",
                                    vendorBakiPayNoteBn = "",
                                    vendorBakiPayById = emptyMap(),
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(
                                stayOnScreen,
                                "${BanglaNumberFormatter.toBanglaDigits(payEntries.size.toString())} জন মহাজনের বকেয়া পরিশোধ সেভ হয়েছে!"
                            )
                        }
                    }
                }
            }

            EntryMode.EXPENSE -> {
                val totalBdt = current.parsedTotalBdt
                if (totalBdt <= 0L) {
                    _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে সঠিক খরচের পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                    return
                }
                viewModelScope.launch {
                    ledgerRepository.recordOperatingExpense(
                        categoryId = current.selectedCategoryId,
                        amountBdt = totalBdt,
                        eventDateMillis = eventMillis,
                        noteBn = current.noteBn
                    )
                    _formState.update { state ->
                        val nextAmounts = state.expenseAmountByCategory.toMutableMap().apply {
                            remove(state.selectedCategoryId)
                        }
                        val nextNotes = state.expenseNoteByCategory.toMutableMap().apply {
                            remove(state.selectedCategoryId)
                        }
                        state.copy(
                            expenseAmountByCategory = nextAmounts,
                            expenseNoteByCategory = nextNotes,
                            errorMessageBn = null
                        )
                    }
                    backupWorkScheduler.scheduleDebouncedMutationSync()
                    emitCompletionEffect(stayOnScreen, "খরচের হিসাব সেভ হয়েছে! অন্য খাতের খরচ লিখুন")
                }
            }

            EntryMode.SALE -> {
                when (current.saleSubMode) {
                    SaleSubMode.QUICK_CASH -> {
                        val totalBdt = current.parsedTotalBdt
                        if (totalBdt <= 0L) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে সঠিক বিক্রির পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }
                        viewModelScope.launch {
                            ledgerRepository.recordSaleWithBaki(
                                shopkeeperId = null,
                                totalBillBdt = totalBdt,
                                cashReceivedBdt = totalBdt,
                                eventDateMillis = eventMillis,
                                noteBn = current.noteBn.ifBlank { "নগদ বিক্রি" }
                            )
                            _formState.update {
                                it.copy(
                                    saleTotalBillInput = "",
                                    saleCashReceivedInput = "",
                                    saleNoteBn = "",
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(stayOnScreen, "নগদ বিক্রি সেভ হয়েছে! পরবর্তী বিক্রি লিখুন")
                        }
                    }

                    SaleSubMode.SHOPKEEPER_SALE -> {
                        val shopkeeperIds = current.effectiveSelectedShopkeeperIds
                        if (shopkeeperIds.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে তালিকা থেকে অন্তত একজন দোকানদার নির্বাচন করুন অথবা '+ নতুন দোকানদার' যোগ করুন") }
                            return
                        }

                        val multiBakiEntries = shopkeeperIds.map { sid ->
                            sid to current.getShopkeeperBakiBdt(sid)
                        }.filter { it.second > 0L }

                        val topTotalBdt = current.parsedTotalBdt
                        val topCashBdt = current.parsedCashBdt

                        if (multiBakiEntries.isEmpty() && topTotalBdt <= 0L) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে মোট বিক্রির বিল অথবা নির্বাচিত দোকানদারদের আজকের বাকির পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }

                        viewModelScope.launch {
                            if (multiBakiEntries.isNotEmpty()) {
                                val sumBakiBdt = multiBakiEntries.sumOf { it.second }
                                val remainingCashSaleBdt = if (topTotalBdt > sumBakiBdt) {
                                    topTotalBdt - sumBakiBdt
                                } else {
                                    topCashBdt
                                }

                                // 1. Record each selected Dokandar's Baki portion
                                for ((sid, bakiBdt) in multiBakiEntries) {
                                    ledgerRepository.recordSaleWithBaki(
                                        shopkeeperId = sid,
                                        totalBillBdt = bakiBdt,
                                        cashReceivedBdt = 0L,
                                        eventDateMillis = eventMillis,
                                        noteBn = current.noteBn.ifBlank { "দোকানদারের কাছে বাকি বিক্রি" }
                                    )
                                }

                                // 2. Record the remaining cash portion of Total Selling (if > 0)
                                if (remainingCashSaleBdt > 0L) {
                                    ledgerRepository.recordSaleWithBaki(
                                        shopkeeperId = null,
                                        totalBillBdt = remainingCashSaleBdt,
                                        cashReceivedBdt = remainingCashSaleBdt,
                                        eventDateMillis = eventMillis,
                                        noteBn = current.noteBn.ifBlank { "নগদ বিক্রি (মোট বিক্রির নগদ অংশ)" }
                                    )
                                }
                            } else {
                                // Fallback: Single Dokandar with top Total Bill & Cash Received
                                val primaryShopkeeperId = current.selectedShopkeeperId ?: shopkeeperIds.first()
                                ledgerRepository.recordSaleWithBaki(
                                    shopkeeperId = primaryShopkeeperId,
                                    totalBillBdt = topTotalBdt,
                                    cashReceivedBdt = topCashBdt,
                                    eventDateMillis = eventMillis,
                                    noteBn = current.noteBn.ifBlank { "দোকানদারের কাছে বিক্রি" }
                                )
                            }

                            _formState.update {
                                it.copy(
                                    saleTotalBillInput = "",
                                    saleCashReceivedInput = "",
                                    saleNoteBn = "",
                                    shopkeeperBakiAmountById = emptyMap(),
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(
                                stayOnScreen,
                                "বিক্রি ও ${BanglaNumberFormatter.toBanglaDigits(shopkeeperIds.size.toString())} জন দোকানদারের বাকি সেভ হয়েছে!"
                            )
                        }
                    }

                    SaleSubMode.BAKI_COLLECT -> {
                        val shopkeeperIds = current.effectiveSelectedShopkeeperIds
                        if (shopkeeperIds.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে তালিকা থেকে অন্তত একজন দোকানদার নির্বাচন করুন") }
                            return
                        }

                        val collectEntries = shopkeeperIds.map { sid ->
                            sid to current.getShopkeeperCollectBdt(sid)
                        }.filter { it.second > 0L }

                        if (collectEntries.isEmpty()) {
                            _formState.update { it.copy(errorMessageBn = "অনুগ্রহ করে আদায়কৃত টাকার পরিমাণ লিখুন (৳ ০ এর বেশি)") }
                            return
                        }

                        viewModelScope.launch {
                            for ((sid, collectedBdt) in collectEntries) {
                                bakiRepository.collectShopkeeperBaki(
                                    shopkeeperId = sid,
                                    collectedCashBdt = collectedBdt,
                                    eventDateMillis = eventMillis,
                                    noteBn = current.noteBn.ifBlank { "পুরাতন বাকি আদায়" }
                                )
                            }
                            _formState.update {
                                it.copy(
                                    bakiCollectAmountInput = "",
                                    bakiCollectNoteBn = "",
                                    shopkeeperCollectAmountById = emptyMap(),
                                    errorMessageBn = null
                                )
                            }
                            backupWorkScheduler.scheduleDebouncedMutationSync()
                            emitCompletionEffect(
                                stayOnScreen,
                                "${BanglaNumberFormatter.toBanglaDigits(collectEntries.size.toString())} জন দোকানদার থেকে বাকি আদায় সেভ হয়েছে!"
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun emitCompletionEffect(stayOnScreen: Boolean, messageBn: String) {
        if (stayOnScreen) {
            _effect.send(TransactionUiEffect.ShowMessage(messageBn))
        } else {
            _effect.send(TransactionUiEffect.NavigateBack)
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            ledgerRepository.deleteTransaction(transactionId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun updateTransaction(
        transactionId: String,
        totalBillInput: String,
        cashAmountInput: String,
        noteBn: String
    ) {
        val totalBillBdt = BanglaNumberFormatter.parseBdtAmount(totalBillInput)
        val cashBdt = BanglaNumberFormatter.parseBdtAmount(cashAmountInput)
        if (totalBillBdt <= 0L) return
        viewModelScope.launch {
            ledgerRepository.updateTransaction(
                transactionId = transactionId,
                newTotalBillBdt = totalBillBdt,
                newCashAmountBdt = cashBdt,
                newNoteBn = noteBn
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun updateVendorInline(
        vendorId: String,
        nameBn: String,
        marketLocationBn: String,
        phoneNumber: String,
        currentBakiInput: String
    ) {
        if (nameBn.isBlank()) return
        val updatedBakiBdt = BanglaNumberFormatter.parseBdtAmount(currentBakiInput)
        viewModelScope.launch {
            ledgerRepository.createOrUpdateVendor(
                vendorId = vendorId,
                nameBn = nameBn,
                marketLocationBn = marketLocationBn,
                phoneNumber = phoneNumber,
                updatedCurrentBakiBdt = updatedBakiBdt
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun deleteVendorInline(vendorId: String) {
        viewModelScope.launch {
            ledgerRepository.deleteVendor(vendorId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }
}
