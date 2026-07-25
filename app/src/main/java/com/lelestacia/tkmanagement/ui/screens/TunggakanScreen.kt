package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.viewmodel.TunggakanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TunggakanScreen(
    viewModel: TunggakanViewModel,
    onOpenStudent: (Long) -> Unit
) {
    val list by viewModel.list.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Tunggakan", fontWeight = FontWeight.SemiBold) }) }
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
