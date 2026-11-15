package com.akram.babsharhisab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Bangla script typography scale (03_UI_Spec.md Section 3).
 * Uses `PlatformTextStyle(includeFontPadding = true)` and generous line-heights (1.4x-1.5x)
 * so Bangla upper/lower diacritics (কার ও ফলা) never clip.
 */
private val banglaPlatformStyle = PlatformTextStyle(includeFontPadding = true)

fun buildBanglaTypography(useSerifClassic: Boolean = false): Typography {
    val family = if (useSerifClassic) FontFamily.Serif else FontFamily.SansSerif
    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 44.sp,
            platformStyle = banglaPlatformStyle
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 34.sp,
            platformStyle = banglaPlatformStyle
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            platformStyle = banglaPlatformStyle
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            platformStyle = banglaPlatformStyle
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            platformStyle = banglaPlatformStyle
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            platformStyle = banglaPlatformStyle
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            platformStyle = banglaPlatformStyle
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            platformStyle = banglaPlatformStyle
        )
    )
}
