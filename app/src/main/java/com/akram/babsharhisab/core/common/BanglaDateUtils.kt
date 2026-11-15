package com.akram.babsharhisab.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Date & Time utilities locked to Bangladesh Standard Time (`Asia/Dhaka`, UTC+6).
 * Generates deterministic `date_key` (YYYY-MM-DD) and `year_month` (YYYY-MM) keys for SQLite indexes
 * and human-friendly Bangla dates (`আজ, ১৩ নভেম্বর ২০২৬`).
 */
object BanglaDateUtils {

    val DHAKA_ZONE: ZoneId = ZoneId.of("Asia/Dhaka")

    private val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
    private val isoYearMonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.US)

    private val banglaMonthNames = arrayOf(
        "জানুয়ারি",
        "ফেব্রুয়ারি",
        "মার্চ",
        "এপ্রিল",
        "মে",
        "জুন",
        "জুলাই",
        "আগস্ট",
        "সেপ্টেম্বর",
        "অক্টোবর",
        "নভেম্বর",
        "ডিসেম্বর"
    )

    private val banglaShortMonthNames = arrayOf(
        "জানু",
        "ফেব্রু",
        "মার্চ",
        "এপ্রি",
        "মে",
        "জুন",
        "জুলা",
        "আগ",
        "সেপ্টে",
        "অক্টো",
        "নভে",
        "ডিসে"
    )

    private val banglaDayOfWeekNames = mapOf(
        1 to "সোমবার",
        2 to "মঙ্গলবার",
        3 to "বুধবার",
        4 to "বৃহস্পতিবার",
        5 to "শুক্রবার",
        6 to "শনিবার",
        7 to "রবিবার"
    )

    fun nowMillis(): Long = System.currentTimeMillis()

    fun todayLocalDate(): LocalDate = LocalDate.now(DHAKA_ZONE)

    fun millisToLocalDate(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(DHAKA_ZONE).toLocalDate()

    fun localDateToNoonMillis(date: LocalDate): Long =
        date.atTime(12, 0).atZone(DHAKA_ZONE).toInstant().toEpochMilli()

    /**
     * Returns indexed SQLite `date_key` in ASCII `YYYY-MM-DD` format for a given timestamp.
     */
    fun toDateKey(epochMillis: Long): String =
        millisToLocalDate(epochMillis).format(isoDateFormatter)

    fun toDateKey(date: LocalDate): String = date.format(isoDateFormatter)

    /**
     * Returns indexed SQLite `year_month` in ASCII `YYYY-MM` format for a given timestamp.
     */
    fun toYearMonthKey(epochMillis: Long): String =
        millisToLocalDate(epochMillis).format(isoYearMonthFormatter)

    fun toYearMonthKey(yearMonth: YearMonth): String =
        yearMonth.format(isoYearMonthFormatter)

    /**
     * Formats a [LocalDate] into full Bangla representation.
     * Example: "আজ, ১৩ নভেম্বর ২০২৬" or "গতকাল, ১২ নভেম্বর ২০২৬" or "১০ জানুয়ারি ২০২৬"
     */
    fun formatFullBanglaDate(date: LocalDate, includeRelativePrefix: Boolean = true): String {
        val today = todayLocalDate()
        val dayStr = BanglaNumberFormatter.toBanglaDigits(date.dayOfMonth)
        val monthStr = banglaMonthNames[date.monthValue - 1]
        val yearStr = BanglaNumberFormatter.toBanglaDigits(date.year)
        val dayOfWeek = banglaDayOfWeekNames[date.dayOfWeek.value] ?: ""

        val baseDate = "$dayStr $monthStr $yearStr"
        if (!includeRelativePrefix) return baseDate

        return when (date) {
            today -> "আজ, $baseDate ($dayOfWeek)"
            today.minusDays(1) -> "গতকাল, $baseDate"
            today.plusDays(1) -> "আগামীকাল, $baseDate"
            else -> "$baseDate ($dayOfWeek)"
        }
    }

    fun formatShortBanglaDate(epochMillis: Long): String {
        val date = millisToLocalDate(epochMillis)
        val dayStr = BanglaNumberFormatter.toBanglaDigits(date.dayOfMonth)
        val monthStr = banglaShortMonthNames[date.monthValue - 1]
        val yearStr = BanglaNumberFormatter.toBanglaDigits(date.year)
        return "$dayStr $monthStr $yearStr"
    }

    fun formatBanglaDateTime(epochMillis: Long): String {
        val zdt = Instant.ofEpochMilli(epochMillis).atZone(DHAKA_ZONE)
        val datePart = formatShortBanglaDate(epochMillis)
        val hour24 = zdt.hour
        val minute = zdt.minute
        val period = when {
            hour24 in 5..11 -> "সকাল"
            hour24 in 12..15 -> "দুপুর"
            hour24 in 16..18 -> "বিকাল"
            else -> "রাত"
        }
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val hourStr = BanglaNumberFormatter.toBanglaDigits(hour12)
        val minStr = BanglaNumberFormatter.toBanglaDigits(minute.toString().padStart(2, '0'))
        return "$datePart, $period $hourStr:$minStr"
    }

    /**
     * Converts a `YYYY-MM` key into a Bangla month-year label (e.g., "নভেম্বর ২০২৬" or "নভে ২৬").
     */
    fun formatYearMonthBangla(yearMonthKey: String, short: Boolean = false): String {
        val parts = yearMonthKey.split("-")
        if (parts.size != 2) return yearMonthKey
        val year = parts[0].toIntOrNull() ?: return yearMonthKey
        val month = parts[1].toIntOrNull()?.coerceIn(1, 12) ?: return yearMonthKey
        return if (short) {
            val shortYear = BanglaNumberFormatter.toBanglaDigits(year % 100)
            "${banglaShortMonthNames[month - 1]}'$shortYear"
        } else {
            "${banglaMonthNames[month - 1]} ${BanglaNumberFormatter.toBanglaDigits(year)}"
        }
    }

    /**
     * Returns a relative Bangla age label for shopkeeper dues (e.g., "আজ", "৩ দিন আগে").
     */
    fun formatRelativeDaysAgo(epochMillis: Long): String {
        if (epochMillis <= 0L) return "কোনো লেনদেন নেই"
        val txDate = millisToLocalDate(epochMillis)
        val today = todayLocalDate()
        val days = ChronoUnit.DAYS.between(txDate, today)
        return when {
            days <= 0L -> "আজ"
            days == 1L -> "গতকাল"
            else -> "${BanglaNumberFormatter.toBanglaDigits(days)} দিন আগে"
        }
    }

    /**
     * Adds [months] to [startEpochMillis] and returns the target epoch milliseconds.
     */
    fun addMonthsToEpoch(startEpochMillis: Long, months: Int): Long {
        val startDate = millisToLocalDate(startEpochMillis)
        val targetDate = startDate.plusMonths(months.toLong())
        return localDateToNoonMillis(targetDate)
    }

    /**
     * Returns the list of last [count] `YYYY-MM` keys ending at the current month in chronological order.
     */
    fun getRecentYearMonthKeys(count: Int): List<String> {
        val currentYm = YearMonth.now(DHAKA_ZONE)
        return ((count - 1) downTo 0).map { offset ->
            toYearMonthKey(currentYm.minusMonths(offset.toLong()))
        }
    }
}
