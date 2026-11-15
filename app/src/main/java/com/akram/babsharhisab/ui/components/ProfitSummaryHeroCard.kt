package com.akram.babsharhisab.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.DailySummary
import com.akram.babsharhisab.ui.theme.BakiAmber
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ExpenseRedBgLight
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.ProfitGreenBgLight
import com.akram.babsharhisab.ui.theme.PurchaseBlue
import com.akram.babsharhisab.ui.theme.parseHexColor

/**
 * Hero P&L and Real-Time Cash Flow Card (03_UI_Spec.md Section 5.1).
 */
@Composable
fun ProfitSummaryHeroCard(
    summary: DailySummary,
    includeDepreciation: Boolean,
    modifier: Modifier = Modifier
) {
    val displayedProfit = if (includeDepreciation) {
        summary.todayAdjustedProfitBdt
    } else {
        summary.todayOperationalProfitBdt
    }
    val isProfit = displayedProfit >= 0L
    val accentColor = if (isProfit) ProfitGreen else ExpenseRed
    val badgeBgColor = if (isProfit) ProfitGreenBgLight else ExpenseRedBgLight

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isProfit) "আজকের নিট লাভ (মুনাফা)" else "আজকের নিট ঘাটতি (খরচ বেশি)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = badgeBgColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isProfit) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isProfit) "লাভে আছেন" else "খরচ/মাল কেনা বেশি",
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = BanglaNumberFormatter.formatTaka(displayedProfit, showSymbol = true, showSign = true),
                style = MaterialTheme.typography.displayLarge,
                color = accentColor
            )

            if (includeDepreciation && summary.dailyAssetDepreciationBdt > 0L) {
                Text(
                    text = "দৈনিক ব্যাটারি/সম্পদ অবচয় সঞ্চয় (${BanglaNumberFormatter.formatTaka(summary.dailyAssetDepreciationBdt)}) বাদ দিয়ে হিসাবকৃত",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            // 3-Column Breakdown: মোট বিক্রি | মাল কেনা | গাড়ি ও দৈনিক খরচ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(
                    label = "মোট বিক্রি (নগদ+বাকি)",
                    amountBdt = summary.totalSalesBdt,
                    valueColor = ProfitGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricColumn(
                    label = "মাল কেনা (মহাজন)",
                    amountBdt = summary.wholesalePurchaseBdt,
                    valueColor = PurchaseBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricColumn(
                    label = "গাড়ি ও দৈনিক খরচ",
                    amountBdt = summary.operatingExpenseBdt,
                    valueColor = ExpenseRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer strip: Realized Cash Flow + Baki Collected Today
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "আজ হাতে নগদ ক্যাশ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(summary.todayNetCashFlowBdt, showSign = true),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (summary.todayNetCashFlowBdt >= 0) ProfitGreen else ExpenseRed
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "আজ পুরাতন বাকি আদায়",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = BanglaNumberFormatter.formatTaka(summary.bakiCollectedBdt),
                            style = MaterialTheme.typography.titleMedium,
                            color = BakiAmber
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    amountBdt: Long,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(end = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = BanglaNumberFormatter.formatTaka(amountBdt),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun CategoryProgressRow(
    item: CategorySpendItem,
    modifier: Modifier = Modifier
) {
    val barColor = parseHexColor(item.colorHex)
    val percentInt = (item.shareFraction * 100).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(barColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconMapper.getIcon(item.iconName),
                        contentDescription = null,
                        tint = barColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.nameBn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${BanglaNumberFormatter.toBanglaDigits(item.txCount)} টি এন্ট্রি (${BanglaNumberFormatter.toBanglaDigits(percentInt)}%)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = BanglaNumberFormatter.formatTaka(item.totalAmountBdt),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { item.shareFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
