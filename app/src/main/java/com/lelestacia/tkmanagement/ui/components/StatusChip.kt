package com.lelestacia.tkmanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.theme.AlertAmber
import com.lelestacia.tkmanagement.ui.theme.MoneyIn

/** Chip status kecil berwarna sesuai [tone], dengan latar transparan dan teks label. */
@Composable
fun StatusChip(label: String, tone: Color, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = tone,
        modifier = modifier
            .background(tone.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

/** Chip status "LUNAS" berwarna hijau. */
@Composable
fun LunasChip(modifier: Modifier = Modifier) = StatusChip("LUNAS", MoneyIn, modifier)

/** Chip status "BELUM LUNAS" berwarna amber. */
@Composable
fun TunggakanChip(modifier: Modifier = Modifier) = StatusChip("BELUM LUNAS", AlertAmber, modifier)
