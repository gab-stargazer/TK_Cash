package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.StudentListUiEvent
import com.lelestacia.tkmanagement.viewmodel.StudentListViewModel

@Composable
fun StudentListScreen(
    viewModel: StudentListViewModel,
    onOpenStudent: (Long) -> Unit,
    onAddStudent: () -> Unit,
    onBack: () -> Unit
) {
    val students by viewModel.students.collectAsState()
    val query by viewModel.currentQuery.collectAsState()

    StudentListContent(
        students = students,
        query = query,
        onEvent = viewModel::onEvent,
        onOpenStudent = onOpenStudent,
        onAddStudent = onAddStudent,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentListContent(
    students: List<Student>,
    query: String,
    onEvent: (StudentListUiEvent) -> Unit,
    onOpenStudent: (Long) -> Unit,
    onAddStudent: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Murid", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddStudent,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Tambah Murid") }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { onEvent(StudentListUiEvent.QueryChange(it)) },
                label = { Text("Cari nama murid") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            if (students.isEmpty()) {
                Text(
                    "Belum ada murid. Tap tombol di bawah untuk menambahkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    items(students, key = { it.id }) { student ->
                        StudentRow(student, onClick = { onOpenStudent(student.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentRow(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(student.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    student.guardianName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            students = listOf(
                Student(id = 1, name = "Kamil", guardianName = "Wali Kamil", whatsappNumber = "08123456789"),
                Student(id = 2, name = "Ahmad", guardianName = "Wali Ahmad", whatsappNumber = "08123456780")
            ),
            query = "",
            onEvent = {},
            onOpenStudent = {},
            onAddStudent = {},
            onBack = {}
        )
    }
}
