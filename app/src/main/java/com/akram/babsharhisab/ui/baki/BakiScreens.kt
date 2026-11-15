package com.akram.babsharhisab.ui.baki

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.domain.model.ShopkeeperAccount
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.domain.model.VendorProfile
import com.akram.babsharhisab.ui.components.BanglaNumericKeypad
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.BakiAmberBgLight
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.theme.PurchaseBlueBgLight
import com.akram.babsharhisab.ui.transaction.DeleteConfirmationDialog
import com.akram.babsharhisab.ui.transaction.EditTransactionDialog
import com.akram.babsharhisab.ui.transaction.TransactionRowCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BakiListScreen(
    viewModel: BakiViewModel,
    onOpenShopkeeperDetail: (String) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val shopkeepers by viewModel.filteredShopkeepers.collectAsStateWithLifecycle()
    val vendors by viewModel.filteredVendors.collectAsStateWithLifecycle()
    val totalMarketBaki by viewModel.totalMarketBaki.collectAsStateWithLifecycle()
    val activeBakiCount by viewModel.activeBakiCount.collectAsStateWithLifecycle()
    val totalVendorBaki by viewModel.totalVendorBaki.collectAsStateWithLifecycle()
    val activeVendorBakiCount by viewModel.activeVendorBakiCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    var collectingShopkeeper by remember { mutableStateOf<ShopkeeperAccount?>(null) }
    var editingShopkeeper by remember { mutableStateOf<ShopkeeperAccount?>(null) }
    var deletingShopkeeper by remember { mutableStateOf<ShopkeeperAccount?>(null) }
    var payingVendor by remember { mutableStateOf<VendorProfile?>(null) }
    var editingVendor by remember { mutableStateOf<VendorProfile?>(null) }
    var deletingVendor by remember { mutableStateOf<VendorProfile?>(null) }
    var showAddShopkeeperDialog by remember { mutableStateOf(false) }
    var showAddVendorDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("বাকি খাতা (দোকানদার ও মহাজন)") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) {
                        showAddShopkeeperDialog = true
                    } else {
                        showAddVendorDialog = true
                    }
                },
                containerColor = if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) {
                    MaterialTheme.colorScheme.primary
                } else {
                    PurchaseBlue
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) "নতুন দোকানদার" else "নতুন মহাজন",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 2-Way Tab Switcher: ১. দোকানদারের বাকি | ২. মহাজনের বাকি (দেনা)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BakiTabMode.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val activeColor = if (tab == BakiTabMode.SHOPKEEPER_RECEIVABLE) BakiAmber else PurchaseBlue
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.selectTab(tab) },
                        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(
                                text = tab.labelBn,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Compact Top Summary Banner (Fixed height & weight so right badge never wraps vertically)
            if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BakiAmberBgLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মার্কেটে মোট বকেয়া পাওনা",
                                style = MaterialTheme.typography.labelLarge,
                                color = BakiAmber,
                                maxLines = 1
                            )
                            Text(
                                text = BanglaNumberFormatter.formatTaka(totalMarketBaki),
                                style = MaterialTheme.typography.headlineLarge,
                                color = BakiAmber,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = BakiAmber.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${BanglaNumberFormatter.toBanglaDigits(activeBakiCount)}টি দোকানে বাকি",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BakiAmber,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PurchaseBlueBgLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মহাজনের কাছে মোট বকেয়া দেনা",
                                style = MaterialTheme.typography.labelLarge,
                                color = PurchaseBlue,
                                maxLines = 1
                            )
                            Text(
                                text = BanglaNumberFormatter.formatTaka(totalVendorBaki),
                                style = MaterialTheme.typography.headlineLarge,
                                color = PurchaseBlue,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = PurchaseBlue.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${BanglaNumberFormatter.toBanglaDigits(activeVendorBakiCount)} জন মহাজন পাবেন",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = PurchaseBlue,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Compact Single-Line Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        text = if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) {
                            "দোকানদার বা মার্কেট খুঁজুন..."
                        } else {
                            "মহাজন বা আড়ত খুঁজুন..."
                        },
                        maxLines = 1
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Single-Line Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BakiFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.updateFilter(filter) },
                        label = {
                            Text(
                                text = filter.labelBn,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    )
                }
            }

            if (selectedTab == BakiTabMode.SHOPKEEPER_RECEIVABLE) {
                if (shopkeepers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো দোকানদারের তথ্য পাওয়া যায়নি। নিচের বাটনে চাপ দিয়ে দোকানদার যোগ করুন।",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(shopkeepers, key = { it.shopkeeperId }) { shopkeeper ->
                            ShopkeeperBakiCard(
                                shopkeeper = shopkeeper,
                                onCardClick = { onOpenShopkeeperDetail(shopkeeper.shopkeeperId) },
                                onCollectClick = { collectingShopkeeper = shopkeeper },
                                onEditClick = { editingShopkeeper = shopkeeper },
                                onDeleteClick = { deletingShopkeeper = shopkeeper }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            } else {
                if (vendors.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোনো মহাজনের তথ্য পাওয়া যায়নি। নিচের বাটনে চাপ দিয়ে মহাজন যোগ করুন।",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(vendors, key = { it.vendorId }) { vendor ->
                            VendorBakiCard(
                                vendor = vendor,
                                onPayClick = { payingVendor = vendor },
                                onEditClick = { editingVendor = vendor },
                                onDeleteClick = { deletingVendor = vendor }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
    }

    if (collectingShopkeeper != null) {
        QuickBakiCollectionSheet(
            shopkeeper = collectingShopkeeper!!,
            onDismiss = { collectingShopkeeper = null },
            onConfirmCollection = { amountStr, note ->
                viewModel.collectBaki(
                    shopkeeperId = collectingShopkeeper!!.shopkeeperId,
                    amountInput = amountStr,
                    noteBn = note,
                    onComplete = { collectingShopkeeper = null }
                )
            }
        )
    }

    if (payingVendor != null) {
        QuickVendorBakiPaySheet(
            vendor = payingVendor!!,
            onDismiss = { payingVendor = null },
            onConfirmPayment = { amountStr, note ->
                viewModel.payVendorBaki(
                    vendorId = payingVendor!!.vendorId,
                    amountInput = amountStr,
                    noteBn = note,
                    onComplete = { payingVendor = null }
                )
            }
        )
    }

    if (showAddShopkeeperDialog) {
        AddShopkeeperWithOpeningBakiDialog(
            onDismiss = { showAddShopkeeperDialog = false },
            onSave = { name, shop, market, phone, opening ->
                viewModel.addShopkeeper(name, shop, market, phone, opening)
                showAddShopkeeperDialog = false
            }
        )
    }

    if (showAddVendorDialog) {
        AddVendorWithOpeningBakiDialog(
            onDismiss = { showAddVendorDialog = false },
            onSave = { name, market, phone, opening ->
                viewModel.addVendor(name, market, phone, opening)
                showAddVendorDialog = false
            }
        )
    }

    if (editingShopkeeper != null) {
        EditShopkeeperDialog(
            shopkeeper = editingShopkeeper!!,
            onDismiss = { editingShopkeeper = null },
            onSave = { name, shop, market, phone, currentBaki ->
                viewModel.updateShopkeeper(
                    shopkeeperId = editingShopkeeper!!.shopkeeperId,
                    nameBn = name,
                    shopNameBn = shop,
                    marketAreaBn = market,
                    phoneNumber = phone,
                    currentBakiInput = currentBaki
                )
                editingShopkeeper = null
            }
        )
    }

    if (deletingShopkeeper != null) {
        DeleteConfirmationDialog(
            titleBn = "দোকানদার ও বাকি মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে '${deletingShopkeeper!!.nameBn}' (বর্তমান বাকি: ${BanglaNumberFormatter.formatTaka(deletingShopkeeper!!.currentBakiBdt)}) এবং তার বাকি খাতার তথ্য মুছে ফেলতে চান?",
            onDismiss = { deletingShopkeeper = null },
            onConfirmDelete = {
                viewModel.deleteShopkeeper(deletingShopkeeper!!.shopkeeperId)
                deletingShopkeeper = null
            }
        )
    }

    if (editingVendor != null) {
        EditVendorDialog(
            vendor = editingVendor!!,
            onDismiss = { editingVendor = null },
            onSave = { name, market, phone, currentBaki ->
                viewModel.updateVendor(
                    vendorId = editingVendor!!.vendorId,
                    nameBn = name,
                    marketLocationBn = market,
                    phoneNumber = phone,
                    currentBakiInput = currentBaki
                )
                editingVendor = null
            }
        )
    }

    if (deletingVendor != null) {
        DeleteConfirmationDialog(
            titleBn = "মহাজনের তথ্য মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে মহাজন '${deletingVendor!!.nameBn}' (বর্তমান দেনা: ${BanglaNumberFormatter.formatTaka(deletingVendor!!.currentBakiBdt)}) মুছে ফেলতে চান?",
            onDismiss = { deletingVendor = null },
            onConfirmDelete = {
                viewModel.deleteVendor(deletingVendor!!.vendorId)
                deletingVendor = null
            }
        )
    }
}

@Composable
fun ShopkeeperBakiCard(
    shopkeeper: ShopkeeperAccount,
    onCardClick: () -> Unit,
    onCollectClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hasDue = shopkeeper.currentBakiBdt > 0L

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasDue) BakiAmber.copy(alpha = 0.16f)
                            else ProfitGreen.copy(alpha = 0.16f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = shopkeeper.nameBn.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasDue) BakiAmber else ProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (shopkeeper.shopNameBn.isNotBlank()) {
                            "${shopkeeper.shopNameBn} (${shopkeeper.nameBn})"
                        } else {
                            shopkeeper.nameBn
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (shopkeeper.marketAreaBn.isNotBlank()) {
                        Text(
                            text = "মার্কেট: ${shopkeeper.marketAreaBn}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "সর্বশেষ লেনদেন: ${BanglaDateUtils.formatRelativeDaysAgo(shopkeeper.lastTransactionAt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (hasDue) BanglaNumberFormatter.formatTaka(shopkeeper.currentBakiBdt) else "পরিশোধিত",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (hasDue) BakiAmber else ProfitGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "সংশোধন করুন",
                            tint = PurchaseBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "মুছুন",
                            tint = ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (hasDue) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = onCollectClick,
                            colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "বাকি আদায় +",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickBakiCollectionSheet(
    shopkeeper: ShopkeeperAccount,
    onDismiss: () -> Unit,
    onConfirmCollection: (amountInput: String, noteBn: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("") }
    var noteBn by remember { mutableStateOf("") }

    val parsedAmount = BanglaNumberFormatter.parseBdtAmount(amountInput)
    val remainingAfter = (shopkeeper.currentBakiBdt - parsedAmount).coerceAtLeast(0L)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "বাকি আদায় করুন — ${shopkeeper.nameBn}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("বর্তমান মোট পাওনা:", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = BanglaNumberFormatter.formatTaka(shopkeeper.currentBakiBdt),
                        style = MaterialTheme.typography.titleMedium,
                        color = BakiAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("জমা দেওয়ার পর বাকি থাকবে:", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = BanglaNumberFormatter.formatTaka(remainingAfter),
                        style = MaterialTheme.typography.titleMedium,
                        color = ProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("আজ নগদ জমা দিলো (৳):", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = BanglaNumberFormatter.formatTaka(parsedAmount),
                            style = MaterialTheme.typography.headlineLarge,
                            color = ProfitGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { amountInput = shopkeeper.currentBakiBdt.toString() }
                    ) {
                        Text(
                            text = "পুরো বাকি শোধ",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            BanglaNumericKeypad(
                currentAsciiAmount = amountInput,
                onAmountChange = { amountInput = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(0.8f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("বাতিল", maxLines = 1)
                }
                Button(
                    onClick = {
                        if (parsedAmount > 0L) {
                            onConfirmCollection(amountInput, noteBn)
                        }
                    },
                    enabled = parsedAmount > 0L,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                ) {
                    Text(
                        text = "বাকি আদায় সেভ করুন",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            }

            OutlinedTextField(
                value = noteBn,
                onValueChange = { noteBn = it },
                label = { Text("নোট (ঐচ্ছিক - যেমন: বিকাশ বা নগদ ক্যাশ)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopkeeperDetailScreen(
    shopkeeperId: String,
    viewModel: BakiViewModel,
    onNavigateBack: () -> Unit
) {
    val shopkeeper by viewModel.observeShopkeeperDetail(shopkeeperId)
        .collectAsStateWithLifecycle(initialValue = null)
    val ledger by viewModel.observeShopkeeperLedger(shopkeeperId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val context = LocalContext.current
    var showCollectSheet by remember { mutableStateOf(false) }
    var showEditShopkeeperDialog by remember { mutableStateOf(false) }
    var showDeleteShopkeeperDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var deletingTransaction by remember { mutableStateOf<TransactionItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(shopkeeper?.nameBn ?: "দোকানদারের খাতা") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                },
                actions = {
                    val phone = shopkeeper?.phoneNumber.orEmpty()
                    if (phone.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "কল করুন", tint = ProfitGreen)
                        }
                    }
                    if (shopkeeper != null) {
                        IconButton(onClick = { showEditShopkeeperDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "দোকানদার সংশোধন", tint = PurchaseBlue)
                        }
                        IconButton(onClick = { showDeleteShopkeeperDialog = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "দোকানদার মুছুন", tint = ExpenseRed)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            shopkeeper?.let { acc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (acc.shopNameBn.isNotBlank()) {
                                Text(acc.shopNameBn, style = MaterialTheme.typography.titleMedium)
                            }
                            if (acc.marketAreaBn.isNotBlank()) {
                                Text("মার্কেট: ${acc.marketAreaBn}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = "বর্তমান মোট পাওনা: ${BanglaNumberFormatter.formatTaka(acc.currentBakiBdt)}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = if (acc.currentBakiBdt > 0) BakiAmber else ProfitGreen
                            )
                        }
                        if (acc.currentBakiBdt > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showCollectSheet = true },
                                colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                            ) {
                                Text("বাকি আদায় +")
                            }
                        }
                    }
                }
            }

            Text(
                text = "লেনদেন ও বাকির ইতিহাস (${BanglaNumberFormatter.toBanglaDigits(ledger.size)}টি)",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ledger, key = { it.transactionId }) { tx ->
                    TransactionRowCard(
                        item = tx,
                        onEdit = { editingTransaction = tx },
                        onDelete = { deletingTransaction = tx }
                    )
                }
            }
        }
    }

    if (showCollectSheet && shopkeeper != null) {
        QuickBakiCollectionSheet(
            shopkeeper = shopkeeper!!,
            onDismiss = { showCollectSheet = false },
            onConfirmCollection = { amountStr, note ->
                viewModel.collectBaki(
                    shopkeeperId = shopkeeperId,
                    amountInput = amountStr,
                    noteBn = note,
                    onComplete = { showCollectSheet = false }
                )
            }
        )
    }

    if (showEditShopkeeperDialog && shopkeeper != null) {
        EditShopkeeperDialog(
            shopkeeper = shopkeeper!!,
            onDismiss = { showEditShopkeeperDialog = false },
            onSave = { name, shop, market, phone, currentBaki ->
                viewModel.updateShopkeeper(
                    shopkeeperId = shopkeeperId,
                    nameBn = name,
                    shopNameBn = shop,
                    marketAreaBn = market,
                    phoneNumber = phone,
                    currentBakiInput = currentBaki
                )
                showEditShopkeeperDialog = false
            }
        )
    }

    if (showDeleteShopkeeperDialog && shopkeeper != null) {
        DeleteConfirmationDialog(
            titleBn = "দোকানদার ও তার হিসাব মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে '${shopkeeper!!.nameBn}' (বর্তমান বাকি: ${BanglaNumberFormatter.formatTaka(shopkeeper!!.currentBakiBdt)}) এবং তার সকল বাকির লেনদেন মুছে ফেলতে চান?",
            onDismiss = { showDeleteShopkeeperDialog = false },
            onConfirmDelete = {
                showDeleteShopkeeperDialog = false
                viewModel.deleteShopkeeper(shopkeeperId) {
                    onNavigateBack()
                }
            }
        )
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
            titleBn = "বাকির লেনদেনটি মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে '${deletingTransaction!!.categoryNameBn}' (${BanglaNumberFormatter.formatTaka(deletingTransaction!!.totalBillBdt)}) এর এই এন্ট্রিটি মুছে ফেলতে চান? এটি মুছে ফেললে দোকানদারের মোট বাকি স্বয়ংক্রিয়ভাবে সমন্বয় হবে।",
            onDismiss = { deletingTransaction = null },
            onConfirmDelete = {
                viewModel.deleteTransaction(deletingTransaction!!.transactionId)
                deletingTransaction = null
            }
        )
    }
}

@Composable
private fun AddShopkeeperWithOpeningBakiDialog(
    onDismiss: () -> Unit,
    onSave: (nameBn: String, shopBn: String, marketBn: String, phone: String, openingBaki: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var shop by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var openingBaki by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন দোকানদার ও পুরাতন বাকি যোগ করুন") },
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
                    label = { Text("দোকানের নাম") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = market,
                    onValueChange = { market = it },
                    label = { Text("মার্কেট / রুট") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(
                    value = openingBaki,
                    onValueChange = { openingBaki = it },
                    label = { Text("পূর্বের বকেয়া পাওনা থাকলে লিখুন (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, shop, market, phone, openingBaki)
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

@Composable
fun VendorBakiCard(
    vendor: VendorProfile,
    onPayClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val hasDue = vendor.currentBakiBdt > 0L
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasDue) PurchaseBlue.copy(alpha = 0.16f)
                            else ProfitGreen.copy(alpha = 0.16f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vendor.nameBn.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasDue) PurchaseBlue else ProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = vendor.nameBn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (vendor.marketLocationBn.isNotBlank()) {
                        Text(
                            text = "আড়ত/বাজার: ${vendor.marketLocationBn}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "মোট মাল কেনা: ${BanglaNumberFormatter.formatTaka(vendor.totalPurchasedBdt)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (hasDue) BanglaNumberFormatter.formatTaka(vendor.currentBakiBdt) else "পরিশোধিত",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (hasDue) PurchaseBlue else ProfitGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (vendor.phoneNumber.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${vendor.phoneNumber}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "কল করুন",
                                tint = ProfitGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "সংশোধন করুন",
                            tint = PurchaseBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "মুছুন",
                            tint = ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (hasDue) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = onPayClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "পরিশোধ +",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditShopkeeperDialog(
    shopkeeper: ShopkeeperAccount,
    onDismiss: () -> Unit,
    onSave: (nameBn: String, shopBn: String, marketBn: String, phone: String, currentBaki: String) -> Unit
) {
    var name by remember(shopkeeper) { mutableStateOf(shopkeeper.nameBn) }
    var shop by remember(shopkeeper) { mutableStateOf(shopkeeper.shopNameBn) }
    var market by remember(shopkeeper) { mutableStateOf(shopkeeper.marketAreaBn) }
    var phone by remember(shopkeeper) { mutableStateOf(shopkeeper.phoneNumber) }
    var currentBaki by remember(shopkeeper) { mutableStateOf(shopkeeper.currentBakiBdt.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("দোকানদারের তথ্য ও বাকি সংশোধন করুন") },
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
                    label = { Text("দোকানের নাম") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = market,
                    onValueChange = { market = it },
                    label = { Text("মার্কেট / রুট") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(
                    value = currentBaki,
                    onValueChange = { currentBaki = it },
                    label = { Text("বর্তমান মোট বকেয়া পাওনা (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, shop, market, phone, currentBaki)
                    }
                }
            ) {
                Text("সংশোধন সেভ করুন")
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
fun EditVendorDialog(
    vendor: VendorProfile,
    onDismiss: () -> Unit,
    onSave: (nameBn: String, marketBn: String, phone: String, currentBaki: String) -> Unit
) {
    var name by remember(vendor) { mutableStateOf(vendor.nameBn) }
    var market by remember(vendor) { mutableStateOf(vendor.marketLocationBn) }
    var phone by remember(vendor) { mutableStateOf(vendor.phoneNumber) }
    var currentBaki by remember(vendor) { mutableStateOf(vendor.currentBakiBdt.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("মহাজনের তথ্য ও বকেয়া সংশোধন করুন") },
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
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(
                    value = currentBaki,
                    onValueChange = { currentBaki = it },
                    label = { Text("বর্তমান মোট বকেয়া দেনা (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, market, phone, currentBaki)
                    }
                }
            ) {
                Text("সংশোধন সেভ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickVendorBakiPaySheet(
    vendor: VendorProfile,
    onDismiss: () -> Unit,
    onConfirmPayment: (amountAscii: String, noteBn: String) -> Unit
) {
    var amountInput by remember(vendor) { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    val parsedPay = BanglaNumberFormatter.parseBdtAmount(amountInput)
    val remainingBaki = (vendor.currentBakiBdt - parsedPay).coerceAtLeast(0L)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "মহাজনের বকেয়া পরিশোধ — ${vendor.nameBn}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "বর্তমান বকেয়া দেনা:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = BanglaNumberFormatter.formatTaka(vendor.currentBakiBdt),
                        style = MaterialTheme.typography.titleMedium,
                        color = PurchaseBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "পরিশোধের পর বাকি থাকবে:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = BanglaNumberFormatter.formatTaka(remainingBaki),
                        style = MaterialTheme.typography.titleMedium,
                        color = ProfitGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "আজ নগদ পরিশোধের পরিমাণ (আংশিক বা পুরো):",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(parsedPay),
                            style = MaterialTheme.typography.headlineLarge,
                            color = PurchaseBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (vendor.currentBakiBdt > 0L) {
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { amountInput = vendor.currentBakiBdt.toString() }
                        ) {
                            Text(
                                text = "পুরো দেনা শোধ",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            BanglaNumericKeypad(
                currentAsciiAmount = amountInput,
                onAmountChange = { amountInput = it }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(0.8f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("বাতিল", maxLines = 1)
                }
                Button(
                    onClick = {
                        if (parsedPay > 0L) {
                            onConfirmPayment(amountInput, noteInput.ifBlank { "মহাজনের বকেয়া পরিশোধ" })
                        }
                    },
                    enabled = parsedPay > 0L,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue)
                ) {
                    Text(
                        text = "পরিশোধ সেভ করুন",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                }
            }

            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                label = { Text("নোট (ঐচ্ছিক - যেমন: বিকাশ বা নগদ পরিশোধ)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AddVendorWithOpeningBakiDialog(
    onDismiss: () -> Unit,
    onSave: (nameBn: String, marketBn: String, phone: String, openingBaki: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var market by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var openingBaki by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন মহাজন ও পুরাতন দেনা যোগ করুন") },
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
                    label = { Text("মোবাইল নম্বর") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(
                    value = openingBaki,
                    onValueChange = { openingBaki = it },
                    label = { Text("মহাজনের পূর্বের বকেয়া পাওনা থাকলে লিখুন (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name, market, phone, openingBaki)
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
