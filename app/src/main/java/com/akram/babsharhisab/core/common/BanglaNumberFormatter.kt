package com.akram.babsharhisab.core.common

import kotlin.math.abs

/**
 * Central Bangla numeral (`০-৯`) and Bangladeshi Taka (`৳`) formatting utility.
 * Enforces South Asian Lakh/Crore (`১,২৫,০০০`) comma placement and bidirectional
 * Bangla <-> ASCII digit normalization for user inputs.
 */
object BanglaNumberFormatter {

    private val asciiToBanglaMap = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    /**
     * Converts any ASCII digits ('0'..'9') in [input] to Bangla digits ('০'..'৯').
     */
    fun toBanglaDigits(input: String): String {
        if (input.isEmpty()) return input
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(asciiToBanglaMap[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toBanglaDigits(value: Long): String = toBanglaDigits(value.toString())

    fun toBanglaDigits(value: Int): String = toBanglaDigits(value.toString())

    /**
     * Converts any Bangla digits ('০'..'৯') in [input] to ASCII digits ('0'..'9')
     * and strips commas/spaces/currency symbols so user input can be parsed cleanly.
     */
    fun toAsciiDigits(input: String): String {
        if (input.isEmpty()) return ""
        val sb = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                in '০'..'৯' -> sb.append(('0'.code + (ch.code - '০'.code)).toChar())
                in '0'..'9' -> sb.append(ch)
                '-' -> if (sb.isEmpty()) sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Parses a user-entered string containing Bangla or English digits into a [Long] BDT amount.
     * Returns 0L if empty or invalid.
     */
    fun parseBdtAmount(input: String): Long {
        val cleaned = toAsciiDigits(input.trim())
        if (cleaned.isEmpty() || cleaned == "-") return 0L
        return cleaned.toLongOrNull() ?: 0L
    }

    /**
     * Formats an integer/long amount using South Asian (Lakh/Crore) grouping in Bangla digits.
     * Example: 125000 -> "১,২৫,০০০"
     */
    fun formatSouthAsianNumber(amount: Long): String {
        val isNegative = amount < 0
        val absStr = if (amount == Long.MIN_VALUE) "9223372036854775808" else abs(amount).toString()

        if (absStr.length <= 3) {
            val bangla = toBanglaDigits(absStr)
            return if (isNegative) "-$bangla" else bangla
        }

        val lastThree = absStr.substring(absStr.length - 3)
        val remaining = absStr.substring(0, absStr.length - 3)

        val groupedRemaining = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            groupedRemaining.append(remaining[i])
            count++
            if (count == 2 && i != 0) {
                groupedRemaining.append(',')
                count = 0
            }
        }
        val formattedAscii = "${groupedRemaining.reverse()},$lastThree"
        val formattedBangla = toBanglaDigits(formattedAscii)
        return if (isNegative) "-$formattedBangla" else formattedBangla
    }

    /**
     * Formats a BDT amount with Bangladeshi Taka symbol (`৳`) and South Asian commas.
     * Example: 125500 -> "৳ ১,২৫,৫০০"
     * Example with [showSign] = true: 2450 -> "+ ৳ ২,৪৫০", -1200 -> "- ৳ ১,২০০"
     */
    fun formatTaka(
        amountBdt: Long,
        showSymbol: Boolean = true,
        showSign: Boolean = false
    ): String {
        val isNegative = amountBdt < 0
        val absValue = if (amountBdt == Long.MIN_VALUE) Long.MAX_VALUE else abs(amountBdt)
        val numberPart = formatSouthAsianNumber(absValue)
        val symbolPart = if (showSymbol) "৳ " else ""

        return when {
            isNegative -> "- $symbolPart$numberPart"
            showSign && amountBdt > 0 -> "+ $symbolPart$numberPart"
            else -> "$symbolPart$numberPart"
        }
    }

    /**
     * Compact Bangla currency formatter for chart Y-axes and badges.
     * Example: 5000 -> "৳ ৫হাজার", 150000 -> "৳ ১.৫লাখ", 10000000 -> "৳ ১কোটি"
     */
    fun formatCompactTaka(amountBdt: Long, showSymbol: Boolean = true): String {
        val isNegative = amountBdt < 0
        val absVal = abs(amountBdt)
        val prefix = buildString {
            if (isNegative) append("-")
            if (showSymbol) append("৳")
        }

        val formatted = when {
            absVal >= 1_00_00_000L -> {
                val crore = absVal / 1_00_00_000.0
                val str = if (crore % 1.0 == 0.0) crore.toLong().toString() else String.format(java.util.Locale.US, "%.1f", crore)
                "${toBanglaDigits(str)}কোটি"
            }
            absVal >= 1_00_000L -> {
                val lakh = absVal / 1_00_000.0
                val str = if (lakh % 1.0 == 0.0) lakh.toLong().toString() else String.format(java.util.Locale.US, "%.1f", lakh)
                "${toBanglaDigits(str)}লাখ"
            }
            absVal >= 1_000L -> {
                val thousand = absVal / 1_000.0
                val str = if (thousand % 1.0 == 0.0) thousand.toLong().toString() else String.format(java.util.Locale.US, "%.1f", thousand)
                "${toBanglaDigits(str)}হাজার"
            }
            else -> toBanglaDigits(absVal)
        }
        return if (prefix.isEmpty()) formatted else "$prefix$formatted"
    }
}
