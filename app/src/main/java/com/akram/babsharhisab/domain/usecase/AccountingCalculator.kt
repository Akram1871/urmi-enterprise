package com.akram.babsharhisab.domain.usecase

import com.akram.babsharhisab.core.common.BanglaDateUtils
import kotlin.math.max
import kotlin.math.roundToLong

data class AssetDepreciationMath(
    val monthlyDepreciationBdt: Long,
    val dailyDepreciationBdt: Long,
    val nextDueDateMillis: Long,
    val elapsedFraction: Float,
    val remainingDays: Long
)

object AccountingCalculator {

    /**
     * Calculates periodic asset maintenance metrics (e.g., 24-month vehicle battery replacement).
     */
    fun calculateAssetMetrics(
        purchaseCostBdt: Long,
        lifespanMonths: Int,
        purchaseDateMillis: Long,
        currentMillis: Long = BanglaDateUtils.nowMillis()
    ): AssetDepreciationMath {
        val safeMonths = max(1, lifespanMonths)
        val monthlyBdt = (purchaseCostBdt.toDouble() / safeMonths.toDouble()).roundToLong()
        val totalDaysInCycle = safeMonths * 30.4375
        val dailyBdt = max(1L, (purchaseCostBdt.toDouble() / totalDaysInCycle).roundToLong())
        val nextDueMillis = BanglaDateUtils.addMonthsToEpoch(purchaseDateMillis, safeMonths)

        val totalSpanMillis = max(1L, nextDueMillis - purchaseDateMillis)
        val elapsedMillis = (currentMillis - purchaseDateMillis).coerceAtLeast(0L)
        val elapsedFraction = (elapsedMillis.toFloat() / totalSpanMillis.toFloat()).coerceIn(0f, 1.5f)
        val remainingMillis = nextDueMillis - currentMillis
        val remainingDays = remainingMillis / (24L * 60L * 60L * 1000L)

        return AssetDepreciationMath(
            monthlyDepreciationBdt = monthlyBdt,
            dailyDepreciationBdt = dailyBdt,
            nextDueDateMillis = nextDueMillis,
            elapsedFraction = elapsedFraction,
            remainingDays = remainingDays
        )
    }

    /**
     * Computes split Sale & Baki accounting delta:
     * - [totalBillBdt]: Full sale value (adds to Accrued Sales Revenue)
     * - [cashReceivedBdt]: Cash paid immediately (adds to Realized Cash Inflow)
     * - Returns `bakiDeltaBdt = totalBillBdt - cashReceivedBdt`
     *   (Positive if new Baki is left with the shopkeeper; Negative if excess cash reduces previous Baki).
     */
    fun computeSaleBakiDelta(totalBillBdt: Long, cashReceivedBdt: Long): Long {
        return totalBillBdt - cashReceivedBdt
    }
}
