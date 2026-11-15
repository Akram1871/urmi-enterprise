package com.akram.babsharhisab.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Tokens (03_UI_Spec.md Section 1.1)
val BackgroundPrimaryLight = Color(0xFFF8FAF9)
val SurfaceCardLight = Color(0xFFFFFFFF)
val SurfaceSubtleLight = Color(0xFFEEF2F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextMutedLight = Color(0xFF64748B)
val BrandPrimaryLight = Color(0xFF047857)
val BrandPrimaryContainerLight = Color(0xFFD1FAE5)
val BorderDividerLight = Color(0xFFE2E8F0)

// Dark Theme Tokens (03_UI_Spec.md Section 1.2)
val BackgroundPrimaryDark = Color(0xFF0B111E)
val SurfaceCardDark = Color(0xFF151F32)
val SurfaceSubtleDark = Color(0xFF1E293B)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val BrandPrimaryDark = Color(0xFF10B981)
val BrandPrimaryContainerDark = Color(0xFF064E3B)
val BorderDividerDark = Color(0xFF26334D)

// Semantic Financial Colors
val ProfitGreen = Color(0xFF059669)
val ProfitGreenBgLight = Color(0xFFECFDF5)
val ProfitGreenDark = Color(0xFF34D399)
val ProfitGreenBgDark = Color(0xFF064E3B)

val ExpenseRed = Color(0xFFDC2626)
val ExpenseRedBgLight = Color(0xFFFEF2F2)
val ExpenseRedDark = Color(0xFFF87171)
val ExpenseRedBgDark = Color(0xFF450A0A)

val BakiAmber = Color(0xFFD97706)
val BakiAmberBgLight = Color(0xFFFFFBEB)
val BakiAmberDark = Color(0xFFFBBF24)
val BakiAmberBgDark = Color(0xFF451A03)

val PurchaseBlue = Color(0xFF1D4ED8)
val PurchaseBlueBgLight = Color(0xFFEFF6FF)
val PurchaseBlueDark = Color(0xFF60A5FA)
val PurchaseBlueBgDark = Color(0xFF1E3A8A)

// Category Deterministic Colors
val CatPurchaseColor = Color(0xFF2563EB)
val CatFuelColor = Color(0xFFEA580C)
val CatTransportColor = Color(0xFF7C3AED)
val CatFoodColor = Color(0xFF0D9488)
val CatRepairColor = Color(0xFFE11D48)
val CatAssetColor = Color(0xFF4F46E5)
val CatOtherColor = Color(0xFF64748B)

fun parseHexColor(hexString: String, fallback: Color = BrandPrimaryLight): Color {
    return try {
        val clean = hexString.trim().removePrefix("#")
        val colorLong = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorLong)
        } else {
            Color(colorLong)
        }
    } catch (_: Exception) {
        fallback
    }
}
