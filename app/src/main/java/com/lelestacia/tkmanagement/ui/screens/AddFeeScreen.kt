package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.ui.components.RupiahVisualTransformation
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.AddFeeUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddFeeUiState
import com.lelestacia.tkmanagement.viewmodel.AddFeeViewModel

@Composable
fun AddFeeScreen(
    viewModel: AddFeeViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    AddFeeContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddFeeContent(
    state: AddFeeUiState,
    onEvent: (AddFeeUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Tagihan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(state.studentName, style = MaterialTheme.typography.titleLarge)

            Text("Jenis Tagihan", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeeType.values().forEach { type ->
                    FilterChip(
                        selected = state.feeType == type,
                        onClick = { onEvent(AddFeeUiEvent.TypeChange(type)) },
                        label = { Text(feeTypeLabel(type)) }
                    )
                }
            }

            if (state.feeType == FeeType.SPP) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MonthDropdown(
                        selectedMonth = state.selectedMonth,
                        onMonthSelected = { onEvent(AddFeeUiEvent.MonthChange(it)) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.selectedYear.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { year -> onEvent(AddFeeUiEvent.YearChange(year)) }
                        },
                        label = { Text("Tahun") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = state.label,
                onValueChange = { onEvent(AddFeeUiEvent.LabelChange(it)) },
                label = { Text("Nama tagihan") },
                placeholder = { Text("mis. Seragam Olahraga, SPP Agustus 2026") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                readOnly = state.feeType != FeeType.LAINNYA,
                enabled = state.feeType == FeeType.LAINNYA
            )

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onEvent(AddFeeUiEvent.AmountChange(it)) },
                label = { Text("Total tagihan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = RupiahVisualTransformation()
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = { onEvent(AddFeeUiEvent.NoteChange(it)) },
                label = { Text("Keterangan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { onEvent(AddFeeUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Tagihan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthDropdown(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = months[selectedMonth - 1],
            onValueChange = {},
            readOnly = true,
            label = { Text("Bulan") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEachIndexed { index, month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        onMonthSelected(index + 1)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun feeTypeLabel(type: FeeType) = when (type) {
    FeeType.PEMBANGUNAN -> "Pendaftaran"
    FeeType.SPP -> "SPP"
    FeeType.SERAGAM -> "Seragam"
    FeeType.BUKU -> "Buku"
    FeeType.KEGIATAN -> "Kegiatan"
    FeeType.LAINNYA -> "Lainnya"
}

@Preview(showBackground = true)
@Composable
private fun AddFeePreview() {
    TkCashTheme {
        AddFeeContent(
            state = AddFeeUiState(
                studentName = "Kamil",
                feeType = FeeType.SPP,
                label = "SPP Juli 2026",
                amountInput = "350000"
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
