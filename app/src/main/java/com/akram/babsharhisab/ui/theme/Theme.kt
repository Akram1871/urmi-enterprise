package com.akram.babsharhisab.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryContainerLight,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = PurchaseBlue,
    onSecondary = Color.White,
    secondaryContainer = PurchaseBlueBgLight,
    onSecondaryContainer = PurchaseBlue,
    tertiary = BakiAmber,
    onTertiary = Color.White,
    tertiaryContainer = BakiAmberBgLight,
    onTertiaryContainer = BakiAmber,
    error = ExpenseRed,
    onError = Color.White,
    errorContainer = ExpenseRedBgLight,
    onErrorContainer = ExpenseRed,
    background = BackgroundPrimaryLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceSubtleLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderDividerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = Color(0xFF064E3B),
    primaryContainer = BrandPrimaryContainerDark,
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = PurchaseBlueDark,
    onSecondary = Color(0xFF1E3A8A),
    secondaryContainer = PurchaseBlueBgDark,
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = BakiAmberDark,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = BakiAmberBgDark,
    onTertiaryContainer = Color(0xFFFEF3C7),
    error = ExpenseRedDark,
    onError = Color(0xFF450A0A),
    errorContainer = ExpenseRedBgDark,
    onErrorContainer = Color(0xFFFEE2E2),
    background = BackgroundPrimaryDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSubtleDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDividerDark
)

@Composable
fun BabsharHisabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useClassicFont: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val typography = buildBanglaTypography(useSerifClassic = useClassicFont)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
