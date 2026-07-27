package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.components.RupiahVisualTransformation
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.AddPaymentUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddPaymentUiState
import com.lelestacia.tkmanagement.viewmodel.AddPaymentViewModel
import java.math.BigDecimal

@Composable
fun AddPaymentScreen(
    viewModel: AddPaymentViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    AddPaymentContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddPaymentContent(
    state: AddPaymentUiState,
    onEvent: (AddPaymentUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Pemasukan", fontWeight = FontWeight.SemiBold) },
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
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.availableFees.forEach { fee ->
                        FilterChip(
                            onClick = { onEvent(AddPaymentUiEvent.SelectFee(fee.id)) },
                            label = { Text(fee.label) },
                            selected = state.selectedFeeId == fee.id
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
                onValueChange = { onEvent(AddPaymentUiEvent.AmountChange(it)) },
                label = { Text("Nominal dibayar hari ini") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = RupiahVisualTransformation()
            )
            OutlinedTextField(
                value = state.note,
                onValueChange = { onEvent(AddPaymentUiEvent.NoteChange(it)) },
                label = { Text("Catatan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { onEvent(AddPaymentUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Pembayaran")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPaymentPreview() {
    TkCashTheme {
        AddPaymentContent(
            state = AddPaymentUiState(
                studentName = "Kamil",
                availableFees = listOf(
                    StudentFee(id = 1, studentId = 1, feeType = FeeType.SPP, label = "SPP Juli", totalAmount = BigDecimal("350000"))
                ),
                selectedFeeId = 1,
                remainingForSelectedFee = BigDecimal("350000")
            ),
            onEvent = {},
            onBack = {}
        )
    }
}
