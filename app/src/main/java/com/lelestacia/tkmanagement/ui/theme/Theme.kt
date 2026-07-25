package com.lelestacia.tkmanagement.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = InkDeep,
    onPrimary = InkPaper,
    primaryContainer = InkMid,
    onPrimaryContainer = InkPaper,
    background = InkPaper,
    onBackground = TextInk,
    surface = SurfaceCard,
    onSurface = TextInk,
    surfaceVariant = LedgerLine,
    onSurfaceVariant = TextMuted,
    error = MoneyOut
)

@Composable
fun TkCashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
