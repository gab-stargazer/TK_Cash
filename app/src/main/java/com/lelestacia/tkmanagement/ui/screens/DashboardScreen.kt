package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.theme.InkPaper
import com.lelestacia.tkmanagement.ui.theme.MoneyIn
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.viewmodel.DashboardViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddPayment: () -> Unit,
    onAddExpense: () -> Unit,
    onOpenStudents: () -> Unit,
    onOpenTunggakan: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        containerColor = InkPaper,
        topBar = {
            TopAppBar(title = { Text("Kas TK", fontWeight = FontWeight.SemiBold) })
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(onClick = onAddExpense) { Icon(Icons.Default.Add, contentDescription = "Catat Pengeluaran") }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Saldo Kas",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        MoneyText(
                            amount = state.saldoKas,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                        )
                    }
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Pemasukan",
                        amount = state.totalPemasukan,
                        color = MoneyIn,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Pengeluaran",
                        amount = state.totalPengeluaran,
                        color = MoneyOut,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(onClick = onAddPayment, modifier = Modifier.weight(1f)) { Text("Catat Pemasukan") }
                    OutlinedButton(onClick = onOpenStudents, modifier = Modifier.weight(1f)) { Text("Data Murid") }
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tunggakan Terbesar", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Lihat semua",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                    )
                }
            }

            if (state.topTunggakan.isEmpty()) {
                item {
                    Text(
                        "Tidak ada tunggakan saat ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.topTunggakan.size) { index ->
                    val item = state.topTunggakan[index]
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.studentName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    item.feeLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            MoneyText(amount = item.remaining, small = true)
                        }
                    }
                }
            }

            item {
                OutlinedButton(onClick = onOpenTunggakan, modifier = Modifier.fillMaxWidth()) {
                    Text("Lihat Semua Tunggakan")
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, amount: BigDecimal, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            MoneyText(amount = amount, small = true, color = color)
        }
    }
}
