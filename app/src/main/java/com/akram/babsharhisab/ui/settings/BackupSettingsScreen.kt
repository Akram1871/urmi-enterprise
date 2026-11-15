package com.akram.babsharhisab.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.core.security.BackupConfigState
import com.akram.babsharhisab.core.security.SecureConfigStore
import com.akram.babsharhisab.data.local.entity.SyncLogEntity
import com.akram.babsharhisab.data.worker.BackupWorkScheduler
import com.akram.babsharhisab.domain.repository.BackupSyncRepository
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val backupSyncRepository: BackupSyncRepository,
    private val secureConfigStore: SecureConfigStore,
    private val backupWorkScheduler: BackupWorkScheduler
) : ViewModel() {

    val configState: StateFlow<BackupConfigState> = secureConfigStore.configFlow

    val pendingSyncCount: StateFlow<Int> = backupSyncRepository.observePendingSyncCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val syncLogs: StateFlow<List<SyncLogEntity>> = backupSyncRepository.observeRecentSyncLogs(20)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSyncingNow = MutableStateFlow(false)
    val isSyncingNow: StateFlow<Boolean> = _isSyncingNow

    fun saveBackupConfiguration(
        webAppUrl: String,
        secretToken: String,
        autoBackupEnabled: Boolean,
        onSaved: () -> Unit
    ) {
        secureConfigStore.updateBackupCredentials(webAppUrl, secretToken, autoBackupEnabled)
        if (autoBackupEnabled) {
            backupWorkScheduler.scheduleDailyPeriodicBackup()
        }
        onSaved()
    }

    fun toggleDepreciationInProfit(include: Boolean) {
        secureConfigStore.setIncludeDepreciationInProfit(include)
    }

    fun toggleClassicFont(useClassic: Boolean) {
        secureConfigStore.setUseClassicFont(useClassic)
    }

    fun syncNowDirect(onResultMessage: (String) -> Unit) {
        if (_isSyncingNow.value) return
        _isSyncingNow.value = true
        viewModelScope.launch {
            val res = backupSyncRepository.performGoogleSheetsSync()
            _isSyncingNow.value = false
            res.fold(
                onSuccess = { count ->
                    onResultMessage("সফলভাবে গুগল শিটে ব্যাকআপ সম্পন্ন (${BanglaNumberFormatter.toBanglaDigits(count)}টি রেকর্ড)")
                },
                onFailure = { err ->
                    onResultMessage("ব্যাকআপ ব্যর্থ: ${err.localizedMessage ?: "নেটওয়ার্ক বা লিংক পরীক্ষা করুন"}")
                }
            )
        }
    }

    fun restoreNowDirect(onResultMessage: (String) -> Unit) {
        if (_isSyncingNow.value) return
        _isSyncingNow.value = true
        viewModelScope.launch {
            val res = backupSyncRepository.restoreFromGoogleSheets()
            _isSyncingNow.value = false
            res.fold(
                onSuccess = { count ->
                    onResultMessage("সফলভাবে গুগল শিট থেকে ${BanglaNumberFormatter.toBanglaDigits(count)}টি রেকর্ড এই ফোনে রিস্টোর হয়েছে!")
                },
                onFailure = { err ->
                    onResultMessage("রিস্টোর ব্যর্থ: ${err.localizedMessage ?: "নেটওয়ার্ক বা গুগল শিট স্ক্রিপ্ট পরীক্ষা করুন"}")
                }
            )
        }
    }

    suspend fun getCsvContent(): String = backupSyncRepository.generateCsvExportContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsScreen(
    viewModel: BackupSettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val config by viewModel.configState.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncingNow.collectAsStateWithLifecycle()

    var urlInput by remember(config.webAppUrl) { mutableStateOf(config.webAppUrl) }
    var secretInput by remember(config.secretToken) { mutableStateOf(config.secretToken) }
    var autoBackup by remember(config.autoBackupEnabled) { mutableStateOf(config.autoBackupEnabled) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val csv = viewModel.getCsvContent()
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(csv.toByteArray(Charsets.UTF_8))
                }
                snackbarHostState.showSnackbar("সিএসভি (CSV) ফাইল সফলভাবে ফোনে সেভ হয়েছে")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("গুগল শিট স্বয়ংক্রিয় ব্যাকআপ ও সেটিংস") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
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
            // Status Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ব্যাকআপের বর্তমান অবস্থা",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ব্যাকআপের অপেক্ষায় থাকা লেনদেন: ${BanglaNumberFormatter.toBanglaDigits(pendingSyncCount)} টি",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (pendingSyncCount == 0) ProfitGreen else BakiAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveBackupConfiguration(urlInput, secretInput, autoBackup) {}
                                    viewModel.syncNowDirect { msg ->
                                        scope.launch { snackbarHostState.showSnackbar(msg) }
                                    }
                                },
                                enabled = !isSyncing,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isSyncing) "ব্যাকআপ হচ্ছে..." else "এখনই ব্যাকআপ নিন")
                            }
                            OutlinedButton(
                                onClick = {
                                    val todayStr = BanglaDateUtils.toDateKey(BanglaDateUtils.nowMillis())
                                    csvExportLauncher.launch("babshar_hisab_$todayStr.csv")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("এক্সেল/CSV সেভ")
                            }
                        }

                        // Pull / Restore Data into New Phone Button
                        Button(
                            onClick = {
                                viewModel.saveBackupConfiguration(urlInput, secretInput, autoBackup) {}
                                showRestoreConfirmDialog = true
                            },
                            enabled = !isSyncing,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSyncing) "সিঙ্ক হচ্ছে..." else "নতুন ফোনে গুগল শিট থেকে সব ডাটা আনুন (Restore)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Webhook URL Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "গুগল শিট ওয়েব অ্যাপ সংযোগ (WorkManager Auto-Sync)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("Google Apps Script Web App URL (https://script.google.com/...)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = secretInput,
                            onValueChange = { secretInput = it },
                            label = { Text("সুরক্ষা টোকেন (Secret Key)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "প্রতিদিন স্বয়ংক্রিয় ব্যাকগ্রাউন্ড ব্যাকআপ (WorkManager)",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = autoBackup,
                                onCheckedChange = { autoBackup = it }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    copyAppsScriptTemplateToClipboard(context)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("গুগল শিট Apps Script কোড কপি হয়েছে!")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("শিট স্ক্রিপ্ট কপি করুন")
                            }
                            Button(
                                onClick = {
                                    viewModel.saveBackupConfiguration(urlInput, secretInput, autoBackup) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("ব্যাকআপ সেটিংস সংরক্ষিত হয়েছে")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("সেটিংস সেভ করুন")
                            }
                        }
                    }
                }
            }

            // Accounting & Display Preferences
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "হিসাব ও ফন্ট পছন্দসমূহ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "নিট লাভে দৈনিক ব্যাটারি/সম্পদ অবচয় সঞ্চয় অন্তর্ভুক্ত করুন",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "চালু থাকলে প্রতিদিনের লাভ থেকে ব্যাটারি পরিবর্তনের দৈনিক সঞ্চয় বাদ দিয়ে প্রকৃত লাভ দেখাবে",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = config.includeDepreciationInProfit,
                                onCheckedChange = { viewModel.toggleDepreciationInProfit(it) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ক্লাসিক ছাপা খাতার ফন্ট স্টাইল (SolaimanLipi Serif)",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = config.useClassicFont,
                                onCheckedChange = { viewModel.toggleClassicFont(it) }
                            )
                        }
                    }
                }
            }

            // Recent Sync Audit Logs
            item {
                Text(
                    text = "সাম্প্রতিক ব্যাকআপ লগ (${BanglaNumberFormatter.toBanglaDigits(syncLogs.size)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(syncLogs, key = { it.logId }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (log.status == "SUCCESS") "সফল" else "ব্যর্থ",
                                color = if (log.status == "SUCCESS") ProfitGreen else ExpenseRed,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = BanglaDateUtils.formatBanglaDateTime(log.executedAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = log.messageBn,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = { Text("গুগল শিট থেকে ডাটা রিস্টোর (নতুন ফোন)") },
            text = {
                Text(
                    "আপনি কি আপনার গুগল শিট থেকে সকল লেনদেন, দোকানদারের বাকি, মহাজনের দেনা এবং ব্যাটারি/সম্পদের তথ্য এই ফোনে ডাউনলোড করতে চান?\n\n(ফোন পরিবর্তন করলে এই অপশনটির মাধ্যমে আপনার পুরাতন সব হিসাব হুবহু চলে আসবে।)"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        viewModel.restoreNowDirect { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue)
                ) {
                    Text("হ্যাঁ, সব ডাটা ফোনে আনুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

private fun copyAppsScriptTemplateToClipboard(context: Context) {
    val scriptSnippet = """
function doGet(e) {
  return pullAllDataFromSheets(SpreadsheetApp.getActiveSpreadsheet());
}

function doPost(e) {
  var lock = LockService.getScriptLock();
  lock.tryLock(25000);
  try {
    var payload = JSON.parse(e.postData.contents);
    var ss = SpreadsheetApp.getActiveSpreadsheet();
    if (payload.action === "PULL_ALL") {
      return pullAllDataFromSheets(ss);
    }
    var syncedCount = 0;
    if (payload.dailySummary) {
      var summarySheet = getOrCreateSheet(ss, "Daily_Summary", ["তারিখ (Date)","মোট বিক্রি (Total Sales ৳)","নগদ বিক্রি (Cash Sales ৳)","নতুন বাকি (New Baki ৳)","বাকি আদায় (Baki Collected ৳)","মাল কেনা (Wholesale Purchase ৳)","গাড়ি ও দৈনিক খরচ (Operating Expense ৳)","সম্পদ/ব্যাটারি খরচ (Asset Maintenance ৳)","দৈনিক অবচয় সঞ্চয় (Daily Depreciation ৳)","নিট লাভ/ক্ষতি (Operational Profit ৳)","অবচয় বাদে প্রকৃত লাভ (Adjusted Profit ৳)","হাতে নগদ ক্যাশ প্রবাহ (Net Cash Flow ৳)","মার্কেটে মোট বকেয়া (Total Active Market Baki ৳)","মহাজনের মোট বকেয়া দেনা (Total Vendor Baki ৳)","বাসা ও সংসারের খরচ (Home Expense ৳)","সর্বশেষ আপডেট (Last Synced)"]);
      var ds = payload.dailySummary;
      upsertRowByKey(summarySheet, 1, ds.dateKey, [ds.dateKey, ds.totalSalesBdt, ds.cashSalesBdt, ds.newBakiGivenBdt, ds.bakiCollectedBdt, ds.wholesalePurchaseBdt, ds.operatingExpenseBdt, ds.assetMaintenanceBdt, ds.dailyDepreciationBdt, ds.netOperationalProfitBdt, ds.adjustedNetProfitBdt, ds.netCashFlowBdt, ds.totalActiveMarketBakiBdt, ds.totalActiveVendorBakiBdt || 0, ds.personalHomeExpenseBdt || 0, new Date().toLocaleString("bn-BD", { timeZone: "Asia/Dhaka" })]);
      syncedCount++;
    }
    if (payload.transactions && payload.transactions.length > 0) {
      var txSheet = getOrCreateSheet(ss, "Transactions", ["Transaction_UUID","তারিখ (Date)","ধরন (Type)","খাত (Category)","মহাজন (Vendor)","দোকানদার (Shopkeeper)","মোট বিল (Total Bill ৳)","নগদ লেনদেন (Cash ৳)","বাকি পরিবর্তন (Baki Delta ৳)","দোকানদারের ব্যালেন্স (Running Baki ৳)","নোট (Note)","সংস্করণ (Version)","অবস্থা (Status)","Vendor_UUID","Shopkeeper_UUID","Asset_UUID"]);
      for (var i = 0; i < payload.transactions.length; i++) {
        var tx = payload.transactions[i];
        upsertRowByKey(txSheet, 1, tx.transactionId, [tx.transactionId, tx.dateKey, tx.txType, tx.categoryId, tx.vendorNameBn || "", tx.shopkeeperNameBn || "", tx.totalBillBdt, tx.cashAmountBdt, tx.bakiDeltaBdt, tx.runningShopkeeperBakiBdt, tx.noteBn || "", tx.version, tx.isDeleted ? "DELETED" : "ACTIVE", tx.vendorId || "", tx.shopkeeperId || "", tx.assetId || ""]);
        syncedCount++;
      }
    }
    if (payload.shopkeepers && payload.shopkeepers.length > 0) {
      var shopSheet = getOrCreateSheet(ss, "Shopkeeper_Baki", ["Shopkeeper_UUID","দোকানদারের নাম (Name)","দোকানের নাম (Shop Name)","মার্কেট/এলাকা (Market Area)","মোবাইল নম্বর (Phone)","বর্তমান বাকি পাওনা (Current Baki ৳)","অবস্থা (Status)"]);
      for (var j = 0; j < payload.shopkeepers.length; j++) {
        var sk = payload.shopkeepers[j];
        upsertRowByKey(shopSheet, 1, sk.shopkeeperId, [sk.shopkeeperId, sk.nameBn, sk.shopNameBn || "", sk.marketAreaBn || "", sk.phoneNumber || "", sk.currentBakiBdt, sk.isDeleted ? "DELETED" : "ACTIVE"]);
        syncedCount++;
      }
    }
    if (payload.vendors && payload.vendors.length > 0) {
      var vendorSheet = getOrCreateSheet(ss, "Vendor_Baki", ["Vendor_UUID","মহাজনের নাম (Vendor Name)","আড়ত/বাজার (Market Location)","মোবাইল নম্বর (Phone)","মোট মাল কেনা (Total Purchased ৳)","বর্তমান বকেয়া দেনা (Current Baki Payable ৳)","অবস্থা (Status)"]);
      for (var m = 0; m < payload.vendors.length; m++) {
        var v = payload.vendors[m];
        upsertRowByKey(vendorSheet, 1, v.vendorId, [v.vendorId, v.nameBn, v.marketLocationBn || "", v.phoneNumber || "", v.totalPurchasedBdt, v.currentBakiBdt || 0, v.isDeleted ? "DELETED" : "ACTIVE"]);
        syncedCount++;
      }
    }
    if (payload.assets && payload.assets.length > 0) {
      var assetSheet = getOrCreateSheet(ss, "Assets_Maintenance", ["Asset_UUID","সম্পদের নাম (Asset Name)","ক্রয় মূল্য (Cost ৳)","মেয়াদ মাস (Lifespan Months)","কেনার তারিখ (Purchase Date)","পরবর্তী পরিবর্তন তারিখ (Next Due Date)","দৈনিক অবচয় (Daily Reserve ৳)","মাসিক অবচয় (Monthly Reserve ৳)","নোট (Notes)","অবস্থা (Status)"]);
      for (var k = 0; k < payload.assets.length; k++) {
        var ast = payload.assets[k];
        upsertRowByKey(assetSheet, 1, ast.assetId, [ast.assetId, ast.assetNameBn, ast.purchaseCostBdt, ast.lifespanMonths, ast.purchaseDateKey, ast.nextDueDateKey, ast.dailyDepreciationBdt, ast.monthlyDepreciationBdt, ast.notesBn || "", ast.isDeleted ? "DELETED" : (ast.isActive ? "ACTIVE" : "ARCHIVED")]);
        syncedCount++;
      }
    }
    return ContentService.createTextOutput(JSON.stringify({status:"SUCCESS", message:"ব্যাকআপ সফলভাবে সম্পন্ন হয়েছে", syncedCount: syncedCount})).setMimeType(ContentService.MimeType.JSON);
  } catch (err) {
    return ContentService.createTextOutput(JSON.stringify({status:"ERROR", message: err.toString(), syncedCount: 0})).setMimeType(ContentService.MimeType.JSON);
  } finally {
    lock.releaseLock();
  }
}

function getOrCreateSheet(ss, sheetName, headers) {
  var sheet = ss.getSheetByName(sheetName);
  if (!sheet) {
    sheet = ss.insertSheet(sheetName);
    sheet.appendRow(headers);
    sheet.getRange(1, 1, 1, headers.length).setFontWeight("bold").setBackground("#047857").setFontColor("#FFFFFF");
    sheet.setFrozenRows(1);
  } else {
    sheet.getRange(1, 1, 1, headers.length).setValues([headers]);
  }
  return sheet;
}

function upsertRowByKey(sheet, keyColIndex1Based, keyValue, rowValues) {
  var lastRow = sheet.getLastRow();
  if (lastRow > 1) {
    var keyRange = sheet.getRange(2, keyColIndex1Based, lastRow - 1, 1).getValues();
    for (var i = 0; i < keyRange.length; i++) {
      if (String(keyRange[i][0]) === String(keyValue)) {
        sheet.getRange(i + 2, 1, 1, rowValues.length).setValues([rowValues]);
        return;
      }
    }
  }
  sheet.appendRow(rowValues);
}

function pullAllDataFromSheets(ss) {
  var transactions = [], shopkeepers = [], vendors = [], assets = [], dailySummaries = [];
  var summarySheet = ss.getSheetByName("Daily_Summary");
  if (summarySheet && summarySheet.getLastRow() > 1) {
    var dsRows = summarySheet.getRange(2, 1, summarySheet.getLastRow() - 1, summarySheet.getLastColumn()).getValues();
    for (var d = 0; d < dsRows.length; d++) {
      var dr = dsRows[d];
      if (!dr[0]) continue;
      dailySummaries.push({dateKey: formatCellDateKey(dr[0], ss), totalSalesBdt: Number(dr[1] || 0), cashSalesBdt: Number(dr[2] || 0), newBakiGivenBdt: Number(dr[3] || 0), bakiCollectedBdt: Number(dr[4] || 0), wholesalePurchaseBdt: Number(dr[5] || 0), operatingExpenseBdt: Number(dr[6] || 0), assetMaintenanceBdt: Number(dr[7] || 0), dailyDepreciationBdt: Number(dr[8] || 0), netOperationalProfitBdt: Number(dr[9] || 0), adjustedNetProfitBdt: Number(dr[10] || 0), netCashFlowBdt: Number(dr[11] || 0), totalActiveMarketBakiBdt: Number(dr[12] || 0), totalActiveVendorBakiBdt: dr.length > 14 ? Number(dr[13] || 0) : 0, personalHomeExpenseBdt: dr.length > 15 ? Number(dr[14] || 0) : 0});
    }
  }
  var txSheet = ss.getSheetByName("Transactions");
  if (txSheet && txSheet.getLastRow() > 1) {
    var txRows = txSheet.getRange(2, 1, txSheet.getLastRow() - 1, txSheet.getLastColumn()).getValues();
    for (var i = 0; i < txRows.length; i++) {
      var r = txRows[i];
      if (!r[0]) continue;
      transactions.push({transactionId: String(r[0]), dateKey: formatCellDateKey(r[1], ss), txType: String(r[2] || "EXPENSE"), categoryId: String(r[3] || "CAT_OTHER"), vendorNameBn: String(r[4] || ""), shopkeeperNameBn: String(r[5] || ""), totalBillBdt: Number(r[6] || 0), cashAmountBdt: Number(r[7] || 0), bakiDeltaBdt: Number(r[8] || 0), runningShopkeeperBakiBdt: Number(r[9] || 0), noteBn: String(r[10] || ""), version: Number(r[11] || 1), isDeleted: String(r[12] || "") === "DELETED", vendorId: r.length > 13 ? String(r[13] || "") : "", shopkeeperId: r.length > 14 ? String(r[14] || "") : "", assetId: r.length > 15 ? String(r[15] || "") : ""});
    }
  }
  var shopSheet = ss.getSheetByName("Shopkeeper_Baki");
  if (shopSheet && shopSheet.getLastRow() > 1) {
    var sRows = shopSheet.getRange(2, 1, shopSheet.getLastRow() - 1, shopSheet.getLastColumn()).getValues();
    for (var j = 0; j < sRows.length; j++) {
      var sr = sRows[j];
      if (!sr[0]) continue;
      shopkeepers.push({shopkeeperId: String(sr[0]), nameBn: String(sr[1] || ""), shopNameBn: String(sr[2] || ""), marketAreaBn: String(sr[3] || ""), phoneNumber: String(sr[4] || ""), currentBakiBdt: Number(sr[5] || 0), isDeleted: String(sr[6] || "") === "DELETED"});
    }
  }
  var vendorSheet = ss.getSheetByName("Vendor_Baki");
  if (vendorSheet && vendorSheet.getLastRow() > 1) {
    var vRows = vendorSheet.getRange(2, 1, vendorSheet.getLastRow() - 1, vendorSheet.getLastColumn()).getValues();
    for (var m = 0; m < vRows.length; m++) {
      var vr = vRows[m];
      if (!vr[0]) continue;
      var totalPurch = Number(vr[4]) || 0;
      var rawBakiCol = vr.length > 5 ? vr[5] : 0;
      var isLegacy6Col = (String(rawBakiCol) === "ACTIVE" || String(rawBakiCol) === "DELETED");
      var currBaki = isLegacy6Col ? totalPurch : (Number(rawBakiCol) || 0);
      var statusStr = isLegacy6Col ? String(rawBakiCol) : (vr.length > 6 ? String(vr[6] || "") : "ACTIVE");
      vendors.push({vendorId: String(vr[0]), nameBn: String(vr[1] || ""), marketLocationBn: String(vr[2] || ""), phoneNumber: String(vr[3] || ""), totalPurchasedBdt: totalPurch, currentBakiBdt: currBaki, isDeleted: statusStr === "DELETED"});
    }
  }
  var assetSheet = ss.getSheetByName("Assets_Maintenance");
  if (assetSheet && assetSheet.getLastRow() > 1) {
    var aRows = assetSheet.getRange(2, 1, assetSheet.getLastRow() - 1, assetSheet.getLastColumn()).getValues();
    for (var k = 0; k < aRows.length; k++) {
      var ar = aRows[k];
      if (!ar[0]) continue;
      assets.push({assetId: String(ar[0]), assetNameBn: String(ar[1] || ""), purchaseCostBdt: Number(ar[2] || 0), lifespanMonths: Number(ar[3] || 24), purchaseDateKey: formatCellDateKey(ar[4], ss), nextDueDateKey: formatCellDateKey(ar[5], ss), dailyDepreciationBdt: Number(ar[6] || 0), monthlyDepreciationBdt: Number(ar[7] || 0), notesBn: String(ar[8] || ""), isActive: String(ar[9] || "") !== "ARCHIVED", isDeleted: String(ar[9] || "") === "DELETED"});
    }
  }
  return ContentService.createTextOutput(JSON.stringify({status:"SUCCESS", message:"ডাটা সফলভাবে ডাউনলোড হয়েছে", syncedCount: transactions.length + shopkeepers.length + vendors.length + assets.length, transactions: transactions, shopkeepers: shopkeepers, vendors: vendors, assets: assets, dailySummaries: dailySummaries})).setMimeType(ContentService.MimeType.JSON);
}

function formatCellDateKey(val, ss) {
  if (!val) return "";
  if (Object.prototype.toString.call(val) === "[object Date]" || val instanceof Date) {
    var tz = (ss && ss.getSpreadsheetTimeZone) ? ss.getSpreadsheetTimeZone() : "Asia/Dhaka";
    return Utilities.formatDate(val, tz, "yyyy-MM-dd");
  }
  return String(val).substring(0, 10);
}
    """.trimIndent()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("BabsharHisab_AppsScript", scriptSnippet))
}
