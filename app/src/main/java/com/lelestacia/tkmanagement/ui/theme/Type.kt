package com.lelestacia.tkmanagement.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Tipografi default aplikasi. */
val AppTypography = Typography()

/**
 * Signature detail: every Rupiah amount in the app uses tabular figures
 * (fixed-width digits) so numbers in a list — saldo, tunggakan, cicilan —
 * align into a real ledger column instead of jittering like normal text.
 */
val MoneyTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    letterSpacing = 0.sp
)

/** Varian [MoneyTextStyle] yang lebih kecil untuk nominal ringkas. */
val MoneyTextStyleSmall = MoneyTextStyle.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium)
