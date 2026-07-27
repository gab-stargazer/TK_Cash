package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.viewmodel.AddFeeUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddFeeViewModel

private fun feeTypeLabel(type: FeeType) = when (type) {
    FeeType.PENDAFTARAN -> "Pendaftaran"
    FeeType.SPP -> "SPP"
    FeeType.SERAGAM -> "Seragam"
    FeeType.BUKU -> "Buku"
    FeeType.KEGIATAN -> "Kegiatan"
    FeeType.LAINNYA -> "Lainnya"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddFeeScreen(
    viewModel: AddFeeViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tambah Tagihan", fontWeight = FontWeight.SemiBold) }) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(state.studentName, style = MaterialTheme.typography.titleLarge)

            Text("Jenis Tagihan", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeeType.values().forEach { type ->
                    FilterChip(
                        selected = state.feeType == type,
                        onClick = { viewModel.onEvent(AddFeeUiEvent.TypeChange(type)) },
                        label = { Text(feeTypeLabel(type)) }
                    )
                }
            }

            OutlinedTextField(
                value = state.label,
                onValueChange = { viewModel.onEvent(AddFeeUiEvent.LabelChange(it)) },
                label = { Text("Nama tagihan") },
                placeholder = { Text("mis. Seragam Olahraga, SPP Agustus 2026") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { viewModel.onEvent(AddFeeUiEvent.AmountChange(it)) },
                label = { Text("Total tagihan (Rp)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { viewModel.onEvent(AddFeeUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Tagihan")
            }
        }
    }
}
