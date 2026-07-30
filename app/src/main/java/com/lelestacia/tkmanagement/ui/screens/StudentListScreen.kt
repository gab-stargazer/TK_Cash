package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.StudentListUiEvent
import com.lelestacia.tkmanagement.viewmodel.StudentListUiState
import com.lelestacia.tkmanagement.viewmodel.StudentListViewModel

@Composable
fun StudentListScreen(
    viewModel: StudentListViewModel,
    onOpenStudent: (Long) -> Unit,
    onAddStudent: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    StudentListContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onOpenStudent = onOpenStudent,
        onAddStudent = onAddStudent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentListContent(
    uiState: StudentListUiState,
    onEvent: (StudentListUiEvent) -> Unit,
    onOpenStudent: (Long) -> Unit,
    onAddStudent: () -> Unit,
    onBack: () -> Unit
) {
    val isSelectionMode = uiState.selectedIds.isNotEmpty()
    var showConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(StudentListUiEvent.DismissMessage)
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Luluskan murid?") },
            text = {
                Text("${uiState.selectedIds.size} murid akan ditandai lulus. Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onEvent(StudentListUiEvent.GraduateSelected)
                }) { Text("Luluskan") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isSelectionMode) "${uiState.selectedIds.size} dipilih"
                        else "Data Murid",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) onEvent(StudentListUiEvent.ClearSelection)
                        else onBack()
                    }) {
                        Icon(
                            if (isSelectionMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSelectionMode) "Batal pilih" else "Kembali"
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { showConfirm = true }) {
                            Icon(Icons.Default.School, contentDescription = "Luluskan")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = onAddStudent,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Tambah Murid") }
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                BottomAppBar(
                    actions = {
                        TextButton(onClick = { onEvent(StudentListUiEvent.SelectAll(uiState.students.map { it.id })) }) {
                            Text("Pilih Semua")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (!isSelectionMode) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { onEvent(StudentListUiEvent.QueryChange(it)) },
                    label = { Text("Cari nama murid") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }

            if (uiState.students.isEmpty()) {
                Text(
                    "Belum ada murid. Tap tombol di bawah untuk menambahkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(uiState.students, key = { it.id }) { student ->
                        StudentRow(
                            student = student,
                            isSelected = student.id in uiState.selectedIds,
                            isSelectionMode = isSelectionMode,
                            onToggle = { onEvent(StudentListUiEvent.ToggleSelection(student.id)) },
                            onOpen = { onOpenStudent(student.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentRow(
    student: Student,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggle: () -> Unit,
    onOpen: () -> Unit
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = {
            if (isSelectionMode) onToggle() else onOpen()
        }
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
                Spacer(Modifier.width(8.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(student.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    student.guardianName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isSelectionMode) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentListPreview() {
    TkCashTheme {
        StudentListContent(
            uiState = StudentListUiState(
                students = listOf(
                    Student(id = 1, name = "Kamil", guardianName = "Wali Kamil", whatsappNumber = "08123456789", uniformShirtSize = "M", uniformPantsOrSkirtSize = "M"),
                    Student(id = 2, name = "Ahmad", guardianName = "Wali Ahmad", whatsappNumber = "08123456780", uniformShirtSize = "L", uniformPantsOrSkirtSize = "L")
                )
            ),
            onEvent = {},
            onOpenStudent = {},
            onAddStudent = {},
            onBack = {}
        )
    }
}