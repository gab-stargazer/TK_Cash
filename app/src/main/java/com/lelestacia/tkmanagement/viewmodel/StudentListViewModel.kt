package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.repository.GraduateResult
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface StudentListUiEvent {
    data class QueryChange(val query: String) : StudentListUiEvent
    data class ToggleSelection(val studentId: Long) : StudentListUiEvent
    data class SelectAll(val studentIds: List<Long>) : StudentListUiEvent
    data object ClearSelection : StudentListUiEvent
    data object GraduateSelected : StudentListUiEvent
    data object DismissMessage : StudentListUiEvent
}

data class StudentListUiState(
    val students: List<Student> = emptyList(),
    val query: String = "",
    val selectedIds: Set<Long> = emptySet(),
    val message: String? = null
)

class StudentListViewModel(private val repository: StudentRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    val currentQuery: StateFlow<String> = query.asStateFlow()

    private val selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    private val message = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val students = query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getActiveStudents() else repository.searchStudents(q)
        }

    val uiState: StateFlow<StudentListUiState> = combine(
        students,
        query,
        selectedIds,
        message
    ) { list, q, sel, msg ->
        StudentListUiState(students = list, query = q, selectedIds = sel, message = msg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentListUiState())

    fun onEvent(event: StudentListUiEvent) {
        when (event) {
            is StudentListUiEvent.QueryChange -> query.value = event.query
            is StudentListUiEvent.ToggleSelection -> {
                val current = selectedIds.value
                selectedIds.value = if (event.studentId in current) {
                    current - event.studentId
                } else {
                    current + event.studentId
                }
            }
            is StudentListUiEvent.SelectAll -> selectedIds.value = event.studentIds.toSet()
            StudentListUiEvent.ClearSelection -> selectedIds.value = emptySet()
            StudentListUiEvent.GraduateSelected -> graduateSelected()
            StudentListUiEvent.DismissMessage -> message.value = null
        }
    }

    private fun graduateSelected() {
        val ids = selectedIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            when (val result = repository.graduateStudents(ids)) {
                is GraduateResult.Success -> {
                    selectedIds.value = emptySet()
                    message.value = if (result.graduatedCount == 0) {
                        "Tidak ada murid yang bisa diluluskan."
                    } else {
                        "${result.graduatedCount} murid diluluskan."
                    }
                }
                is GraduateResult.Skipped -> {
                    selectedIds.value = emptySet()
                    message.value = "${result.skippedIds.size} murid dilewati karena masih ada tagihan belum lunas."
                }
            }
        }
    }
}