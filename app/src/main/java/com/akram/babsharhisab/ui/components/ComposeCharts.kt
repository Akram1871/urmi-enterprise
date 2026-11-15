package com.akram.babsharhisab.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akram.babsharhisab.core.common.BanglaNumberFormatter
import com.akram.babsharhisab.domain.model.CategorySpendItem
import com.akram.babsharhisab.domain.model.CategoryTrendPoint
import com.akram.babsharhisab.domain.model.MonthlyBarPoint
import com.akram.babsharhisab.ui.theme.ExpenseRed
import com.akram.babsharhisab.ui.theme.ProfitGreen
import com.akram.babsharhisab.ui.theme.parseHexColor
import kotlin.math.max

/**
 * 1. Interactive Grouped Income vs. Expense Bar Chart (`03_UI_Spec.md` Section 5.5)
 */
@Composable
fun IncomeExpenseBarChart(
    points: List<MonthlyBarPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    var selectedIdx by remember(points) { mutableIntStateOf(points.lastIndex.coerceAtLeast(0)) }
    val selectedPoint = points.getOrNull(selectedIdx)
    val maxVal = remember(points) {
        points.maxOfOrNull { max(it.totalSalesBdt, it.totalExpenseBdt) }?.coerceAtLeast(1000L) ?: 1000L
    }
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Column(modifier = modifier.fillMaxWidth()) {
        // Structured 2-Row Tooltip Summary for Selected Month (Zero vertical text wrapping!)
        if (selectedPoint != null) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "নির্বাচিত মাস: ${selectedPoint.labelBn}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মোট আয়",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = BanglaNumberFormatter.formatCompactTaka(selectedPoint.totalSalesBdt),
                                style = MaterialTheme.typography.bodyLarge,
                                color = ProfitGreen,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "মোট ব্যয়",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = BanglaNumberFormatter.formatCompactTaka(selectedPoint.totalExpenseBdt),
                                style = MaterialTheme.typography.bodyLarge,
                                color = ExpenseRed,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "নিট লাভ/ক্ষতি",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = BanglaNumberFormatter.formatCompactTaka(selectedPoint.netProfitBdt),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedPoint.netProfitBdt >= 0) ProfitGreen else ExpenseRed,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val slotWidth = size.width / points.size.toFloat()
                        val tappedIndex = (offset.x / slotWidth).toInt().coerceIn(0, points.lastIndex)
                        selectedIdx = tappedIndex
                    }
                }
        ) {
            val chartHeight = size.height
            val slotWidth = size.width / points.size.toFloat()
            val barWidth = (slotWidth * 0.30f).coerceAtMost(26.dp.toPx())

            // Horizontal guide lines
            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { frac ->
                val y = chartHeight * frac
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            points.forEachIndexed { index, pt ->
                val centerX = slotWidth * index + slotWidth / 2f
                val salesHeight = (pt.totalSalesBdt.toFloat() / maxVal.toFloat()) * (chartHeight * 0.88f)
                val expHeight = (pt.totalExpenseBdt.toFloat() / maxVal.toFloat()) * (chartHeight * 0.88f)

                if (index == selectedIdx) {
                    drawRoundRect(
                        color = gridColor.copy(alpha = 0.25f),
                        topLeft = Offset(slotWidth * index + 4f, 0f),
                        size = Size(slotWidth - 8f, chartHeight),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                }

                // Income Bar (Emerald)
                val salesTop = chartHeight - salesHeight.coerceAtLeast(4f)
                drawRoundRect(
                    color = ProfitGreen,
                    topLeft = Offset(centerX - barWidth - 2.dp.toPx(), salesTop),
                    size = Size(barWidth, salesHeight.coerceAtLeast(4f)),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                // Expense Bar (Red)
                val expTop = chartHeight - expHeight.coerceAtLeast(4f)
                drawRoundRect(
                    color = ExpenseRed,
                    topLeft = Offset(centerX + 2.dp.toPx(), expTop),
                    size = Size(barWidth, expHeight.coerceAtLeast(4f)),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            points.forEachIndexed { index, pt ->
                Text(
                    text = pt.labelBn,
                    fontSize = 11.sp,
                    fontWeight = if (index == selectedIdx) FontWeight.Bold else FontWeight.Normal,
                    color = if (index == selectedIdx) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 2. Category Breakdown Donut Chart (`03_UI_Spec.md` Section 5.5)
 */
@Composable
fun CategoryBreakdownDonutChart(
    items: List<CategorySpendItem>,
    modifier: Modifier = Modifier
) {
    val totalSpend = remember(items) { items.sumOf { it.totalAmountBdt } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(145.dp),
            contentAlignment = Alignment.Center
        ) {
            val emptyTrackColor = MaterialTheme.colorScheme.surfaceVariant
            Canvas(modifier = Modifier.size(130.dp)) {
                val strokeWidth = 24.dp.toPx()
                if (items.isEmpty() || totalSpend <= 0L) {
                    drawArc(
                        color = emptyTrackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    var startAngle = -90f
                    items.forEach { item ->
                        val sweep = (item.shareFraction * 360f).coerceAtLeast(2f)
                        drawArc(
                            color = parseHexColor(item.colorHex),
                            startAngle = startAngle,
                            sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "মোট খরচ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = BanglaNumberFormatter.formatCompactTaka(totalSpend),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (items.isEmpty()) {
                Text(
                    text = "এই সময়ে কোনো খরচের হিসাব নেই",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.take(5).forEach { item ->
                    val pct = (item.shareFraction * 100).toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(item.colorHex))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.nameBn,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${BanglaNumberFormatter.toBanglaDigits(pct)}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Multi-Month Category Trend Line Chart (`03_UI_Spec.md` Section 5.5)
 */
@Composable
fun CategoryTrendLineChart(
    points: List<CategoryTrendPoint>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    val maxVal = remember(points) {
        points.maxOfOrNull { it.amountBdt }?.coerceAtLeast(500L) ?: 500L
    }
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            val w = size.width
            val h = size.height
            val stepX = if (points.size > 1) w / (points.size - 1).toFloat() else w / 2f

            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { frac ->
                drawLine(
                    color = gridColor,
                    start = Offset(0f, h * frac),
                    end = Offset(w, h * frac),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val offsets = points.mapIndexed { idx, pt ->
                val x = if (points.size > 1) idx * stepX else w / 2f
                val normalized = (pt.amountBdt.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                val y = h - (normalized * (h * 0.85f)) - 4.dp.toPx()
                Offset(x, y)
            }

            if (offsets.size >= 2) {
                val linePath = Path().apply {
                    moveTo(offsets.first().x, offsets.first().y)
                    for (i in 1 until offsets.size) {
                        lineTo(offsets[i].x, offsets[i].y)
                    }
                }
                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(offsets.last().x, h)
                    lineTo(offsets.first().x, h)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent),
                        startY = 0f,
                        endY = h
                    )
                )
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            offsets.forEach { center ->
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = lineColor,
                    radius = 3.5.dp.toPx(),
                    center = center
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            points.forEach { pt ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = BanglaNumberFormatter.formatCompactTaka(pt.amountBdt, showSymbol = false),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = lineColor,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = pt.labelBn,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
