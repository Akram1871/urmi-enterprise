package com.akram.babsharhisab.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.security.BackupConfigState
import com.akram.babsharhisab.core.security.SecureConfigStore
import com.akram.babsharhisab.data.worker.BackupWorkScheduler
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.DailySummary
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.repository.BackupSyncRepository
import com.akram.babsharhisab.domain.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val backupSyncRepository: BackupSyncRepository,
    private val secureConfigStore: SecureConfigStore,
    private val backupWorkScheduler: BackupWorkScheduler
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(BanglaDateUtils.todayLocalDate())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val configState: StateFlow<BackupConfigState> = secureConfigStore.configFlow

    val pendingSyncCount: StateFlow<Int> = backupSyncRepository.observePendingSyncCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dailySummary: StateFlow<DailySummary> = _selectedDate
        .flatMapLatest { date ->
            val key = BanglaDateUtils.toDateKey(date)
            ledgerRepository.observeDailySummary(key)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailySummary(dateKey = BanglaDateUtils.toDateKey(BanglaDateUtils.todayLocalDate()))
        )

    val categoryBreakdown: StateFlow<List<CategorySpendItem>> = _selectedDate
        .flatMapLatest { date ->
            ledgerRepository.observeDailyExpenseBreakdown(BanglaDateUtils.toDateKey(date))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTransactions: StateFlow<List<TransactionItem>> = _selectedDate
        .flatMapLatest { date ->
            ledgerRepository.observeTransactionsByDate(BanglaDateUtils.toDateKey(date))
        }
        .map { it.take(15) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun goToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun goToNextDay() {
        val next = _selectedDate.value.plusDays(1)
        if (!next.isAfter(BanglaDateUtils.todayLocalDate())) {
            _selectedDate.value = next
        }
    }

    fun goToToday() {
        _selectedDate.value = BanglaDateUtils.todayLocalDate()
    }

    fun toggleDepreciationInProfit(include: Boolean) {
        secureConfigStore.setIncludeDepreciationInProfit(include)
    }

    fun triggerInstantBackup() {
        backupWorkScheduler.triggerManualSyncNow()
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
