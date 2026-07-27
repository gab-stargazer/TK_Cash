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
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.viewmodel.AddExpenseUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catat Pengeluaran", fontWeight = FontWeight.SemiBold) }) }
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
                        onClick = { viewModel.onEvent(AddExpenseUiEvent.CategoryChange(category)) },
                        label = { Text(category.name) }
                    )
                }
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { viewModel.onEvent(AddExpenseUiEvent.AmountChange(it)) },
                label = { Text("Nominal (Rp)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.onEvent(AddExpenseUiEvent.NoteChange(it)) },
                label = { Text("Keterangan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { viewModel.onEvent(AddExpenseUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Pengeluaran")
            }
        }
    }
}
