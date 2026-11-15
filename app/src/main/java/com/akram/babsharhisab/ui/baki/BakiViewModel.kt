package com.akram.babsharhisab.ui.baki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.worker.BackupWorkScheduler
import com.akram.babsharhisab.domain.model.ShopkeeperAccount
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.model.VendorProfile
import com.akram.babsharhisab.domain.repository.BakiRepository
import com.akram.babsharhisab.domain.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BakiTabMode(val labelBn: String) {
    SHOPKEEPER_RECEIVABLE("দোকানদারের বাকি"),
    VENDOR_PAYABLE("মহাজনের বাকি (দেনা)")
}

enum class BakiFilter(val labelBn: String) {
    ALL("সব তালিকা"),
    ONLY_DUE("শুধু বাকি"),
    HIGH_DUE("৳১,০০০+ বাকি")
}

@HiltViewModel
class BakiViewModel @Inject constructor(
    private val bakiRepository: BakiRepository,
    private val ledgerRepository: LedgerRepository,
    private val backupWorkScheduler: BackupWorkScheduler
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(BakiTabMode.SHOPKEEPER_RECEIVABLE)
    val selectedTab: StateFlow<BakiTabMode> = _selectedTab

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow(BakiFilter.ALL)
    val selectedFilter: StateFlow<BakiFilter> = _selectedFilter

    val totalMarketBaki: StateFlow<Long> = bakiRepository.observeTotalMarketBaki()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val activeBakiCount: StateFlow<Int> = bakiRepository.observeActiveBakiShopkeeperCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalVendorBaki: StateFlow<Long> = ledgerRepository.observeTotalVendorBaki()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val activeVendorBakiCount: StateFlow<Int> = ledgerRepository.observeActiveBakiVendorCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredShopkeepers: StateFlow<List<ShopkeeperAccount>> = combine(
        bakiRepository.observeShopkeepers(),
        _searchQuery,
        _selectedFilter
    ) { list, query, filter ->
        val q = query.trim().lowercase()
        list.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.nameBn.lowercase().contains(q) ||
                item.shopNameBn.lowercase().contains(q) ||
                item.marketAreaBn.lowercase().contains(q) ||
                item.phoneNumber.contains(q)

            val matchesFilter = when (filter) {
                BakiFilter.ALL -> true
                BakiFilter.ONLY_DUE -> item.currentBakiBdt > 0L
                BakiFilter.HIGH_DUE -> item.currentBakiBdt >= 1_000L
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredVendors: StateFlow<List<VendorProfile>> = combine(
        ledgerRepository.observeVendors(),
        _searchQuery,
        _selectedFilter
    ) { list, query, filter ->
        val q = query.trim().lowercase()
        list.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.nameBn.lowercase().contains(q) ||
                item.marketLocationBn.lowercase().contains(q) ||
                item.phoneNumber.contains(q)

            val matchesFilter = when (filter) {
                BakiFilter.ALL -> true
                BakiFilter.ONLY_DUE -> item.currentBakiBdt > 0L
                BakiFilter.HIGH_DUE -> item.currentBakiBdt >= 1_000L
            }
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: BakiTabMode) {
        _selectedTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateFilter(filter: BakiFilter) {
        _selectedFilter.value = filter
    }

    fun observeShopkeeperDetail(shopkeeperId: String): Flow<ShopkeeperAccount?> =
        bakiRepository.observeShopkeeperById(shopkeeperId)

    fun observeShopkeeperLedger(shopkeeperId: String): Flow<List<TransactionItem>> =
        bakiRepository.observeShopkeeperLedger(shopkeeperId)

    fun addShopkeeper(
        nameBn: String,
        shopNameBn: String,
        marketAreaBn: String,
        phoneNumber: String,
        openingBakiInput: String
    ) {
        if (nameBn.isBlank()) return
        val openingBdt = BanglaNumberFormatter.parseBdtAmount(openingBakiInput)
        viewModelScope.launch {
            bakiRepository.createOrUpdateShopkeeper(
                nameBn = nameBn,
                shopNameBn = shopNameBn,
                marketAreaBn = marketAreaBn,
                phoneNumber = phoneNumber,
                openingBakiBdt = openingBdt
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun collectBaki(
        shopkeeperId: String,
        amountInput: String,
        noteBn: String,
        onComplete: () -> Unit = {}
    ) {
        val collectedBdt = BanglaNumberFormatter.parseBdtAmount(amountInput)
        if (collectedBdt <= 0L) return
        viewModelScope.launch {
            bakiRepository.collectShopkeeperBaki(
                shopkeeperId = shopkeeperId,
                collectedCashBdt = collectedBdt,
                eventDateMillis = BanglaDateUtils.nowMillis(),
                noteBn = noteBn
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
            onComplete()
        }
    }

    fun addVendor(
        nameBn: String,
        marketLocationBn: String,
        phoneNumber: String,
        openingBakiInput: String = ""
    ) {
        if (nameBn.isBlank()) return
        val openingBdt = BanglaNumberFormatter.parseBdtAmount(openingBakiInput)
        viewModelScope.launch {
            ledgerRepository.createOrUpdateVendor(
                nameBn = nameBn,
                marketLocationBn = marketLocationBn,
                phoneNumber = phoneNumber,
                openingBakiBdt = openingBdt
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun payVendorBaki(
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

    fun updateShopkeeper(
        shopkeeperId: String,
        nameBn: String,
        shopNameBn: String,
        marketAreaBn: String,
        phoneNumber: String,
        currentBakiInput: String
    ) {
        if (nameBn.isBlank()) return
        val updatedBakiBdt = BanglaNumberFormatter.parseBdtAmount(currentBakiInput)
        viewModelScope.launch {
            bakiRepository.createOrUpdateShopkeeper(
                shopkeeperId = shopkeeperId,
                nameBn = nameBn,
                shopNameBn = shopNameBn,
                marketAreaBn = marketAreaBn,
                phoneNumber = phoneNumber,
                updatedCurrentBakiBdt = updatedBakiBdt
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun deleteShopkeeper(
        shopkeeperId: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            bakiRepository.deleteShopkeeper(shopkeeperId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
            onComplete()
        }
    }

    fun updateVendor(
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

    fun deleteVendor(
        vendorId: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            ledgerRepository.deleteVendor(vendorId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
            onComplete()
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

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            ledgerRepository.deleteTransaction(transactionId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }
}
