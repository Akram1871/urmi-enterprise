package com.akram.babsharhisab.domain.usecase

import com.akram.babsharhisab.core.common.BanglaDateUtils
import com.akram.babsharhisab.domain.model.DailySummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AccountingAndBakiMathTest {

    @Test
    fun `split Sale with Baki separates Accrued Sales Revenue from Realized Cash Flow`() {
        // Trader sells 10,000 BDT to a shopkeeper: 6,000 Cash + 4,000 Baki
        val totalBill = 10_000L
        val cashReceived = 6_000L
        val bakiDelta = AccountingCalculator.computeSaleBakiDelta(totalBill, cashReceived)
        assertEquals(4_000L, bakiDelta)

        // Later on the same day, another shopkeeper pays 2,500 BDT of previous Baki (BAKI_COLLECTION),
        // and the trader spends 5,500 BDT on wholesale purchases and 800 BDT on vehicle fuel.
        val summary = DailySummary(
            dateKey = "2026-11-13",
            totalSalesBdt = 10_000L,           // Only SALE counts toward Sales Revenue
            cashSalesBdt = 6_000L,             // Cash portion of today's sale
            newBakiGivenBdt = 4_000L,          // Uncollected portion of today's sale
            bakiCollectedBdt = 2_500L,         // Previous Baki collected (NOT added to totalSalesBdt!)
            wholesalePurchaseBdt = 5_500L,     // Multi-vendor purchases
            operatingExpenseBdt = 800L,        // Fuel/transport/food/repair
            assetMaintenanceBdt = 0L,
            dailyAssetDepreciationBdt = 33L,   // 24,000 BDT battery over 24 months (~33 BDT/day)
            totalActiveMarketBakiBdt = 15_000L,
            activeBakiShopkeeperCount = 4
        )

        // 1. Operational Net Profit = Total Sales (10,000) - Wholesale (5,500) - Ops Expense (800) = 3,700
        assertEquals(3_700L, summary.todayOperationalProfitBdt)

        // 2. Adjusted Net Profit after Daily Battery Depreciation (3,700 - 33 = 3,667)
        assertEquals(3_667L, summary.todayAdjustedProfitBdt)

        // 3. Today's Realized Cash Inflow = Cash Sales (6,000) + Baki Collected (2,500) = 8,500
        assertEquals(8_500L, summary.todayCashInflowBdt)

        // 4. Today's Cash Outflow = Wholesale (5,500) + Ops Expense (800) = 6,300
        assertEquals(6_300L, summary.todayCashOutflowBdt)

        // 5. Net Cash in Hand Today = 8,500 - 6,300 = +2,200
        assertEquals(2_200L, summary.todayNetCashFlowBdt)
    }

    @Test
    fun `24-month vehicle battery asset depreciation calculates monthly and daily reserves`() {
        val installDate = LocalDate.of(2025, 11, 13)
        val currentDate = LocalDate.of(2026, 11, 13) // 12 months (50%) elapsed
        val installMillis = BanglaDateUtils.localDateToNoonMillis(installDate)
        val currentMillis = BanglaDateUtils.localDateToNoonMillis(currentDate)

        val metrics = AccountingCalculator.calculateAssetMetrics(
            purchaseCostBdt = 24_000L,
            lifespanMonths = 24,
            purchaseDateMillis = installMillis,
            currentMillis = currentMillis
        )

        // 24,000 BDT / 24 months = 1,000 BDT/month
        assertEquals(1_000L, metrics.monthlyDepreciationBdt)
        // 24,000 BDT / ~730.5 days = 33 BDT/day
        assertEquals(33L, metrics.dailyDepreciationBdt)
        // ~50% lifecycle elapsed
        assertTrue(metrics.elapsedFraction in 0.48f..0.52f)
        // ~365 days remaining
        assertTrue(metrics.remainingDays in 360L..366L)
    }

    @Test
    fun `TransactionFormState isolates amounts across Purchase, Expense categories, and Sale`() {
        val state = com.akram.babsharhisab.ui.transaction.TransactionFormState(
            mode = com.akram.babsharhisab.ui.transaction.EntryMode.PURCHASE,
            purchaseAmountInput = "5000",
            expenseAmountByCategory = mapOf(
                "CAT_FUEL" to "600",
                "CAT_TRANSPORT" to "350"
            ),
            saleTotalBillInput = "8500"
        )

        // In PURCHASE mode, parsedTotalBdt is 5000
        assertEquals(5000L, state.parsedTotalBdt)

        // Switching to EXPENSE (CAT_FUEL) shows 600, NOT 5000
        val fuelState = state.copy(
            mode = com.akram.babsharhisab.ui.transaction.EntryMode.EXPENSE,
            selectedCategoryId = "CAT_FUEL"
        )
        assertEquals(600L, fuelState.parsedTotalBdt)

        // Switching to EXPENSE (CAT_TRANSPORT) shows 350
        val transportState = fuelState.copy(selectedCategoryId = "CAT_TRANSPORT")
        assertEquals(350L, transportState.parsedTotalBdt)

        // Switching to EXPENSE (CAT_FOOD - untouched) shows 0
        val foodState = fuelState.copy(selectedCategoryId = "CAT_FOOD")
        assertEquals(0L, foodState.parsedTotalBdt)

        // Switching to SALE shows 8500
        val saleState = state.copy(mode = com.akram.babsharhisab.ui.transaction.EntryMode.SALE)
        assertEquals(8500L, saleState.parsedTotalBdt)

        // Switching within SALE to BAKI_COLLECT isolates collection input from sale bill
        val collectState = saleState.copy(
            saleSubMode = com.akram.babsharhisab.ui.transaction.SaleSubMode.BAKI_COLLECT,
            bakiCollectAmountInput = "3200"
        )
        assertEquals(3200L, collectState.parsedTotalBdt)
        assertEquals(8500L, collectState.copy(
            saleSubMode = com.akram.babsharhisab.ui.transaction.SaleSubMode.SHOPKEEPER_SALE
        ).parsedTotalBdt)
    }

    @Test
    fun `multi-shopkeeper Baki sales and multi-shopkeeper Baki collections in a single day calculate accurately`() {
        // Scenario:
        // 1. Trader sells on Baki to Shopkeeper A (Bill: 4,000, Cash: 1,000 -> New Baki: 3,000)
        // 2. Trader sells on Baki to Shopkeeper B (Bill: 6,000, Cash: 0 -> New Baki: 6,000)
        // 3. Trader collects previous Baki from Shopkeeper C (Collected: 2,000)
        // 4. Trader collects previous Baki from Shopkeeper D (Collected: 3,500)
        val bakiSaleA = AccountingCalculator.computeSaleBakiDelta(4_000L, 1_000L)
        val bakiSaleB = AccountingCalculator.computeSaleBakiDelta(6_000L, 0L)
        assertEquals(3_000L, bakiSaleA)
        assertEquals(6_000L, bakiSaleB)

        val summary = DailySummary(
            dateKey = "2026-11-13",
            totalSalesBdt = 4_000L + 6_000L,          // 10,000 total sales today
            cashSalesBdt = 1_000L + 0L,               // 1,000 cash received from today's sales
            newBakiGivenBdt = bakiSaleA + bakiSaleB,  // 9,000 new Baki given across Shopkeepers A & B
            bakiCollectedBdt = 2_000L + 3_500L,       // 5,500 collected from Shopkeepers C & D
            wholesalePurchaseBdt = 5_000L,
            operatingExpenseBdt = 500L,
            assetMaintenanceBdt = 0L,
            dailyAssetDepreciationBdt = 0L,
            totalActiveMarketBakiBdt = 20_000L,
            activeBakiShopkeeperCount = 4
        )

        // Operational Profit = 10,000 (Sales) - 5,000 (Purchase) - 500 (Expense) = 4,500
        assertEquals(4_500L, summary.todayOperationalProfitBdt)
        // Cash Inflow Today = 1,000 (Cash from today's sales) + 5,500 (Previous Baki collected) = 6,500
        assertEquals(6_500L, summary.todayCashInflowBdt)
        // Net Cash in Hand Today = 6,500 (Cash In) - 5,500 (Cash Out) = +1,000
        assertEquals(1_000L, summary.todayNetCashFlowBdt)
    }

    @Test
    fun `Mahajan split Nagad plus Baki purchase and later Mahajan Baki payment calculate accurately`() {
        // Scenario:
        // 1. Trader buys 20,000 BDT goods from Mahajan A: pays 6,000 Nagad + 14,000 Mahajan Baki
        // 2. Trader pays 4,000 BDT to Mahajan B for a previous day's Baki
        // 3. Trader sells 25,000 BDT in Cash and spends 1,000 BDT on Fuel
        val formState = com.akram.babsharhisab.ui.transaction.TransactionFormState(
            mode = com.akram.babsharhisab.ui.transaction.EntryMode.PURCHASE,
            purchaseSubMode = com.akram.babsharhisab.ui.transaction.PurchaseSubMode.VENDOR_BAKI_PURCHASE,
            purchaseAmountInput = "20000",
            purchaseCashPaidInput = "6000"
        )
        assertEquals(20_000L, formState.parsedTotalBdt)
        assertEquals(6_000L, formState.parsedPurchaseCashPaidBdt)
        assertEquals(14_000L, formState.calculatedNewVendorBakiBdt)

        val summary = DailySummary(
            dateKey = "2026-11-13",
            totalSalesBdt = 25_000L,
            cashSalesBdt = 25_000L,
            wholesalePurchaseBdt = 20_000L,    // Full 20,000 counts as cost of goods bought today
            cashPurchaseBdt = 6_000L,          // Only 6,000 cash left hand for today's purchase
            newVendorBakiBdt = 14_000L,        // 14,000 owed to Mahajan A
            vendorBakiPaidBdt = 4_000L,        // 4,000 cash paid today to Mahajan B for old Baki
            operatingExpenseBdt = 1_000L
        )

        // Operational Profit = 25,000 (Sales) - 20,000 (Wholesale Purchase) - 1,000 (Fuel) = 4,000
        assertEquals(4_000L, summary.todayOperationalProfitBdt)
        // Total Cash Outflow = 6,000 (Nagad Purchase) + 4,000 (Old Mahajan Baki Paid) + 1,000 (Fuel) = 11,000
        assertEquals(11_000L, summary.todayCashOutflowBdt)
        // Net Cash in Hand Today = 25,000 (Cash In) - 11,000 (Cash Out) = +14,000
        assertEquals(14_000L, summary.todayNetCashFlowBdt)
    }

    @Test
    fun `Personal Home Expense is tracked separately and strictly excluded from business Profit and Loss`() {
        // Trader makes 15,000 BDT sales, spends 9,000 BDT on goods, 1,000 BDT on vehicle fuel,
        // and spends 2,500 BDT today on Personal/Home Expense (e.g., family groceries/utility).
        val summary = DailySummary(
            dateKey = "2026-11-13",
            totalSalesBdt = 15_000L,
            cashSalesBdt = 15_000L,
            wholesalePurchaseBdt = 9_000L,
            cashPurchaseBdt = 9_000L,
            operatingExpenseBdt = 1_000L,      // Only business expenses
            personalHomeExpenseBdt = 2_500L,   // Personal/home spending today
            monthlyHomeExpenseBdt = 18_500L    // Total home spending in current month
        )

        // Business Operational Profit = 15,000 - 9,000 - 1,000 = 5,000 (Home expense 2,500 is NOT deducted!)
        assertEquals(5_000L, summary.todayOperationalProfitBdt)
        assertEquals(5_000L, summary.todayAdjustedProfitBdt)

        // Business Cash Outflow = 9,000 + 1,000 = 10,000 (strictly business cash flow)
        assertEquals(10_000L, summary.todayCashOutflowBdt)

        // Home spending is tracked accurately on its dedicated fields
        assertEquals(2_500L, summary.personalHomeExpenseBdt)
        assertEquals(18_500L, summary.monthlyHomeExpenseBdt)
    }
}
