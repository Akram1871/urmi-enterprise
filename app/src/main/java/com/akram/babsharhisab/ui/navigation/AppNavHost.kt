package com.akram.babsharhisab.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.ui.analytics.AnalyticsScreen
import com.akram.babsharhisab.ui.analytics.AnalyticsViewModel
import com.akram.babsharhisab.ui.assets.AssetMaintenanceScreen
import com.akram.babsharhisab.ui.assets.AssetViewModel
import com.akram.babsharhisab.ui.baki.BakiListScreen
import com.akram.babsharhisab.ui.baki.BakiViewModel
import com.akram.babsharhisab.ui.baki.ShopkeeperDetailScreen
import com.akram.babsharhisab.ui.dashboard.DashboardScreen
import com.akram.babsharhisab.ui.dashboard.DashboardViewModel
import com.akram.babsharhisab.ui.settings.BackupSettingsScreen
import com.akram.babsharhisab.ui.settings.BackupSettingsViewModel
import com.akram.babsharhisab.ui.settings.VendorListScreen
import com.akram.babsharhisab.ui.theme.CatAssetColor
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.transaction.TransactionEntryScreen
import com.akram.babsharhisab.ui.transaction.TransactionListScreen
import com.akram.babsharhisab.ui.transaction.TransactionViewModel
import com.akram.babsharhisab.ui.update.AppUpdateUiState
import com.akram.babsharhisab.ui.update.AppUpdateViewModel

object AppRoutes {
    const val TAB_DASHBOARD = "tab/dashboard"
    const val TAB_BAKI = "tab/baki"
    const val TAB_ANALYTICS = "tab/analytics"
    const val TAB_MORE = "tab/more"

    const val TX_ENTRY = "transaction/entry?type={type}"
    const val TX_LIST = "transaction/list"
    const val SHOPKEEPER_DETAIL = "baki/shopkeeper/{shopkeeperId}"
    const val ASSETS_LIST = "assets/list"
    const val VENDORS_LIST = "vendors/list"
    const val BACKUP_SETTINGS = "settings/backup"

    fun txEntryRoute(type: String) = "transaction/entry?type=$type"
    fun shopkeeperDetailRoute(id: String) = "baki/shopkeeper/$id"
}

private data class BottomTabItem(
    val route: String,
    val labelBn: String,
    val icon: ImageVector
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val updateVm: AppUpdateViewModel = hiltViewModel()
    val updateState by updateVm.uiState.collectAsStateWithLifecycle()

    if (updateState.availableRelease != null) {
        AutoUpdateDialog(
            state = updateState,
            onStartUpdate = { updateVm.startDownloadAndInstall() },
            onDismiss = { updateVm.dismissUpdateDialog() }
        )
    }

    val bottomTabs = listOf(
        BottomTabItem(AppRoutes.TAB_DASHBOARD, "হোম হিসাব", Icons.Default.Home),
        BottomTabItem(AppRoutes.TAB_BAKI, "বাকি খাতা", Icons.Default.Payments),
        BottomTabItem(AppRoutes.TAB_ANALYTICS, "বিশ্লেষণ", Icons.Default.Analytics),
        BottomTabItem(AppRoutes.TAB_MORE, "সম্পদ ও শিট", Icons.Default.Widgets)
    )

    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomTabs.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.labelBn) },
                            label = {
                                Text(
                                    text = tab.labelBn,
                                    maxLines = 1,
                                    softWrap = false,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.TAB_DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.TAB_DASHBOARD) {
                val vm: DashboardViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = vm,
                    onNavigateToEntry = { mode ->
                        navController.navigate(AppRoutes.txEntryRoute(mode))
                    },
                    onNavigateToBakiTab = {
                        navController.navigate(AppRoutes.TAB_BAKI) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToAllTransactions = {
                        navController.navigate(AppRoutes.TX_LIST)
                    },
                    onNavigateToBackupSettings = {
                        navController.navigate(AppRoutes.BACKUP_SETTINGS)
                    }
                )
            }

            composable(AppRoutes.TAB_BAKI) {
                val vm: BakiViewModel = hiltViewModel()
                BakiListScreen(
                    viewModel = vm,
                    onOpenShopkeeperDetail = { shopkeeperId ->
                        navController.navigate(AppRoutes.shopkeeperDetailRoute(shopkeeperId))
                    }
                )
            }

            composable(AppRoutes.TAB_ANALYTICS) {
                val vm: AnalyticsViewModel = hiltViewModel()
                AnalyticsScreen(viewModel = vm)
            }

            composable(AppRoutes.TAB_MORE) {
                MoreHubScreen(
                    updateState = updateState,
                    onCheckForUpdate = { updateVm.checkForUpdatesManually() },
                    onOpenAssets = { navController.navigate(AppRoutes.ASSETS_LIST) },
                    onOpenVendors = { navController.navigate(AppRoutes.VENDORS_LIST) },
                    onOpenAllTransactions = { navController.navigate(AppRoutes.TX_LIST) },
                    onOpenBackupSettings = { navController.navigate(AppRoutes.BACKUP_SETTINGS) }
                )
            }

            composable(
                route = AppRoutes.TX_ENTRY,
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.StringType
                        defaultValue = "EXPENSE"
                    }
                )
            ) { entry ->
                val mode = entry.arguments?.getString("type")
                val vm: TransactionViewModel = hiltViewModel()
                TransactionEntryScreen(
                    initialMode = mode,
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.TX_LIST) {
                val vm: TransactionViewModel = hiltViewModel()
                TransactionListScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.SHOPKEEPER_DETAIL,
                arguments = listOf(navArgument("shopkeeperId") { type = NavType.StringType })
            ) { entry ->
                val shopkeeperId = entry.arguments?.getString("shopkeeperId").orEmpty()
                val vm: BakiViewModel = hiltViewModel()
                ShopkeeperDetailScreen(
                    shopkeeperId = shopkeeperId,
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.ASSETS_LIST) {
                val vm: AssetViewModel = hiltViewModel()
                AssetMaintenanceScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.VENDORS_LIST) {
                val vm: TransactionViewModel = hiltViewModel()
                VendorListScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.BACKUP_SETTINGS) {
                val vm: BackupSettingsViewModel = hiltViewModel()
                BackupSettingsScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreHubScreen(
    updateState: AppUpdateUiState,
    onCheckForUpdate: () -> Unit,
    onOpenAssets: () -> Unit,
    onOpenVendors: () -> Unit,
    onOpenAllTransactions: () -> Unit,
    onOpenBackupSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("সম্পদ, মহাজন ও গুগল শিট ব্যাকআপ") }
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
            HubMenuCard(
                titleBn = "গাড়ির ব্যাটারি ও দীর্ঘমেয়াদী সম্পদ রক্ষণাবেক্ষণ",
                subtitleBn = "২ বছর মেয়াদী ব্যাটারি, টায়ার বা বড় মেরামতের অবচয় ও পরিবর্তনের হিসাব",
                icon = Icons.Default.BatteryChargingFull,
                accentColor = CatAssetColor,
                onClick = onOpenAssets
            )
            HubMenuCard(
                titleBn = "মহাজন ও পাইকার তালিকা (Vendors)",
                subtitleBn = "এক বা একাধিক মহাজনের নাম, আড়তের ঠিকানা ও মোট মাল কেনার হিসাব",
                icon = Icons.Default.Storefront,
                accentColor = PurchaseBlue,
                onClick = onOpenVendors
            )
            HubMenuCard(
                titleBn = "সকল লেনদেনের পূর্ণাঙ্গ খাতা",
                subtitleBn = "তারিখ অনুযায়ী সকল বিক্রি, মাল কেনা ও খরচের তালিকা ও সংশোধন",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onOpenAllTransactions
            )
            HubMenuCard(
                titleBn = "স্বয়ংক্রিয় গুগল শিট ব্যাকআপ ও সেটিংস",
                subtitleBn = "প্রতিদিন ব্যাকগ্রাউন্ডে গুগল শিটে হিসাব ব্যাকআপ ও এক্সেল/CSV ডাউনলোড",
                icon = Icons.Default.CloudSync,
                accentColor = ProfitGreen,
                onClick = onOpenBackupSettings
            )
            HubMenuCard(
                titleBn = "অ্যাপ অটো-আপডেট (GitHub: akram1871) • ভার্সন ${BanglaNumberFormatter.toBanglaDigits(updateState.currentVersionName)}",
                subtitleBn = updateState.statusMessageBn
                    ?: "নতুন ভার্সন রিলিজ হলে স্বয়ংক্রিয়ভাবে ডাউনলোড ও ইনস্টল করতে এখানে চাপুন",
                icon = Icons.Default.SystemUpdate,
                accentColor = PurchaseBlue,
                onClick = onCheckForUpdate
            )
        }
    }
}

@Composable
private fun AutoUpdateDialog(
    state: AppUpdateUiState,
    onStartUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    val release = state.availableRelease ?: return
    AlertDialog(
        onDismissRequest = {
            if (!state.isDownloading) onDismiss()
        },
        title = {
            Text(
                text = "নতুন আপডেট পাওয়া গেছে! (${BanglaNumberFormatter.toBanglaDigits(release.versionTag)})",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GitHub (${release.repoFullName}) এ উর্মি এন্টারপ্রাইজের নতুন ভার্সন প্রকাশিত হয়েছে।",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (release.releaseNotes.isNotBlank()) {
                    Text(
                        text = release.releaseNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.isDownloading) {
                    LinearProgressIndicator(
                        progress = { state.downloadProgressPercent / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "ডাউনলোড হচ্ছে: ${BanglaNumberFormatter.toBanglaDigits(state.downloadProgressPercent.toString())}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = PurchaseBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (!state.statusMessageBn.isNullOrBlank()) {
                    Text(
                        text = state.statusMessageBn,
                        style = MaterialTheme.typography.labelMedium,
                        color = ProfitGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStartUpdate,
                enabled = !state.isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = PurchaseBlue)
            ) {
                Text(
                    if (state.isDownloading) "ডাউনলোড চলছে..." else "এখনই ডাউনলোড ও ইনস্টল করুন"
                )
            }
        },
        dismissButton = {
            if (!state.isDownloading) {
                TextButton(onClick = onDismiss) {
                    Text("পরে করব")
                }
            }
        }
    )
}

@Composable
private fun HubMenuCard(
    titleBn: String,
    subtitleBn: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = titleBn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitleBn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
