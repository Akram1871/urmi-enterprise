package com.akram.babsharhisab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akram.babsharhisab.core.common.BanglaNumberFormatter

/**
 * High-speed thumb-friendly Bangla Numeric Keypad (`০-৯`, `০০`, `০০০`, `C`, Backspace)
 * designed for 1-handed field entry by traders.
 */
@Composable
fun BanglaNumericKeypad(
    currentAsciiAmount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("00", "0", "BACKSPACE")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick denomination chips (+১০০, +৫০০, +১০০০, মুছুন)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(100L to "+ ৳১০০", 500L to "+ ৳৫০০", 1000L to "+ ৳১,০০০").forEach { (addVal, label) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val current = BanglaNumberFormatter.parseBdtAmount(currentAsciiAmount)
                            val next = (current + addVal).coerceAtMost(99_99_99_999L)
                            onAmountChange(next.toString())
                        },
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .weight(0.8f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAmountChange("") },
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "সব মুছুন",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeys.forEach { key ->
                    val isBackspace = key == "BACKSPACE"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isBackspace) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable {
                                val normalized = BanglaNumberFormatter.toAsciiDigits(currentAsciiAmount)
                                when (key) {
                                    "BACKSPACE" -> {
                                        if (normalized.isNotEmpty()) {
                                            onAmountChange(normalized.dropLast(1))
                                        }
                                    }
                                    "0", "00" -> {
                                        if (normalized.isNotEmpty() && normalized != "0" && normalized.length + key.length <= 9) {
                                            onAmountChange(normalized + key)
                                        } else if (normalized.isEmpty()) {
                                            onAmountChange("0")
                                        }
                                    }
                                    else -> {
                                        val next = if (normalized == "0") key else normalized + key
                                        if (next.length <= 9) {
                                            onAmountChange(next)
                                        }
                                    }
                                }
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBackspace) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "মুছুন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = BanglaNumberFormatter.toBanglaDigits(key),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
