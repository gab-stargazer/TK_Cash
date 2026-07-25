package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.components.LunasChip
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.components.TunggakanChip
import com.lelestacia.tkmanagement.viewmodel.StudentDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: Long,
    viewModel: StudentDetailViewModel,
    onAddFee: () -> Unit,
    onAddPayment: (feeId: Long?) -> Unit,
    onOpenReceipt: (feeId: Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) { viewModel.load(studentId) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(state.student?.name ?: "Murid", fontWeight = FontWeight.SemiBold) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                state.student?.let { s ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Wali: ${s.guardianName}", style = MaterialTheme.typography.bodyMedium)
                            Text("WhatsApp: ${s.whatsappNumber}", style = MaterialTheme.typography.bodyMedium)
                            if (s.nis != null) Text("NIS: ${s.nis}", style = MaterialTheme.typography.bodyMedium)
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tagihan", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "+ Tambah Tagihan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onAddFee)
                    )
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
                        OutlinedButton(onClick = onAddFee, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text("Tambah Tagihan Pertama")
                        }
                    }
                }
            } else {
                items(state.fees, key = { it.studentFeeId }) { fw ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(fw.label, style = MaterialTheme.typography.titleMedium)
                                if (fw.remaining <= 0) LunasChip() else TunggakanChip()
                            }
                            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    MoneyText(fw.totalAmount, small = true)
                                }
                                Column {
                                    Text("Sudah dibayar", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    MoneyText(fw.paidAmount, small = true)
                                }
                                Column {
                                    Text("Sisa", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    MoneyText(fw.remaining, small = true)
                                }
                            }
                            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { onAddPayment(fw.studentFeeId) }, modifier = Modifier.weight(1f)) {
                                    Text(if (fw.remaining <= 0) "Lihat Kuitansi" else "Bayar Cicilan")
                                }
                                OutlinedButton(onClick = { onOpenReceipt(fw.studentFeeId) }, modifier = Modifier.weight(1f)) {
                                    Text("Cetak Kuitansi")
                                }
                            }
                        }
                    }
                }
            }

            item { Divider(Modifier.padding(vertical = 4.dp)) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Riwayat Pembayaran", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (state.showFullHistory) "Sembunyikan" else "Lihat Riwayat Lengkap",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = viewModel::toggleFullHistory)
                    )
                }
            }

            val paymentsToShow = if (state.showFullHistory) state.payments else state.payments.take(3)
            items(paymentsToShow) { p ->
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(p.note ?: "Pembayaran", style = MaterialTheme.typography.bodyMedium)
                    }
                    MoneyText(p.amount, small = true)
                }
            }

            if (!state.showFullHistory && state.payments.size > 3) {
                item {
                    OutlinedButton(onClick = viewModel::toggleFullHistory, modifier = Modifier.fillMaxWidth()) {
                        Text("Lihat Riwayat Lengkap (${state.payments.size})")
                    }
                }
            }
        }
    }
}
