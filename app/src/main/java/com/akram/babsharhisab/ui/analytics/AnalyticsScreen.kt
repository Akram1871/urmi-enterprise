package com.akram.babsharhisab.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.CategoryTrendPoint
import com.akram.babsharhisab.domain.model.MonthlyBarPoint
import com.akram.babsharhisab.domain.repository.LedgerRepository
import com.akram.babsharhisab.ui.components.CategoryBreakdownDonutChart
import com.akram.babsharhisab.ui.components.CategoryProgressRow
import com.akram.babsharhisab.ui.components.CategoryTrendLineChart
import com.akram.babsharhisab.ui.components.IncomeExpenseBarChart
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.parseHexColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class AnalyticsPeriod(val monthsCount: Int, val labelBn: String) {
    THIS_MONTH(1, "এই মাস"),
    LAST_3_MONTHS(3, "গত ৩ মাস"),
    LAST_6_MONTHS(6, "গত ৬ মাস"),
    YEARLY(12, "গত ১২ মাস")
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(AnalyticsPeriod.LAST_6_MONTHS)
    val selectedPeriod: StateFlow<AnalyticsPeriod> = _selectedPeriod

    private val _selectedTrendCategoryId = MutableStateFlow(CategoryEntity.CAT_FUEL)
    val selectedTrendCategoryId: StateFlow<String> = _selectedTrendCategoryId

    val categories: StateFlow<List<CategoryEntity>> = ledgerRepository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryEntity.defaultSystemCategories())

    val monthlyBarPoints: StateFlow<List<MonthlyBarPoint>> = _selectedPeriod
        .flatMapLatest { period ->
            val ymKeys = BanglaDateUtils.getRecentYearMonthKeys(period.monthsCount.coerceAtLeast(3))
            ledgerRepository.observeMonthlyIncomeExpense(ymKeys)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val periodCategoryBreakdown: StateFlow<List<CategorySpendItem>> = _selectedPeriod
        .flatMapLatest { period ->
            val ymKeys = BanglaDateUtils.getRecentYearMonthKeys(period.monthsCount)
            ledgerRepository.observePeriodCategoryBreakdown(ymKeys)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryTrendPoints: StateFlow<List<CategoryTrendPoint>> = combine(
        _selectedPeriod,
        _selectedTrendCategoryId
    ) { period, catId ->
        BanglaDateUtils.getRecentYearMonthKeys(period.monthsCount.coerceAtLeast(6)) to catId
    }.flatMapLatest { (ymKeys, catId) ->
        ledgerRepository.observeCategoryTrend(ymKeys, catId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPeriod(period: AnalyticsPeriod) {
        _selectedPeriod.value = period
    }

    fun selectTrendCategory(categoryId: String) {
        _selectedTrendCategoryId.value = categoryId
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val monthlyPoints by viewModel.monthlyBarPoints.collectAsStateWithLifecycle()
    val categoryBreakdown by viewModel.periodCategoryBreakdown.collectAsStateWithLifecycle()
    val trendPoints by viewModel.categoryTrendPoints.collectAsStateWithLifecycle()
    val selectedTrendCategoryId by viewModel.selectedTrendCategoryId.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val periodSalesTotal = monthlyPoints.takeLast(selectedPeriod.monthsCount).sumOf { it.totalSalesBdt }
    val periodExpenseTotal = monthlyPoints.takeLast(selectedPeriod.monthsCount).sumOf { it.totalExpenseBdt }
    val periodNetProfit = periodSalesTotal - periodExpenseTotal
    val periodHomeExpenseTotal = monthlyPoints.takeLast(selectedPeriod.monthsCount).sumOf { it.personalHomeExpenseBdt }

    val expenseCategories = categories.filter {
        it.type == "EXPENSE" || it.type == "PURCHASE" || it.categoryId == CategoryEntity.CAT_ASSET || it.categoryId == CategoryEntity.CAT_HOME_EXPENSE
    }
    val selectedTrendCategory = expenseCategories.find { it.categoryId == selectedTrendCategoryId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("মাসিক ও দীর্ঘমেয়াদী আর্থিক বিশ্লেষণ") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Period Filter Row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AnalyticsPeriod.entries) { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { viewModel.selectPeriod(period) },
                            label = { Text(period.labelBn) }
                        )
                    }
                }
            }

            // KPI Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${selectedPeriod.labelBn} এর সারসংক্ষেপ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "মোট বিক্রি (আয়)",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                                Text(
                                    text = BanglaNumberFormatter.formatTaka(periodSalesTotal),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ProfitGreen,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "মোট খরচ",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                                Text(
                                    text = BanglaNumberFormatter.formatTaka(periodExpenseTotal),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ExpenseRed,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নিট লাভ/ক্ষতি",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                                Text(
                                    text = BanglaNumberFormatter.formatTaka(periodNetProfit, showSign = true),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (periodNetProfit >= 0) ProfitGreen else ExpenseRed,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "বাসা ও সংসারের খরচ (লাভ-ক্ষতির বাইরে):",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = BanglaNumberFormatter.formatTaka(periodHomeExpenseTotal),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Chart 1: Monthly Income vs Expense Grouped Bar Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "মাসিক আয় বনাম ব্যয় তুলনা (সবুজ = আয়, লাল = ব্যয়)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        IncomeExpenseBarChart(points = monthlyPoints)
                    }
                }
            }

            // Chart 2: Category Expense Breakdown Donut + Progress Bars
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "খাতওয়ারী খরচের অনুপাত (${selectedPeriod.labelBn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryBreakdownDonutChart(items = categoryBreakdown)
                        Spacer(modifier = Modifier.height(8.dp))
                        categoryBreakdown.forEach { item ->
                            CategoryProgressRow(item = item)
                        }
                    }
                }
            }

            // Chart 3: Category Trend Line Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "নির্দিষ্ট খরচের মাসিক প্রবণতা (Category Trend Line)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "গাড়ির তেল, মেরামত বা মাল কেনার খরচ মাসে মাসে বাড়ছে কি না দেখুন:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(expenseCategories, key = { it.categoryId }) { cat ->
                                FilterChip(
                                    selected = selectedTrendCategoryId == cat.categoryId,
                                    onClick = { viewModel.selectTrendCategory(cat.categoryId) },
                                    label = { Text(cat.nameBn) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryTrendLineChart(
                            points = trendPoints,
                            lineColor = parseHexColor(selectedTrendCategory?.colorHex ?: "#EA580C")
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
