package com.lelestacia.tkmanagement.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.theme.MoneyIn
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.MoneyTextStyle
import com.lelestacia.tkmanagement.ui.theme.MoneyTextStyleSmall
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

private val idFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))

/** "1500000" -> "Rp1.500.000" */
fun formatRupiah(amount: BigDecimal): String = "Rp${idFormat.format(amount)}"

@Composable
fun MoneyText(
    amount: BigDecimal,
    modifier: Modifier = Modifier,
    small: Boolean = false,
    color: Color? = null,
    signed: Boolean = false
) {
    val prefix = if (signed && amount > BigDecimal.ZERO) "+" else ""
    Text(
        text = "$prefix${formatRupiah(amount)}",
        style =
            if (small) {
                MaterialTheme.typography.bodySmall
            } else {
                MaterialTheme.typography.bodyMedium
            },
        color = color
            ?: (if (signed) (if (amount >= BigDecimal.ZERO) MoneyIn else MoneyOut) else Color.Unspecified),
        modifier = modifier.padding(vertical = 0.dp)
    )
}
