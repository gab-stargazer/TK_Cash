package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.ui.components.RupiahVisualTransformation
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.AddExpenseUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddExpenseUiState
import com.lelestacia.tkmanagement.viewmodel.AddExpenseViewModel

@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    AddExpenseContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddExpenseContent(
    state: AddExpenseUiState,
    onEvent: (AddExpenseUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Pengeluaran", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Kategori", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpenseCategory.values().forEach { category ->
                    FilterChip(
                        selected = state.category == category,
                        onClick = { onEvent(AddExpenseUiEvent.CategoryChange(category)) },
                        label = { Text(category.name) }
                    )
                }
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onEvent(AddExpenseUiEvent.AmountChange(it)) },
                label = { Text("Nominal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = RupiahVisualTransformation()
            )
            OutlinedTextField(
                value = state.note,
                onValueChange = { onEvent(AddExpenseUiEvent.NoteChange(it)) },
                label = { Text("Keterangan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { onEvent(AddExpenseUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Pengeluaran")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddExpensePreview() {
    TkCashTheme {
        AddExpenseContent(
            state = AddExpenseUiState(),
            onEvent = {},
            onBack = {}
        )
    }
}
