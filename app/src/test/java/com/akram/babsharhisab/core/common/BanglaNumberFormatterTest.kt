package com.akram.babsharhisab.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class BanglaNumberFormatterTest {

    @Test
    fun `toBanglaDigits converts ASCII digits to Bangla accurately`() {
        assertEquals("০১২৩৪৫৬৭৮৯", BanglaNumberFormatter.toBanglaDigits("0123456789"))
        assertEquals("২৪,৫০০", BanglaNumberFormatter.toBanglaDigits("24,500"))
    }

    @Test
    fun `toAsciiDigits normalizes mixed Bangla and English input and strips formatting`() {
        assertEquals("125500", BanglaNumberFormatter.toAsciiDigits("৳ ১,২৫,500"))
        assertEquals("36000", BanglaNumberFormatter.toAsciiDigits("৩৬০০০"))
    }

    @Test
    fun `parseBdtAmount safely parses Bangla, ASCII, and empty strings`() {
        assertEquals(0L, BanglaNumberFormatter.parseBdtAmount(""))
        assertEquals(0L, BanglaNumberFormatter.parseBdtAmount("   "))
        assertEquals(1850L, BanglaNumberFormatter.parseBdtAmount("১৮৫০"))
        assertEquals(125000L, BanglaNumberFormatter.parseBdtAmount("১,২৫,০০০"))
    }

    @Test
    fun `formatSouthAsianNumber applies Lakh and Crore comma grouping`() {
        assertEquals("০", BanglaNumberFormatter.formatSouthAsianNumber(0L))
        assertEquals("৯৯৯", BanglaNumberFormatter.formatSouthAsianNumber(999L))
        assertEquals("১,০০০", BanglaNumberFormatter.formatSouthAsianNumber(1_000L))
        assertEquals("১২,৫০০", BanglaNumberFormatter.formatSouthAsianNumber(12_500L))
        assertEquals("১,২৫,০০০", BanglaNumberFormatter.formatSouthAsianNumber(1_25_000L))
        assertEquals("১২,৩৪,৫৬৭", BanglaNumberFormatter.formatSouthAsianNumber(12_34_567L))
        assertEquals("১,০০,০০,০০০", BanglaNumberFormatter.formatSouthAsianNumber(1_00_00_000L))
    }

    @Test
    fun `formatTaka formats positive and negative currency with optional sign`() {
        assertEquals("৳ ২,৪৫০", BanglaNumberFormatter.formatTaka(2450L, showSymbol = true, showSign = false))
        assertEquals("+ ৳ ২,৪৫০", BanglaNumberFormatter.formatTaka(2450L, showSymbol = true, showSign = true))
        assertEquals("- ৳ ১,২০০", BanglaNumberFormatter.formatTaka(-1200L, showSymbol = true, showSign = true))
    }

    @Test
    fun `formatCompactTaka formats thousands, lakhs, and crores cleanly`() {
        assertEquals("৳৫০০", BanglaNumberFormatter.formatCompactTaka(500L))
        assertEquals("৳৫হাজার", BanglaNumberFormatter.formatCompactTaka(5_000L))
        assertEquals("৳১.৫লাখ", BanglaNumberFormatter.formatCompactTaka(1_50_000L))
        assertEquals("৳২কোটি", BanglaNumberFormatter.formatCompactTaka(2_00_00_000L))
    }
}
