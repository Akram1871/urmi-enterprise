package com.akram.babsharhisab.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.local.entity.CategoryEntity
import com.akram.babsharhisab.ui.components.BanglaNumericKeypad
import com.akram.babsharhisab.ui.components.CategoryIconMapper
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionEntryScreen(
    initialMode: String?,
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val vendors by viewModel.vendors.collectAsStateWithLifecycle()
    val shopkeepers by viewModel.shopkeepers.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var showAddVendorDialog by remember { mutableStateOf(false) }
    var showAddShopkeeperDialog by remember { mutableStateOf(false) }
    var activeKeypadField by remember { mutableStateOf("TOTAL") } // TOTAL or CASH
    var isNoteFieldFocused by remember { mutableStateOf(false) }

    // Closes the phone's text keyboard (if Note was focused) and shows ONLY the single Bangla Keypad
    val activateBanglaNumberPad: (String) -> Unit = { targetField ->
        activeKeypadField = targetField
        isNoteFieldFocused = false
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    LaunchedEffect(initialMode) {
        viewModel.initializeMode(initialMode)
    }

    LaunchedEffect(state.mode, state.purchaseSubMode, state.saleSubMode) {
        activateBanglaNumberPad("TOTAL")
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TransactionUiEffect.NavigateBack -> onNavigateBack()
                is TransactionUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.messageBn)
            }
        }
    }

    val modeAccentColor = when (state.mode) {
        EntryMode.PURCHASE -> when (state.purchaseSubMode) {
            PurchaseSubMode.VENDOR_BAKI_PAY -> BakiAmber
            else -> PurchaseBlue
        }
        EntryMode.EXPENSE -> ExpenseRed
        EntryMode.SALE -> when (state.saleSubMode) {
            SaleSubMode.BAKI_COLLECT -> BakiAmber
            else -> ProfitGreen
        }
    }

    val selectedCategoryName = categories.find { it.categoryId == state.selectedCategoryId }?.nameBn ?: "খরচ"
    val selectedVendor = vendors.find { it.vendorId == state.selectedVendorId }
    val selectedShopkeeper = shopkeepers.find { it.shopkeeperId == state.selectedShopkeeperId }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.mode.titleBn,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    FilterChip(
                        selected = !state.useYesterdayDate,
                        onClick = { viewModel.toggleDateYesterday(false) },
                        label = { Text("আজ") }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    FilterChip(
                        selected = state.useYesterdayDate,
                        onClick = { viewModel.toggleDateYesterday(true) },
                        label = { Text("গতকাল") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    // ONLY show the custom Bangla numeric keypad when the Note text keyboard is NOT open.
                    val currentKeypadAscii = when {
                        activeKeypadField.startsWith("VENDOR_PURCHASE:") -> {
                            val id = activeKeypadField.removePrefix("VENDOR_PURCHASE:")
                            state.getVendorPurchaseInput(id)
                        }
                        activeKeypadField.startsWith("VENDOR_CASH:") -> {
                            val id = activeKeypadField.removePrefix("VENDOR_CASH:")
                            state.getVendorCashPaidInput(id)
                        }
                        activeKeypadField.startsWith("VENDOR_PAY:") -> {
                            val id = activeKeypadField.removePrefix("VENDOR_PAY:")
                            state.getVendorBakiPayInput(id)
                        }
                        activeKeypadField.startsWith("SHOPKEEPER_BAKI:") -> {
                            val id = activeKeypadField.removePrefix("SHOPKEEPER_BAKI:")
                            state.getShopkeeperBakiInput(id)
                        }
                        activeKeypadField.startsWith("SHOPKEEPER_COLLECT:") -> {
                            val id = activeKeypadField.removePrefix("SHOPKEEPER_COLLECT:")
                            state.getShopkeeperCollectInput(id)
                        }
                        activeKeypadField == "CASH" && (
                            (state.mode == EntryMode.PURCHASE && state.isVendorBakiPurchase) ||
                                (state.mode == EntryMode.SALE && state.isShopkeeperSale)
                            ) -> state.cashReceivedInput
                        else -> state.totalBillInput
                    }

                    if (!isNoteFieldFocused) {
                        BanglaNumericKeypad(
                            currentAsciiAmount = currentKeypadAscii,
                            onAmountChange = { newAmount ->
                                when {
                                    activeKeypadField.startsWith("VENDOR_PURCHASE:") -> {
                                        val id = activeKeypadField.removePrefix("VENDOR_PURCHASE:")
                                        viewModel.updateVendorPurchaseAmount(id, newAmount)
                                    }
                                    activeKeypadField.startsWith("VENDOR_CASH:") -> {
                                        val id = activeKeypadField.removePrefix("VENDOR_CASH:")
                                        viewModel.updateVendorCashPaidAmount(id, newAmount)
                                    }
                                    activeKeypadField.startsWith("VENDOR_PAY:") -> {
                                        val id = activeKeypadField.removePrefix("VENDOR_PAY:")
                                        viewModel.updateVendorBakiPayAmount(id, newAmount)
                                    }
                                    activeKeypadField.startsWith("SHOPKEEPER_BAKI:") -> {
                                        val id = activeKeypadField.removePrefix("SHOPKEEPER_BAKI:")
                                        viewModel.updateShopkeeperBakiAmount(id, newAmount)
                                    }
                                    activeKeypadField.startsWith("SHOPKEEPER_COLLECT:") -> {
                                        val id = activeKeypadField.removePrefix("SHOPKEEPER_COLLECT:")
                                        viewModel.updateShopkeeperCollectAmount(id, newAmount)
                                    }
                                    activeKeypadField == "CASH" && (
                                        (state.mode == EntryMode.PURCHASE && state.isVendorBakiPurchase) ||
                                            (state.mode == EntryMode.SALE && state.isShopkeeperSale)
                                        ) -> viewModel.updateCashReceivedAmount(newAmount)
                                    else -> {
                                        activeKeypadField = "TOTAL"
                                        viewModel.updateTotalAmount(newAmount)
                                    }
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.saveTransaction(stayOnScreen = true)
                            },
                            modifier = Modifier
                                .weight(1.05f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when (state.mode) {
                                    EntryMode.PURCHASE -> when (state.purchaseSubMode) {
                                        PurchaseSubMode.QUICK_CASH -> "সেভ ও আরেক কেনা +"
                                        PurchaseSubMode.VENDOR_BAKI_PURCHASE -> "সেভ ও আরেক মহাজন +"
                                        PurchaseSubMode.VENDOR_BAKI_PAY -> "সেভ ও আরেক পরিশোধ +"
                                    }
                                    EntryMode.EXPENSE -> "সেভ ও আরেক খরচ +"
                                    EntryMode.SALE -> when (state.saleSubMode) {
                                        SaleSubMode.QUICK_CASH -> "সেভ ও আরেক বিক্রি +"
                                        SaleSubMode.SHOPKEEPER_SALE -> "সেভ ও আরেক দোকানদার +"
                                        SaleSubMode.BAKI_COLLECT -> "সেভ ও আরেক আদায় +"
                                    }
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.saveTransaction(stayOnScreen = false)
                            },
                            modifier = Modifier
                                .weight(1.1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = modeAccentColor)
                        ) {
                            Text(
                                text = "হিসাব সেভ করুন",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Mode Switcher Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EntryMode.entries.forEach { entryMode ->
                    val selected = state.mode == entryMode
                    val tabColor = when (entryMode) {
                        EntryMode.PURCHASE -> PurchaseBlue
                        EntryMode.EXPENSE -> ExpenseRed
                        EntryMode.SALE -> ProfitGreen
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.setEntryMode(entryMode)
                            },
                        color = if (selected) tabColor else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                            Text(
                                text = entryMode.titleBn,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // If in PURCHASE mode, show the 3-way Sub-Mode Switcher right at the top:
            // ১. নগদ কেনা | ২. মহাজনের বাকি | ৩. মহাজন পরিশোধ
            if (state.mode == EntryMode.PURCHASE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PurchaseSubMode.entries.forEach { subMode ->
                        val selected = state.purchaseSubMode == subMode
                        FilterChip(
                            selected = selected,
                            onClick = {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.setPurchaseSubMode(subMode)
                            },
                            label = {
                                Text(
                                    text = subMode.labelBn,
                                    maxLines = 1,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // If in SALE mode, show the 3-way Sub-Mode Switcher right at the top:
            // ১. নগদ বিক্রি | ২. দোকানদারকে বাকি | ৩. পুরাতন বাকি আদায়
            if (state.mode == EntryMode.SALE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SaleSubMode.entries.forEach { subMode ->
                        val selected = state.saleSubMode == subMode
                        FilterChip(
                            selected = selected,
                            onClick = {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.setSaleSubMode(subMode)
                            },
                            label = {
                                Text(
                                    text = subMode.labelBn,
                                    maxLines = 1,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Single-Keypad Primary Amount Display Card
            val displayPrimaryBdt = when {
                state.mode == EntryMode.PURCHASE && state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE && state.totalMultiVendorPurchaseBdt > 0L ->
                    state.totalMultiVendorPurchaseBdt
                state.mode == EntryMode.PURCHASE && state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PAY && state.totalMultiVendorBakiPayBdt > 0L ->
                    state.totalMultiVendorBakiPayBdt
                state.mode == EntryMode.SALE && state.saleSubMode == SaleSubMode.SHOPKEEPER_SALE ->
                    state.effectiveShopkeeperSaleTotalBdt
                state.mode == EntryMode.SALE && state.saleSubMode == SaleSubMode.BAKI_COLLECT && state.totalMultiShopkeeperCollectBdt > 0L ->
                    state.totalMultiShopkeeperCollectBdt
                else -> state.parsedTotalBdt
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { activateBanglaNumberPad("TOTAL") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (activeKeypadField == "TOTAL" && !isNoteFieldFocused) modeAccentColor
                    else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (state.mode) {
                                EntryMode.PURCHASE -> when (state.purchaseSubMode) {
                                    PurchaseSubMode.QUICK_CASH -> "নগদ মাল কেনার পরিমাণ (৳)"
                                    PurchaseSubMode.VENDOR_BAKI_PURCHASE -> "আজকের মোট মাল কেনা (১ বা একাধিক মহাজন) (৳)"
                                    PurchaseSubMode.VENDOR_BAKI_PAY -> "মহাজনদের আজ মোট পরিশোধকৃত টাকা (৳)"
                                }
                                EntryMode.EXPENSE -> "$selectedCategoryName — খরচের পরিমাণ (৳)"
                                EntryMode.SALE -> when (state.saleSubMode) {
                                    SaleSubMode.QUICK_CASH -> "নগদ বিক্রির পরিমাণ (৳)"
                                    SaleSubMode.SHOPKEEPER_SALE -> "আজকের মোট বিক্রি (নগদ + সব দোকানদারের বাকি সহ) (৳)"
                                    SaleSubMode.BAKI_COLLECT -> "দোকানদারদের থেকে আজ মোট আদায়কৃত টাকা (৳)"
                                }
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(displayPrimaryBdt),
                            style = MaterialTheme.typography.displayLarge,
                            color = modeAccentColor
                        )
                        if (state.mode == EntryMode.SALE && state.saleSubMode == SaleSubMode.SHOPKEEPER_SALE) {
                            Text(
                                text = "মোট বাকি (${BanglaNumberFormatter.toBanglaDigits(state.effectiveSelectedShopkeeperIds.size.toString())} জন): ${BanglaNumberFormatter.formatTaka(state.calculatedNewBakiBdt)}  •  নগদ বিক্রি: ${BanglaNumberFormatter.formatTaka(state.calculatedRemainingCashSaleBdt)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = BakiAmber,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (state.mode == EntryMode.PURCHASE && state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE && state.effectiveSelectedVendorIds.isNotEmpty()) {
                            Text(
                                text = "নির্বাচিত মহাজন: ${BanglaNumberFormatter.toBanglaDigits(state.effectiveSelectedVendorIds.size.toString())} জন  •  আজ মোট নগদ পরিশোধ: ${BanglaNumberFormatter.formatTaka(state.totalMultiVendorCashPaidBdt)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = PurchaseBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (state.totalBillInput.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                activateBanglaNumberPad("TOTAL")
                                viewModel.updateTotalAmount("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "মুছুন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Mode-Specific Section
            when (state.mode) {
                EntryMode.PURCHASE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (state.purchaseSubMode) {
                                PurchaseSubMode.QUICK_CASH -> "মহাজন নির্বাচন (উপর-নিচ স্ক্রল করুন):"
                                PurchaseSubMode.VENDOR_BAKI_PURCHASE -> "মহাজন নির্বাচন করুন (একাধিক মহাজন সিলেক্ট করা যাবে):"
                                PurchaseSubMode.VENDOR_BAKI_PAY -> "কোন মহাজনের বাকি পরিশোধ করছেন নির্বাচন করুন (একাধিক সিলেক্ট করা যাবে):"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = { showAddVendorDialog = true },
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন মহাজন", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                        }
                    }

                    // Vertically Scrollable Toast List for Mahajans (Vendors)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp, max = 165.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        if (vendors.isEmpty() && state.purchaseSubMode != PurchaseSubMode.QUICK_CASH) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "কোনো মহাজন যুক্ত নেই। উপরের '+ নতুন মহাজন' বাটনে চাপ দিয়ে যোগ করুন।",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (state.purchaseSubMode == PurchaseSubMode.QUICK_CASH) {
                                    item(key = "GENERAL_CASH_PURCHASE") {
                                        val isGeneralSelected = state.selectedVendorId == null
                                        VerticalSelectorToastItem(
                                            titleBn = "সাধারণ নগদ কেনা (মহাজন ছাড়া)",
                                            subtitleBn = "যেকোনো খুচরা বা সাধারণ নগদ মাল ক্রয়",
                                            badgeTextBn = "নগদ",
                                            isSelected = isGeneralSelected,
                                            accentColor = PurchaseBlue,
                                            onClick = {
                                                activateBanglaNumberPad("TOTAL")
                                                viewModel.selectVendor(null)
                                            }
                                        )
                                    }
                                }
                                items(vendors, key = { it.vendorId }) { vendor ->
                                    val isSelected = if (state.purchaseSubMode == PurchaseSubMode.QUICK_CASH) {
                                        state.selectedVendorId == vendor.vendorId
                                    } else {
                                        state.effectiveSelectedVendorIds.contains(vendor.vendorId)
                                    }
                                    val sub = if (vendor.marketLocationBn.isNotBlank()) {
                                        "আড়ত/বাজার: ${vendor.marketLocationBn} • মোট কেনা: ${BanglaNumberFormatter.formatTaka(vendor.totalPurchasedBdt)}"
                                    } else {
                                        "মোট কেনা: ${BanglaNumberFormatter.formatTaka(vendor.totalPurchasedBdt)}"
                                    }
                                    VerticalSelectorToastItem(
                                        titleBn = vendor.nameBn,
                                        subtitleBn = sub,
                                        badgeTextBn = "দেনা: ${BanglaNumberFormatter.formatTaka(vendor.currentBakiBdt)}",
                                        isSelected = isSelected,
                                        accentColor = PurchaseBlue,
                                        onClick = {
                                            if (state.purchaseSubMode == PurchaseSubMode.QUICK_CASH) {
                                                activateBanglaNumberPad("TOTAL")
                                                viewModel.selectVendor(vendor.vendorId)
                                            } else {
                                                val willBeSelected = !state.effectiveSelectedVendorIds.contains(vendor.vendorId)
                                                viewModel.toggleVendorSelection(vendor.vendorId)
                                                if (willBeSelected) {
                                                    val fieldPrefix = if (state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PAY) {
                                                        "VENDOR_PAY"
                                                    } else {
                                                        "VENDOR_PURCHASE"
                                                    }
                                                    activateBanglaNumberPad("$fieldPrefix:${vendor.vendorId}")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Purchase Sub-mode 2: Multi-Mahajan Purchase + Partial/Extra Cash Payment (covers new purchase + previous Baki)
                    if (state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PURCHASE) {
                        val selectedVendorProfiles = vendors.filter { state.effectiveSelectedVendorIds.contains(it.vendorId) }
                        if (selectedVendorProfiles.isEmpty()) {
                            Text(
                                text = "উপরের তালিকা থেকে ১ বা একাধিক মহাজন সিলেক্ট করুন যাদের থেকে আজ মাল কিনছেন বা বকেয়া পরিশোধ করছেন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PurchaseBlue
                            )
                        } else {
                            selectedVendorProfiles.forEach { v ->
                                val purchaseFieldKey = "VENDOR_PURCHASE:${v.vendorId}"
                                val cashFieldKey = "VENDOR_CASH:${v.vendorId}"
                                val vPurchaseBdt = state.getVendorPurchaseBdt(v.vendorId)
                                val vCashPaidBdt = state.getVendorCashPaidBdt(v.vendorId)
                                val vNetRunningBaki = (v.currentBakiBdt + vPurchaseBdt - vCashPaidBdt).coerceAtLeast(0L)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, PurchaseBlue.copy(alpha = 0.45f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = v.nameBn,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PurchaseBlue
                                                )
                                                Text(
                                                    text = "আগের বকেয়া দেনা: ${BanglaNumberFormatter.formatTaka(v.currentBakiBdt)}  →  নতুন মোট দেনা থাকবে: ${BanglaNumberFormatter.formatTaka(vNetRunningBaki)}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = BakiAmber,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.removeVendorSelection(v.vendorId) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "তালিকা থেকে সরান",
                                                    tint = ExpenseRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Box 1: Today's New Purchase for this Mahajan
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { activateBanglaNumberPad(purchaseFieldKey) },
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(
                                                    width = if (activeKeypadField == purchaseFieldKey && !isNoteFieldFocused) 2.dp else 1.dp,
                                                    color = if (activeKeypadField == purchaseFieldKey && !isNoteFieldFocused) PurchaseBlue else MaterialTheme.colorScheme.outline
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = "আজ নতুন কেনা (৳):",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = BanglaNumberFormatter.formatTaka(vPurchaseBdt),
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color = PurchaseBlue,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            // Box 2: Today's Cash Paid to this Mahajan (can cover new purchase + old Baki!)
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { activateBanglaNumberPad(cashFieldKey) },
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(
                                                    width = if (activeKeypadField == cashFieldKey && !isNoteFieldFocused) 2.dp else 1.dp,
                                                    color = if (activeKeypadField == cashFieldKey && !isNoteFieldFocused) ProfitGreen else MaterialTheme.colorScheme.outline
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Text(
                                                        text = "আজ নগদ পরিশোধ (৳):",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = BanglaNumberFormatter.formatTaka(vCashPaidBdt),
                                                        style = MaterialTheme.typography.titleLarge,
                                                        color = ProfitGreen,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    activateBanglaNumberPad(cashFieldKey)
                                                    viewModel.updateVendorCashPaidAmount(v.vendorId, "0")
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("পুরো বাকি", maxLines = 1)
                                            }
                                            OutlinedButton(
                                                onClick = {
                                                    activateBanglaNumberPad(cashFieldKey)
                                                    viewModel.updateVendorCashPaidAmount(
                                                        v.vendorId,
                                                        vPurchaseBdt.toString()
                                                    )
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("আজকের বিল নগদ", maxLines = 1)
                                            }
                                            if (v.currentBakiBdt > 0L) {
                                                OutlinedButton(
                                                    onClick = {
                                                        activateBanglaNumberPad(cashFieldKey)
                                                        viewModel.updateVendorCashPaidAmount(
                                                            v.vendorId,
                                                            (vPurchaseBdt + v.currentBakiBdt).toString()
                                                        )
                                                    },
                                                    modifier = Modifier.weight(1.1f)
                                                ) {
                                                    Text("আগেরসহ সব শোধ", maxLines = 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Purchase Sub-mode 3: Pay back previously taken Baki to 1 or multiple Mahajans anytime
                    if (state.purchaseSubMode == PurchaseSubMode.VENDOR_BAKI_PAY) {
                        val selectedVendorProfiles = vendors.filter { state.effectiveSelectedVendorIds.contains(it.vendorId) }
                        if (selectedVendorProfiles.isEmpty()) {
                            Text(
                                text = "উপরের তালিকা থেকে যে মহাজনদের বকেয়া পরিশোধ করছেন তাদের নির্বাচন করুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BakiAmber
                            )
                        } else {
                            selectedVendorProfiles.forEach { v ->
                                val payFieldKey = "VENDOR_PAY:${v.vendorId}"
                                val vPayBdt = state.getVendorBakiPayBdt(v.vendorId)
                                val remainingVendorDue = (v.currentBakiBdt - vPayBdt).coerceAtLeast(0L)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { activateBanglaNumberPad(payFieldKey) },
                                    colors = CardDefaults.cardColors(containerColor = BakiAmber.copy(alpha = 0.10f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        width = if (activeKeypadField == payFieldKey && !isNoteFieldFocused) 2.dp else 1.dp,
                                        color = if (activeKeypadField == payFieldKey && !isNoteFieldFocused) BakiAmber else BakiAmber.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${v.nameBn} — বর্তমান দেনা: ${BanglaNumberFormatter.formatTaka(v.currentBakiBdt)}",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "আজ পরিশোধ (এখানে চাপুন): ${BanglaNumberFormatter.formatTaka(vPayBdt)}  •  বাকি থাকবে: ${BanglaNumberFormatter.formatTaka(remainingVendorDue)}",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = PurchaseBlue,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            if (v.currentBakiBdt > 0L) {
                                                OutlinedButton(
                                                    onClick = {
                                                        activateBanglaNumberPad(payFieldKey)
                                                        viewModel.updateVendorBakiPayAmount(v.vendorId, v.currentBakiBdt.toString())
                                                    }
                                                ) {
                                                    Text("পুরো শোধ")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                EntryMode.EXPENSE -> {
                    Text(
                        text = "খরচের খাত নির্বাচন করুন (প্রতিটি খাতে আলাদা টাকা লিখতে পারবেন):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    val expenseCategories = categories.filter {
                        it.type == "EXPENSE" ||
                            it.categoryId == CategoryEntity.CAT_ASSET ||
                            it.categoryId == CategoryEntity.CAT_HOME_EXPENSE
                    }
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        expenseCategories.forEach { cat ->
                            val isSelected = state.selectedCategoryId == cat.categoryId
                            val catColor = parseHexColor(cat.colorHex)
                            val catSavedDraftBdt = state.getExpenseAmountForCategory(cat.categoryId)

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        activateBanglaNumberPad("TOTAL")
                                        viewModel.selectCategory(cat.categoryId)
                                    },
                                color = if (isSelected) catColor.copy(alpha = 0.16f)
                                else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) catColor else MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = CategoryIconMapper.getIcon(cat.iconName),
                                        contentDescription = null,
                                        tint = catColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (cat.categoryId == CategoryEntity.CAT_HOME_EXPENSE) {
                                                "${cat.nameBn} (লাভ-ক্ষতির বাইরে)"
                                            } else {
                                                cat.nameBn
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        if (catSavedDraftBdt > 0L) {
                                            Text(
                                                text = BanglaNumberFormatter.formatTaka(catSavedDraftBdt),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = catColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (state.selectedCategoryId == CategoryEntity.CAT_HOME_EXPENSE) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = parseHexColor("#9333EA").copy(alpha = 0.10f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, parseHexColor("#9333EA").copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "নোট: বাসা ও সংসারের খরচ ব্যবসার নিট লাভ বা ক্ষতির হিসাব থেকে কাটা হবে না — এটি শুধুমাত্র আপনার ব্যক্তিগত সংসার খরচের আলাদা হিসাব রাখবে।",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = parseHexColor("#7E22CE"),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                EntryMode.SALE -> {
                    if (state.saleSubMode == SaleSubMode.SHOPKEEPER_SALE || state.saleSubMode == SaleSubMode.BAKI_COLLECT) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.saleSubMode == SaleSubMode.SHOPKEEPER_SALE) {
                                    "যারা আজ বাকি নিচ্ছে সেই দোকানদারদের নির্বাচন করুন (একাধিক সিলেক্ট করা যাবে):"
                                } else {
                                    "যাদের থেকে বাকি আদায় করছেন নির্বাচন করুন (একাধিক সিলেক্ট করা যাবে):"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { showAddShopkeeperDialog = true },
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("নতুন দোকানদার", style = MaterialTheme.typography.labelMedium, maxLines = 1)
                            }
                        }

                        // Vertically Scrollable Toast List for Dokandars (Shopkeepers) — Supports Multi-Select!
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp, max = 165.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        ) {
                            if (shopkeepers.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "কোনো দোকানদার যুক্ত নেই। উপরের '+ নতুন দোকানদার' বাটনে চাপ দিয়ে যোগ করুন।",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(shopkeepers, key = { it.shopkeeperId }) { shopkeeper ->
                                        val isSelected = state.effectiveSelectedShopkeeperIds.contains(shopkeeper.shopkeeperId)
                                        val title = if (shopkeeper.shopNameBn.isNotBlank()) {
                                            "${shopkeeper.shopNameBn} (${shopkeeper.nameBn})"
                                        } else {
                                            shopkeeper.nameBn
                                        }
                                        val subtitle = if (shopkeeper.marketAreaBn.isNotBlank()) {
                                            "মার্কেট: ${shopkeeper.marketAreaBn}"
                                        } else {
                                            ""
                                        }
                                        VerticalSelectorToastItem(
                                            titleBn = title,
                                            subtitleBn = subtitle,
                                            badgeTextBn = "বাকি: ${BanglaNumberFormatter.formatTaka(shopkeeper.currentBakiBdt)}",
                                            isSelected = isSelected,
                                            accentColor = ProfitGreen,
                                            onClick = {
                                                val willBeSelected = !state.effectiveSelectedShopkeeperIds.contains(shopkeeper.shopkeeperId)
                                                viewModel.toggleShopkeeperSelection(shopkeeper.shopkeeperId)
                                                if (willBeSelected) {
                                                    val fieldPrefix = if (state.saleSubMode == SaleSubMode.BAKI_COLLECT) {
                                                        "SHOPKEEPER_COLLECT"
                                                    } else {
                                                        "SHOPKEEPER_BAKI"
                                                    }
                                                    activateBanglaNumberPad("$fieldPrefix:${shopkeeper.shopkeeperId}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sub-mode 2: Multi-Dokandar Baki within Total Selling
                    if (state.saleSubMode == SaleSubMode.SHOPKEEPER_SALE) {
                        val selectedShopkeeperList = shopkeepers.filter {
                            state.effectiveSelectedShopkeeperIds.contains(it.shopkeeperId)
                        }

                        if (selectedShopkeeperList.isEmpty()) {
                            Text(
                                text = "১. উপরে আজকের মোট বিক্রির বিল লিখুন (ঐচ্ছিক)\n২. তালিকা থেকে যেসব দোকানদার আজ বাকি নিয়েছে তাদের একের পর এক সিলেক্ট করে প্রত্যেকের বাকির পরিমাণ লিখুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ProfitGreen
                            )
                        } else {
                            selectedShopkeeperList.forEach { sk ->
                                val bakiFieldKey = "SHOPKEEPER_BAKI:${sk.shopkeeperId}"
                                val skNewBakiBdt = state.getShopkeeperBakiBdt(sk.shopkeeperId)
                                val skUpdatedTotalBaki = (sk.currentBakiBdt + skNewBakiBdt).coerceAtLeast(0L)
                                val displayName = if (sk.shopNameBn.isNotBlank()) "${sk.shopNameBn} (${sk.nameBn})" else sk.nameBn

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { activateBanglaNumberPad(bakiFieldKey) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        width = if (activeKeypadField == bakiFieldKey && !isNoteFieldFocused) 2.dp else 1.dp,
                                        color = if (activeKeypadField == bakiFieldKey && !isNoteFieldFocused) ProfitGreen else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = displayName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = ProfitGreen
                                            )
                                            Text(
                                                text = "আগের বাকি: ${BanglaNumberFormatter.formatTaka(sk.currentBakiBdt)} → মোট বাকি হবে: ${BanglaNumberFormatter.formatTaka(skUpdatedTotalBaki)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = BakiAmber,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "আজকের বাকি (চাপুন):",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                            Text(
                                                text = BanglaNumberFormatter.formatTaka(skNewBakiBdt),
                                                style = MaterialTheme.typography.titleLarge,
                                                color = BakiAmber,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { viewModel.removeShopkeeperSelection(sk.shopkeeperId) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "তালিকা থেকে সরান",
                                                tint = ExpenseRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Summary & Optional Extra Cash Received Box
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { activateBanglaNumberPad("CASH") },
                                colors = CardDefaults.cardColors(containerColor = ProfitGreen.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (activeKeypadField == "CASH" && !isNoteFieldFocused) 2.dp else 1.dp,
                                    color = if (activeKeypadField == "CASH" && !isNoteFieldFocused) ProfitGreen else ProfitGreen.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "নগদ বিক্রি / নগদ জমা (প্রয়োজনে চাপুন):",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = BanglaNumberFormatter.formatTaka(state.calculatedRemainingCashSaleBdt),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = ProfitGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "আজকের সব দোকানদারের মোট বাকি:",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Text(
                                            text = BanglaNumberFormatter.formatTaka(state.calculatedNewBakiBdt),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = BakiAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sub-mode 3: Collect Previously Given Baki from 1 or Multiple Shopkeepers
                    if (state.saleSubMode == SaleSubMode.BAKI_COLLECT) {
                        val selectedShopkeeperList = shopkeepers.filter {
                            state.effectiveSelectedShopkeeperIds.contains(it.shopkeeperId)
                        }
                        if (selectedShopkeeperList.isEmpty()) {
                            Text(
                                text = "উপরের তালিকা থেকে যে দোকানদারদের কাছ থেকে বকেয়া টাকা আদায় করছেন তাদের নির্বাচন করুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BakiAmber
                            )
                        } else {
                            selectedShopkeeperList.forEach { sk ->
                                val collectFieldKey = "SHOPKEEPER_COLLECT:${sk.shopkeeperId}"
                                val collectedBdt = state.getShopkeeperCollectBdt(sk.shopkeeperId)
                                val remainingDue = (sk.currentBakiBdt - collectedBdt).coerceAtLeast(0L)
                                val displayName = if (sk.shopNameBn.isNotBlank()) "${sk.shopNameBn} (${sk.nameBn})" else sk.nameBn

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { activateBanglaNumberPad(collectFieldKey) },
                                    colors = CardDefaults.cardColors(containerColor = BakiAmber.copy(alpha = 0.10f)),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        width = if (activeKeypadField == collectFieldKey && !isNoteFieldFocused) 2.dp else 1.dp,
                                        color = if (activeKeypadField == collectFieldKey && !isNoteFieldFocused) BakiAmber else BakiAmber.copy(alpha = 0.4f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "$displayName — বর্তমান বকেয়া: ${BanglaNumberFormatter.formatTaka(sk.currentBakiBdt)}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "আজ আদায় (চাপুন): ${BanglaNumberFormatter.formatTaka(collectedBdt)}  •  বাকি থাকবে: ${BanglaNumberFormatter.formatTaka(remainingDue)}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = ProfitGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (sk.currentBakiBdt > 0L) {
                                            OutlinedButton(
                                                onClick = {
                                                    activateBanglaNumberPad(collectFieldKey)
                                                    viewModel.updateShopkeeperCollectAmount(
                                                        sk.shopkeeperId,
                                                        sk.currentBakiBdt.toString()
                                                    )
                                                }
                                            ) {
                                                Text("পুরো আদায়")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Optional Note Input
            OutlinedTextField(
                value = state.noteBn,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("বিবরণ বা নোট লিখুন (ঐচ্ছিক - যেমন: ৫ বস্তা মাল / চাকা মেরামত)") },
                trailingIcon = {
                    if (isNoteFieldFocused || state.noteBn.isNotEmpty()) {
                        TextButton(onClick = { activateBanglaNumberPad("TOTAL") }) {
                            Text("সম্পন্ন", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { activateBanglaNumberPad("TOTAL") }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isNoteFieldFocused = focusState.isFocused
                    },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (!state.errorMessageBn.isNullOrBlank()) {
                Text(
                    text = state.errorMessageBn!!,
                    color = ExpenseRed,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showAddVendorDialog) {
        AddVendorDialog(
            onDismiss = { showAddVendorDialog = false },
            onSave = { name, market, phone ->
                viewModel.createNewVendorInline(name, market, phone)
                showAddVendorDialog = false
            }
        )
    }

    if (showAddShopkeeperDialog) {
        AddShopkeeperDialog(
            onDismiss = { showAddShopkeeperDialog = false },
            onSave = { name, shop, market, phone ->
                viewModel.createNewShopkeeperInline(name, shop, market, phone)
                showAddShopkeeperDialog = false
            }
        )
    }
}

@Composable
private fun VerticalSelectorToastItem(
    titleBn: String,
    subtitleBn: String,
    badgeTextBn: String,
    isSelected: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) accentColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Column {
                    Text(
                        text = titleBn,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (subtitleBn.isNotBlank()) {
                        Text(
                            text = subtitleBn,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = if (isSelected) accentColor else BakiAmber.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeTextBn,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else BakiAmber,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun AddVendorDialog(
    onDismiss: () -> Unit,
    onSave: (nameBn: String, marketBn: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন মহাজন / পাইকার যোগ করুন") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("মহাজনের নাম *") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = market,
                    onValueChange = { market = it },
                    label = { Text("পাইকারি বাজার / আড়তের ঠিকানা") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর (ঐচ্ছিক)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, market, phone) }
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

@Composable
fun AddShopkeeperDialog(
    onDismiss: () -> Unit,
    onSave: (nameBn: String, shopNameBn: String, marketBn: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shop by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন দোকানদার যোগ করুন") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("দোকানদারের নাম *") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = shop,
                    onValueChange = { shop = it },
                    label = { Text("দোকানের নাম (যেমন: বিসমিল্লাহ স্টোর)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = market,
                    onValueChange = { market = it },
                    label = { Text("মার্কেট / এলাকার নাম") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name, shop, market, phone) }
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
