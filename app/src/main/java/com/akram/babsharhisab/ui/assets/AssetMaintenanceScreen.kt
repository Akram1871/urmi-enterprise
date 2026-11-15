package com.akram.babsharhisab.ui.assets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.worker.BackupWorkScheduler
import com.akram.babsharhisab.domain.model.AssetItem
import com.akram.babsharhisab.domain.repository.AssetRepository
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.CatAssetColor
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.transaction.DeleteConfirmationDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val backupWorkScheduler: BackupWorkScheduler
) : ViewModel() {

    val activeAssets: StateFlow<List<AssetItem>> = assetRepository.observeActiveAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDailyDepreciation: StateFlow<Long> = assetRepository.observeTotalDailyDepreciation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalMonthlyDepreciation: StateFlow<Long> = assetRepository.observeTotalMonthlyDepreciation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun saveAsset(
        assetId: String? = null,
        nameBn: String,
        costInput: String,
        lifespanMonthsInput: String,
        notesBn: String,
        recordCashOutflowToday: Boolean
    ) {
        val costBdt = BanglaNumberFormatter.parseBdtAmount(costInput)
        val months = BanglaNumberFormatter.parseBdtAmount(lifespanMonthsInput).toInt().coerceAtLeast(1)
        if (nameBn.isBlank() || costBdt <= 0L) return

        viewModelScope.launch {
            assetRepository.saveOrReplaceAsset(
                assetId = assetId,
                assetNameBn = nameBn,
                purchaseCostBdt = costBdt,
                lifespanMonths = months,
                purchaseDateMillis = BanglaDateUtils.nowMillis(),
                notesBn = notesBn,
                recordAsTodayCashExpense = recordCashOutflowToday
            )
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }

    fun deleteAsset(assetId: String) {
        viewModelScope.launch {
            assetRepository.archiveAsset(assetId)
            backupWorkScheduler.scheduleDebouncedMutationSync()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetMaintenanceScreen(
    viewModel: AssetViewModel,
    onNavigateBack: () -> Unit
) {
    val assets by viewModel.activeAssets.collectAsStateWithLifecycle()
    val dailyDep by viewModel.totalDailyDepreciation.collectAsStateWithLifecycle()
    val monthlyDep by viewModel.totalMonthlyDepreciation.collectAsStateWithLifecycle()

    var showAddOrReplaceDialog by remember { mutableStateOf(false) }
    var replacingAsset by remember { mutableStateOf<AssetItem?>(null) }
    var deletingAsset by remember { mutableStateOf<AssetItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("গাড়ির ব্যাটারি ও দীর্ঘমেয়াদী সম্পদ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    replacingAsset = null
                    showAddOrReplaceDialog = true
                },
                containerColor = CatAssetColor
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "নতুন ব্যাটারি/সম্পদ যোগ",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Depreciation Reserve Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CatAssetColor.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ভবিষ্যৎ ব্যাটারি ও যন্ত্রাংশ পরিবর্তনের সঞ্চয় লক্ষ্য",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CatAssetColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("দৈনিক অবচয় সঞ্চয়:", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${BanglaNumberFormatter.formatTaka(dailyDep)} / দিন",
                                style = MaterialTheme.typography.headlineLarge,
                                color = CatAssetColor
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("মাসিক অবচয় সঞ্চয়:", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${BanglaNumberFormatter.formatTaka(monthlyDep)} / মাস",
                                style = MaterialTheme.typography.titleLarge,
                                color = CatAssetColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (assets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "যেমন: ২ বছর (২৪ মাস) মেয়াদী গাড়ির ব্যাটারি বা টায়ার যোগ করুন যাতে প্রতিদিনের প্রকৃত খরচ ও পরবর্তী পরিবর্তনের তারিখ জানা যায়।",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(assets, key = { it.assetId }) { asset ->
                        AssetHealthCard(
                            asset = asset,
                            onReplaceClick = {
                                replacingAsset = asset
                                showAddOrReplaceDialog = true
                            },
                            onDeleteClick = { deletingAsset = asset }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (deletingAsset != null) {
        DeleteConfirmationDialog(
            titleBn = "ব্যাটারি / সম্পদ মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে '${deletingAsset!!.assetNameBn}' (${BanglaNumberFormatter.formatTaka(deletingAsset!!.purchaseCostBdt)}) তালিকা থেকে মুছে ফেলতে চান?",
            onDismiss = { deletingAsset = null },
            onConfirmDelete = {
                viewModel.deleteAsset(deletingAsset!!.assetId)
                deletingAsset = null
            }
        )
    }

    if (showAddOrReplaceDialog) {
        AssetFormDialog(
            existing = replacingAsset,
            onDismiss = { showAddOrReplaceDialog = false },
            onSave = { name, cost, months, notes, recordTodayCash ->
                viewModel.saveAsset(
                    assetId = replacingAsset?.assetId,
                    nameBn = name,
                    costInput = cost,
                    lifespanMonthsInput = months,
                    notesBn = notes,
                    recordCashOutflowToday = recordTodayCash
                )
                showAddOrReplaceDialog = false
            }
        )
    }
}

@Composable
fun AssetHealthCard(
    asset: AssetItem,
    onReplaceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = asset.elapsedFraction.coerceIn(0f, 1f)
    val statusColor = when {
        asset.elapsedFraction >= 1.0f -> ExpenseRed
        asset.elapsedFraction >= 0.75f -> BakiAmber
        else -> ProfitGreen
    }
    val statusLabel = when {
        asset.elapsedFraction >= 1.0f -> "মেয়াদ শেষ — পরিবর্তনের সময় হয়েছে!"
        asset.elapsedFraction >= 0.75f -> "শীঘ্রই পরিবর্তনের সময় হবে (${BanglaNumberFormatter.toBanglaDigits(asset.remainingDays)} দিন বাকি)"
        else -> "সচল আছে (${BanglaNumberFormatter.toBanglaDigits(asset.remainingDays)} দিন বাকি)"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = null,
                        tint = CatAssetColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = asset.assetNameBn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "মেয়াদ: ${BanglaNumberFormatter.toBanglaDigits(asset.lifespanMonths)} মাস | মূল্য: ${BanglaNumberFormatter.formatTaka(asset.purchaseCostBdt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "কেনার তারিখ: ${BanglaDateUtils.formatShortBanglaDate(asset.purchaseDateMillis)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "পরবর্তী পরিবর্তন: ${BanglaDateUtils.formatShortBanglaDate(asset.nextDueDateMillis)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "দৈনিক জমা: ${BanglaNumberFormatter.formatTaka(asset.dailyDepreciationBdt)} | মাসিক: ${BanglaNumberFormatter.formatTaka(asset.monthlyDepreciationBdt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = onReplaceClick) {
                    Icon(Icons.Default.Autorenew, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন পরিবর্তন")
                }
            }
        }
    }
}

@Composable
private fun AssetFormDialog(
    existing: AssetItem?,
    onDismiss: () -> Unit,
    onSave: (nameBn: String, cost: String, months: String, notes: String, recordTodayCash: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(existing?.assetNameBn ?: "গাড়ির ব্যাটারি সেট") }
    var cost by remember { mutableStateOf(existing?.purchaseCostBdt?.toString() ?: "") }
    var months by remember { mutableStateOf(existing?.lifespanMonths?.toString() ?: "24") }
    var notes by remember { mutableStateOf(existing?.notesBn ?: "") }
    var recordTodayCash by remember { mutableStateOf(existing != null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existing == null) "দীর্ঘমেয়াদী সম্পদ / ব্যাটারি যোগ করুন" else "নতুন ব্যাটারি/সম্পদ পরিবর্তন রেকর্ড")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("সম্পদের নাম (যেমন: গাড়ির ব্যাটারি / টায়ার) *") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("মোট ক্রয়/পরিবর্তন খরচ (৳) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = months,
                    onValueChange = { months = it },
                    label = { Text("মেয়াদ কত মাস? (যেমন: ২ বছর = ২৪ মাস) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ওয়ারেন্টি বা ব্র্যান্ড নোট (ঐচ্ছিক)") },
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = recordTodayCash,
                        onCheckedChange = { recordTodayCash = it }
                    )
                    Text(
                        text = "আজকের নগদ ক্যাশ খরচ থেকে পুরো টাকা বাদ দিন",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && BanglaNumberFormatter.parseBdtAmount(cost) > 0L) {
                        onSave(name, cost, months, notes, recordTodayCash)
                    }
                }
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
