package com.lelestacia.tkmanagement.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.theme.MoneyIn
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.MoneyTextStyle
import com.lelestacia.tkmanagement.ui.theme.MoneyTextStyleSmall
import java.text.NumberFormat
import java.util.Locale

private val idFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))

/** "1500000" -> "Rp1.500.000" */
fun formatRupiah(amount: Long): String = "Rp${idFormat.format(amount)}"

@Composable
fun MoneyText(
    amount: Long,
    modifier: Modifier = Modifier,
    small: Boolean = false,
    color: Color? = null,
    signed: Boolean = false
) {
    val prefix = if (signed && amount > 0) "+" else ""
    Text(
        text = "$prefix${formatRupiah(amount)}",
        style = if (small) MoneyTextStyleSmall else MoneyTextStyle,
        color = color ?: (if (signed) (if (amount >= 0) MoneyIn else MoneyOut) else Color.Unspecified),
        modifier = modifier.padding(vertical = 0.dp)
    )
}
