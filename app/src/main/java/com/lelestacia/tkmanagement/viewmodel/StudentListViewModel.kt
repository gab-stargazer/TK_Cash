package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

sealed interface StudentListUiEvent {
    data class QueryChange(val query: String) : StudentListUiEvent
}

class StudentListViewModel(private val repository: StudentRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    val currentQuery: StateFlow<String> = query

    fun onEvent(event: StudentListUiEvent) {
        when (event) {
            is StudentListUiEvent.QueryChange -> query.value = event.query
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getActiveStudents() else repository.searchStudents(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
