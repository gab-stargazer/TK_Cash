package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FullReceiptUiState(
    val isLoading: Boolean = true,
    val history: StudentWithFullHistory? = null
)

sealed interface FullReceiptUiEvent

class FullReceiptViewModel(
    private val studentRepository: StudentRepository,
    private val studentId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FullReceiptUiState())
    val uiState: StateFlow<FullReceiptUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val data = studentRepository.getFullHistory(studentId)
            _uiState.value = _uiState.value.copy(isLoading = false, history = data)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: FullReceiptUiEvent) { }
}
