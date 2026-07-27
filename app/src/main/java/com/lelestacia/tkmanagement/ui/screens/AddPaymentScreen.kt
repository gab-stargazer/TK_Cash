package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.viewmodel.AddPaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    viewModel: AddPaymentViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catat Pemasukan", fontWeight = FontWeight.SemiBold) }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(state.studentName, style = MaterialTheme.typography.titleLarge)

            Text("Pilih Tagihan", style = MaterialTheme.typography.titleMedium)
            if (state.availableFees.isEmpty()) {
                Text(
                    "Belum ada tagihan untuk murid ini. Pembayaran akan dicatat sebagai pemasukan umum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.availableFees.forEach { fee ->
                        AssistChip(
                            onClick = { viewModel.selectFee(fee.id) },
                            label = { Text(fee.label) }
                        )
                    }
                }
            }

            state.remainingForSelectedFee?.let { remaining ->
                Text("Sisa tagihan saat ini:", style = MaterialTheme.typography.bodyMedium)
                MoneyText(remaining, small = true)
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Nominal dibayar hari ini (Rp)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Catatan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = viewModel::save,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Pembayaran")
            }
        }
    }
}
