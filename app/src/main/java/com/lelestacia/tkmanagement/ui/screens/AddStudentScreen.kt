package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.ui.theme.MoneyOut
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.AddStudentUiEvent
import com.lelestacia.tkmanagement.viewmodel.AddStudentUiState
import com.lelestacia.tkmanagement.viewmodel.AddStudentViewModel

@Composable
fun AddStudentScreen(
    viewModel: AddStudentViewModel,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.saved) { if (state.saved) onSaved() }

    AddStudentContent(
        state = state,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStudentContent(
    state: AddStudentUiState,
    onEvent: (AddStudentUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Murid", fontWeight = FontWeight.SemiBold) },
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
            OutlinedTextField(
                value = state.name,
                onValueChange = { onEvent(AddStudentUiEvent.UpdateName(it)) },
                label = { Text("Nama murid*") },
                singleLine = true,
                isError = state.name.isBlank() && state.error != null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.nis,
                onValueChange = { onEvent(AddStudentUiEvent.UpdateNis(it)) },
                label = { Text("Nomor induk siswa (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.guardianName,
                onValueChange = { onEvent(AddStudentUiEvent.UpdateGuardianName(it)) },
                label = { Text("Nama wali*") },
                singleLine = true,
                isError = state.guardianName.isBlank() && state.error != null,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.whatsappNumber,
                onValueChange = { onEvent(AddStudentUiEvent.UpdateWhatsappNumber(it)) },
                label = { Text("Nomor WhatsApp wali*") },
                singleLine = true,
                isError = state.whatsappNumber.isBlank() && state.error != null,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ukuran Seragam", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.uniformShirtSize,
                    onValueChange = { onEvent(AddStudentUiEvent.UpdateUniformShirtSize(it)) },
                    label = { Text("Baju*") },
                    singleLine = true,
                    isError = state.uniformShirtSize.isBlank() && state.error != null,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.uniformPantsOrSkirtSize,
                    onValueChange = { onEvent(AddStudentUiEvent.UpdateUniformPantsOrSkirtSize(it)) },
                    label = { Text("Celana/Rok*") },
                    singleLine = true,
                    isError = state.uniformPantsOrSkirtSize.isBlank() && state.error != null,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable {
                    onEvent(AddStudentUiEvent.UpdateAlumniStatus(!state.isAlumniFamily))
                }
            ) {
                Checkbox(
                    checked = state.isAlumniFamily,
                    onCheckedChange = { onEvent(AddStudentUiEvent.UpdateAlumniStatus(it)) }
                )
                Text("Keluarga Alumni (Potongan Pembangunan Rp 75.000)")
            }

            state.error?.let {
                Text(it, color = MoneyOut, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = { onEvent(AddStudentUiEvent.Save) },
                enabled = !state.isSaving && state.isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Menyimpan..." else "Simpan Murid")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddStudentPreview() {
    TkCashTheme {
        AddStudentContent(
            state = AddStudentUiState(),
            onEvent = {},
            onBack = {}
        )
    }
}