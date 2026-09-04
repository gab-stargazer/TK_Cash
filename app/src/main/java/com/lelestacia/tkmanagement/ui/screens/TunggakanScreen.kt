package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.TunggakanViewModel
import java.math.BigDecimal

@Composable
/** Layar daftar seluruh tunggakan dan pintu masuk ke detail murid. */
fun TunggakanScreen(
    viewModel: TunggakanViewModel,
    onOpenStudent: (Long) -> Unit,
    onBack: () -> Unit
) {
    val list by viewModel.list.collectAsState()

    TunggakanContent(
        list = list,
        onOpenStudent = onOpenStudent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TunggakanContent(
    list: List<TunggakanItem>,
    onOpenStudent: (Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Tunggakan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (list.isEmpty()) {
            Column(Modifier.fillMaxWidth().padding(padding).padding(16.dp)) {
                Text(
                    "Tidak ada tunggakan. Semua pembayaran lunas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list.size) { index->
                    val item = list[index]
                    Card(
                        onClick = { onOpenStudent(item.studentId) },
                        Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.studentName, style = MaterialTheme.typography.titleMedium)
                                MoneyText(item.remaining, small = true)
                            }
                            Text(
                                item.feeLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TunggakanPreview() {
    TkCashTheme {
        TunggakanContent(
            list = listOf(
                TunggakanItem(
                    studentFeeId = 1,
                    studentId = 1,
                    studentName = "Kamil",
                    guardianName = "Malik",
                    feeLabel = "SPP Juli",
                    totalAmount = BigDecimal("350000"),
                    paidAmount = BigDecimal("0"),
                    remaining = BigDecimal("350000")
                )
            ),
            onOpenStudent = {},
            onBack = {}
        )
    }
}
