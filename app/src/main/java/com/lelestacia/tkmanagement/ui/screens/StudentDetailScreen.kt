package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.components.LunasChip
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.components.TunggakanChip
import com.lelestacia.tkmanagement.viewmodel.StudentDetailUiEvent
import com.lelestacia.tkmanagement.viewmodel.StudentDetailViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    viewModel: StudentDetailViewModel,
    onAddFee: () -> Unit,
    onAddPayment: (feeId: Long?) -> Unit,
    onOpenReceipt: (feeId: Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    state.student?.name ?: "Murid",
                    fontWeight = FontWeight.SemiBold
                )
            })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                state.student?.let { s ->
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                "Data Wali dan Informasi Seragam:",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                "Wali: ${s.guardianName}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                "WhatsApp: ${s.whatsappNumber}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            s.nis?.let { nis ->
                                Text(
                                    "NIS: ${s.nis}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (s.uniformShirtSize != null || s.uniformPantsOrSkirtSize != null || s.uniformShoeSize != null) {
                                Text(
                                    "Seragam: ${s.uniformShirtSize ?: "-"} / ${s.uniformPantsOrSkirtSize ?: "-"} / ${s.uniformShoeSize ?: "-"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Daftar Tagihan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    TextButton(
                        onClick = onAddFee,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = Icons.Default.Add.name,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                "Tambah Tagihan",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (state.fees.isEmpty()) {
                item {
                    Column {
                        Text(
                            "Belum ada tagihan untuk murid ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedButton(
                            onClick = onAddFee,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Tambah Tagihan Pertama")
                        }
                    }
                }
            } else {
                items(state.fees, key = { it.studentFeeId }) { feeProgress ->
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = feeProgress.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                if (feeProgress.remaining <= BigDecimal.ZERO) LunasChip() else TunggakanChip()
                            }

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Column {
                                    Text(
                                        "Total",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    MoneyText(feeProgress.totalAmount, small = true)
                                }
                                Column {
                                    Text(
                                        "Sudah dibayar",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    MoneyText(feeProgress.paidAmount, small = true)
                                }
                                Column {
                                    Text(
                                        "Sisa",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    MoneyText(feeProgress.remaining, small = true)
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (feeProgress.paidAmount == feeProgress.totalAmount) {

                                        } else {
                                            onAddPayment(feeProgress.studentFeeId)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        when {
                                            feeProgress.remaining <= BigDecimal.ZERO -> "Lihat Kuitansi"
                                            else -> "Bayar Cicilan"
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                OutlinedButton(
                                    onClick = { onOpenReceipt(feeProgress.studentFeeId) },
                                    border = BorderStroke(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cetak Kuitansi")
                                }
                            }
                        }
                    }
                }
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }

            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Riwayat Pembayaran",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            val paymentsToShow = if (state.showFullHistory) state.payments else state.payments.take(3)
            items(paymentsToShow) { payment ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(payment.note ?: "Pembayaran", style = MaterialTheme.typography.bodyMedium)
                    }
                    MoneyText(payment.amount, small = true)
                }
            }

            if (!state.showFullHistory && state.payments.size > 3) {
                item {
                    OutlinedButton(
                        onClick = { viewModel.onEvent(StudentDetailUiEvent.ToggleHistory) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Lihat Riwayat Lengkap (${state.payments.size})")
                    }
                }
            }
        }
    }
}
