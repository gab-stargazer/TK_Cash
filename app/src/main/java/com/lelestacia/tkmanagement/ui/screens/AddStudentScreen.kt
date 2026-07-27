package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.viewmodel.AddStudentUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddStudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(
    viewModel: AddStudentViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tambah Murid", fontWeight = FontWeight.SemiBold) }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateName(it)) },
                label = { Text("Nama murid") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.nis,
                onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateNis(it)) },
                label = { Text("Nomor induk siswa (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.guardianName,
                onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateGuardianName(it)) },
                label = { Text("Nama wali") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.whatsappNumber,
                onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateWhatsappNumber(it)) },
                label = { Text("Nomor WhatsApp wali") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ukuran Seragam", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.uniformShirtSize,
                    onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateUniformShirtSize(it)) },
                    label = { Text("Baju") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.uniformPantsOrSkirtSize,
                    onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateUniformPantsOrSkirtSize(it)) },
                    label = { Text("Celana/Rok") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.uniformShoeSize,
                    onValueChange = { viewModel.onEvent(AddStudentUiEvent.UpdateUniformShoeSize(it)) },
                    label = { Text("Sepatu") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { viewModel.onEvent(AddStudentUiEvent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Murid")
            }
        }
    }
}
