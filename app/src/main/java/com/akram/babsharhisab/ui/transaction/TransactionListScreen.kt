package com.akram.babsharhisab.ui.transaction

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.data.local.entity.TxType
import com.akram.babsharhisab.domain.model.TransactionItem
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    var editingTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var deletingTransaction by remember { mutableStateOf<TransactionItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সকল লেনদেনের খাতা") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "এখনো কোনো লেনদেন রেকর্ড করা হয়নি।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.transactionId }) { tx ->
                    TransactionRowCard(
                        item = tx,
                        onEdit = { editingTransaction = tx },
                        onDelete = { deletingTransaction = tx }
                    )
                }
            }
        }
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
            messageBn = "আপনি কি নিশ্চিত যে '${deletingTransaction!!.categoryNameBn}' (${BanglaNumberFormatter.formatTaka(deletingTransaction!!.totalBillBdt)}) এর এই হিসাবটি মুছে ফেলতে চান? এটি মুছে ফেললে সংশ্লিষ্ট বাকি ও আজকের হিসাব স্বয়ংক্রিয়ভাবে সমন্বয় হবে।",
            onDismiss = { deletingTransaction = null },
            onConfirmDelete = {
                viewModel.deleteTransaction(deletingTransaction!!.transactionId)
                deletingTransaction = null
            }
        )
    }
}

@Composable
fun TransactionRowCard(
    item: TransactionItem,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val badgeColor = parseHexColor(item.categoryColorHex)
    val amountColor = when (item.txType) {
        TxType.SALE -> ProfitGreen
        TxType.BAKI_COLLECTION -> BakiAmber
        TxType.WHOLESALE_PURCHASE -> PurchaseBlue
        else -> ExpenseRed
    }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.categoryNameBn.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.categoryNameBn,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    val partyText = buildString {
                        if (!item.vendorNameBn.isNullOrBlank()) append("মহাজন: ${item.vendorNameBn} • ")
                        if (!item.shopkeeperNameBn.isNullOrBlank()) append("দোকানদার: ${item.shopkeeperNameBn} • ")
                        if (item.noteBn.isNotBlank()) append(item.noteBn)
                    }
                    if (partyText.isNotBlank()) {
                        Text(
                            text = partyText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = BanglaDateUtils.formatBanglaDateTime(item.eventDateMillis),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    val prefix = when (item.txType) {
                        TxType.SALE, TxType.BAKI_COLLECTION -> "+ "
                        else -> "- "
                    }
                    Text(
                        text = prefix + BanglaNumberFormatter.formatTaka(item.totalBillBdt),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                    if ((item.txType == TxType.SALE || item.txType == TxType.WHOLESALE_PURCHASE) && item.bakiDeltaBdt > 0L) {
                        Text(
                            text = "নগদ: ${BanglaNumberFormatter.formatTaka(item.cashAmountBdt)} | বাকি: ${BanglaNumberFormatter.formatTaka(item.bakiDeltaBdt)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = BakiAmber
                        )
                    }
                }
                if (onEdit != null) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "সংশোধন করুন",
                            tint = PurchaseBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "মুছে ফেলুন",
                            tint = ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditTransactionDialog(
    item: TransactionItem,
    onDismiss: () -> Unit,
    onSave: (totalBillInput: String, cashAmountInput: String, noteBn: String) -> Unit
) {
    val isSplitPartyTx = (item.txType == TxType.SALE && !item.shopkeeperId.isNullOrBlank()) ||
        (item.txType == TxType.WHOLESALE_PURCHASE && !item.vendorId.isNullOrBlank())

    var totalBillStr by remember(item) { mutableStateOf(item.totalBillBdt.toString()) }
    var cashStr by remember(item) { mutableStateOf(item.cashAmountBdt.toString()) }
    var noteStr by remember(item) { mutableStateOf(item.noteBn) }

    val parsedBill = BanglaNumberFormatter.parseBdtAmount(totalBillStr)
    val parsedCash = if (isSplitPartyTx) {
        BanglaNumberFormatter.parseBdtAmount(cashStr).coerceIn(0L, parsedBill)
    } else {
        parsedBill
    }
    val calculatedBaki = (parsedBill - parsedCash).coerceAtLeast(0L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("হিসাব সংশোধন করুন — ${item.categoryNameBn}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!item.shopkeeperNameBn.isNullOrBlank()) {
                    Text(
                        text = "দোকানদার: ${item.shopkeeperNameBn}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BakiAmber
                    )
                }
                if (!item.vendorNameBn.isNullOrBlank()) {
                    Text(
                        text = "মহাজন: ${item.vendorNameBn}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PurchaseBlue
                    )
                }
                OutlinedTextField(
                    value = totalBillStr,
                    onValueChange = { totalBillStr = it },
                    label = {
                        Text(
                            when (item.txType) {
                                TxType.BAKI_COLLECTION -> "আদায়কৃত টাকার পরিমাণ (৳) *"
                                TxType.VENDOR_BAKI_PAYMENT -> "পরিশোধকৃত টাকার পরিমাণ (৳) *"
                                else -> "মোট বিল / টাকার পরিমাণ (৳) *"
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isSplitPartyTx) {
                    OutlinedTextField(
                        value = cashStr,
                        onValueChange = { cashStr = it },
                        label = { Text("নগদ অংশ (৳) — বাকি সব বকেয়া খাতায় যাবে") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "নতুন বকেয়া অংশ হবে: ${BanglaNumberFormatter.formatTaka(calculatedBaki)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = BakiAmber,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    label = { Text("বিবরণ বা নোট") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedBill > 0L) {
                        onSave(totalBillStr, if (isSplitPartyTx) cashStr else totalBillStr, noteStr)
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
fun DeleteConfirmationDialog(
    titleBn: String,
    messageBn: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                tint = ExpenseRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = titleBn,
                color = ExpenseRed,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = messageBn,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
            ) {
                Text("হ্যাঁ, মুছে ফেলুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("না, থাকুক")
            }
        }
    )
}
