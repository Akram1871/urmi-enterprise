package com.akram.babsharhisab.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.ui.components.CategoryProgressRow
import com.akram.babsharhisab.ui.components.ProfitSummaryHeroCard
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.BakiAmberBgLight
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ExpenseRedBgLight
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.ProfitGreenBgLight
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.theme.PurchaseBlueBgLight
import com.akram.babsharhisab.ui.transaction.DeleteConfirmationDialog
import com.akram.babsharhisab.ui.transaction.EditTransactionDialog
import com.akram.babsharhisab.ui.transaction.TransactionRowCard

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToEntry: (mode: String) -> Unit,
    onNavigateToBakiTab: () -> Unit,
    onNavigateToAllTransactions: () -> Unit,
    onNavigateToBackupSettings: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
    val todayTransactions by viewModel.todayTransactions.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()

    val isToday = selectedDate == BanglaDateUtils.todayLocalDate()
    var editingTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var deletingTransaction by remember { mutableStateOf<TransactionItem?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Header Row: App Title
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ঊর্মি এন্টারপ্রাইজ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Urmi Enterprise • দৈনিক আয়-ব্যয় ও বাকি খাতা",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Date Selector Strip
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goToPreviousDay() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "আগের দিন")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = BanglaDateUtils.formatFullBanglaDate(selectedDate),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isToday) {
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = false,
                                onClick = { viewModel.goToToday() },
                                label = { Text("আজ ফিরুন") }
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.goToNextDay() },
                        enabled = !isToday
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "পরের দিন")
                    }
                }
            }
        }

        // Real-Time Hero P&L Card
        item {
            ProfitSummaryHeroCard(
                summary = summary,
                includeDepreciation = config.includeDepreciationInProfit
            )
        }

        // Quick Action 2x2 Grid (03_UI_Spec.md Section 5.2)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        titleBn = "+ মাল কেনা (মহাজন)",
                        subtitleBn = "নগদ বা বাকি কেনা ও পরিশোধ",
                        icon = Icons.Default.Inventory2,
                        bgColor = PurchaseBlueBgLight,
                        contentColor = PurchaseBlue,
                        onClick = { onNavigateToEntry("PURCHASE") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        titleBn = "+ গাড়ি ও দৈনিক খরচ",
                        subtitleBn = "তেল, ভাড়া, খাবার, মেরামত",
                        icon = Icons.Default.LocalGasStation,
                        bgColor = ExpenseRedBgLight,
                        contentColor = ExpenseRed,
                        onClick = { onNavigateToEntry("EXPENSE") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        titleBn = "+ নগদ ও বাকি বিক্রি",
                        subtitleBn = "নগদ বা একাধিক দোকানে বাকি",
                        icon = Icons.Default.PointOfSale,
                        bgColor = ProfitGreenBgLight,
                        contentColor = ProfitGreen,
                        onClick = { onNavigateToEntry("SALE") },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        titleBn = "+ বাকি টাকা আদায়",
                        subtitleBn = "পাওনা: ${BanglaNumberFormatter.formatCompactTaka(summary.totalActiveMarketBakiBdt)}",
                        icon = Icons.Default.Payments,
                        bgColor = BakiAmberBgLight,
                        contentColor = BakiAmber,
                        onClick = { onNavigateToEntry("BAKI_COLLECT") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Market Dues Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBakiTab() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BakiAmber.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "মার্কেটে দোকানদারদের কাছে মোট পাওনা (বাকি)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(summary.totalActiveMarketBakiBdt),
                            style = MaterialTheme.typography.headlineLarge,
                            color = BakiAmber
                        )
                        Text(
                            text = "${BanglaNumberFormatter.toBanglaDigits(summary.activeBakiShopkeeperCount)} জন দোকানদারের কাছে বাকি আছে • আজ নতুন বাকি: ${BanglaNumberFormatter.formatTaka(summary.newBakiGivenBdt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "খাতা দেখুন >",
                        style = MaterialTheme.typography.labelLarge,
                        color = BakiAmber,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        // Active Mahajan Payable Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToEntry("VENDOR_PAY") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, PurchaseBlue.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "মহাজনদের কাছে মোট বকেয়া দেনা (পরিশোধযোগ্য)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(summary.totalActiveVendorBakiBdt),
                            style = MaterialTheme.typography.headlineLarge,
                            color = PurchaseBlue
                        )
                        Text(
                            text = "${BanglaNumberFormatter.toBanglaDigits(summary.activeBakiVendorCount)} জন মহাজন পাবেন • আজ নতুন বাকি কেনা: ${BanglaNumberFormatter.formatTaka(summary.newVendorBakiBdt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "পরিশোধ >",
                        style = MaterialTheme.typography.labelLarge,
                        color = PurchaseBlue,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        // Personal / Home Expense Tracker Card (Strictly excluded from Profit/Loss)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToEntry("HOME_EXPENSE") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "বাসা ও সংসারের খরচ (লাভ-ক্ষতির বাইরে)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আজ বাসার খরচ: ${BanglaNumberFormatter.formatTaka(summary.personalHomeExpenseBdt)} • এই মাসে মোট: ${BanglaNumberFormatter.formatTaka(summary.monthlyHomeExpenseBdt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "+ বাসার খরচ",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Category-Wise Expense Breakdown Today
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "খাতওয়ারী আজকের খরচের হিসাব",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (categoryBreakdown.isEmpty()) {
                        Text(
                            text = "আজ এখনো কোনো মাল কেনা বা গাড়ির খরচ যোগ করা হয়নি।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        categoryBreakdown.forEach { catItem ->
                            CategoryProgressRow(item = catItem)
                        }
                    }
                }
            }
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "আজকের লেনদেন সমূহ (${BanglaNumberFormatter.toBanglaDigits(todayTransactions.size)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToAllTransactions) {
                    Text("সব দেখুন >")
                }
            }
        }

        if (todayTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "আজ এখনো কোনো লেনদেন লেখা হয়নি। উপরের বাটনে চাপ দিয়ে আজকের মাল কেনা, গাড়ির খরচ বা বিক্রি যোগ করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(todayTransactions, key = { it.transactionId }) { tx ->
                TransactionRowCard(
                    item = tx,
                    onEdit = { editingTransaction = tx },
                    onDelete = { deletingTransaction = tx }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (editingTransaction != null) {
        EditTransactionDialog(
            item = editingTransaction!!,
            onDismiss = { editingTransaction = null },
            onSave = { totalBill, cashAmount, note ->
                viewModel.updateTransaction(
                    transactionId = editingTransaction!!.transactionId,
                    totalBillInput = totalBill,
                    cashAmountInput = cashAmount,
                    noteBn = note
                )
                editingTransaction = null
            }
        )
    }

    if (deletingTransaction != null) {
        DeleteConfirmationDialog(
            titleBn = "লেনদেনটি মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে '${deletingTransaction!!.categoryNameBn}' (${BanglaNumberFormatter.formatTaka(deletingTransaction!!.totalBillBdt)}) এর এই হিসাবটি মুছে ফেলতে চান?",
            onDismiss = { deletingTransaction = null },
            onConfirmDelete = {
                viewModel.deleteTransaction(deletingTransaction!!.transactionId)
                deletingTransaction = null
            }
        )
    }
}

@Composable
private fun QuickActionTile(
    titleBn: String,
    subtitleBn: String,
    icon: ImageVector,
    bgColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = titleBn,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = subtitleBn,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}
