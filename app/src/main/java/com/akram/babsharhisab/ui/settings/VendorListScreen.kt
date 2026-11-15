package com.akram.babsharhisab.ui.settings

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.domain.model.VendorProfile
import com.akram.babsharhisab.ui.baki.EditVendorDialog
import com.akram.babsharhisab.ui.components.BanglaNumericKeypad
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.transaction.AddVendorDialog
import com.akram.babsharhisab.ui.transaction.DeleteConfirmationDialog
import com.akram.babsharhisab.ui.transaction.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorListScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val vendors by viewModel.vendors.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAddVendor by remember { mutableStateOf(false) }
    var payingVendor by remember { mutableStateOf<VendorProfile?>(null) }
    var editingVendor by remember { mutableStateOf<VendorProfile?>(null) }
    var deletingVendor by remember { mutableStateOf<VendorProfile?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("মহাজন ও পাইকার তালিকা") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddVendor = true },
                containerColor = PurchaseBlue
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "নতুন মহাজন যোগ",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        if (vendors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোনো মহাজনের নাম যুক্ত করা হয়নি। এক বা একাধিক মহাজন থেকে প্রতিদিনের মাল কেনার হিসাব রাখতে নতুন মহাজন যোগ করুন।",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vendors, key = { it.vendorId }) { vendor ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = PurchaseBlue
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = vendor.nameBn,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
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
                                        style = MaterialTheme.typography.labelLarge,
                                        color = PurchaseBlue
                                    )
                                    Text(
                                        text = "বর্তমান বকেয়া দেনা: ${BanglaNumberFormatter.formatTaka(vendor.currentBakiBdt)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (vendor.currentBakiBdt > 0L) BakiAmber else ProfitGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (vendor.currentBakiBdt > 0L) {
                                    Button(
                                        onClick = { payingVendor = vendor },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("পরিশোধ", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                                if (vendor.phoneNumber.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${vendor.phoneNumber}"))
                                            context.startActivity(intent)
                                        }
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "কল করুন", tint = ProfitGreen)
                                    }
                                }
                                IconButton(onClick = { editingVendor = vendor }) {
                                    Icon(Icons.Default.Edit, contentDescription = "সংশোধন করুন", tint = PurchaseBlue)
                                }
                                IconButton(onClick = { deletingVendor = vendor }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", tint = ExpenseRed)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }

    if (editingVendor != null) {
        EditVendorDialog(
            vendor = editingVendor!!,
            onDismiss = { editingVendor = null },
            onSave = { name, market, phone, currentBaki ->
                viewModel.updateVendorInline(
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
            titleBn = "মহাজন মুছে ফেলবেন?",
            messageBn = "আপনি কি নিশ্চিত যে মহাজন '${deletingVendor!!.nameBn}' (বর্তমান দেনা: ${BanglaNumberFormatter.formatTaka(deletingVendor!!.currentBakiBdt)}) এর তথ্য মুছে ফেলতে চান?",
            onDismiss = { deletingVendor = null },
            onConfirmDelete = {
                viewModel.deleteVendorInline(deletingVendor!!.vendorId)
                deletingVendor = null
            }
        )
    }

    if (payingVendor != null) {
        var amountStr by remember(payingVendor) { mutableStateOf(payingVendor!!.currentBakiBdt.toString()) }
        var noteStr by remember(payingVendor) { mutableStateOf("") }

        ModalBottomSheet(onDismissRequest = { payingVendor = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "মহাজনের বকেয়া দেনা পরিশোধ: ${payingVendor!!.nameBn}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "বর্তমান বকেয়া দেনা: ${BanglaNumberFormatter.formatTaka(payingVendor!!.currentBakiBdt)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = BakiAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "আজ নগদ পরিশোধ করছেন: ${BanglaNumberFormatter.formatTaka(BanglaNumberFormatter.parseBdtAmount(amountStr))}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PurchaseBlue,
                    fontWeight = FontWeight.Bold
                )
                BanglaNumericKeypad(
                    currentAsciiAmount = amountStr,
                    onAmountChange = { amountStr = it }
                )
                OutlinedTextField(
                    value = noteStr,
                    onValueChange = { noteStr = it },
                    label = { Text("নোট (ঐচ্ছিক)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { payingVendor = null },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = {
                            viewModel.payVendorBakiInline(
                                vendorId = payingVendor!!.vendorId,
                                amountInput = amountStr,
                                noteBn = noteStr.ifBlank { "মহাজনের বকেয়া পরিশোধ" },
                                onComplete = { payingVendor = null }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue)
                    ) {
                        Text("পরিশোধ সেভ করুন")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddVendor) {
        AddVendorDialog(
            onDismiss = { showAddVendor = false },
            onSave = { name, market, phone ->
                viewModel.createNewVendorInline(name, market, phone)
                showAddVendor = false
            }
        )
    }
}
