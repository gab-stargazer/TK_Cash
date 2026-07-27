package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import com.lelestacia.tkmanagement.data.repository.FinanceRepository
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class StudentDetailUiState(
    val isLoading: Boolean = true,
    val student: Student? = null,
    val fees: List<FeeProgress> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val showFullHistory: Boolean = false
)

sealed interface StudentDetailUiEvent {
    object ToggleHistory : StudentDetailUiEvent
}

class StudentDetailViewModel(
    private val studentRepository: StudentRepository,
    private val feeRepository: FeeRepository,
    private val financeRepository: FinanceRepository,
    private val studentId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDetailUiState())
    val uiState: StateFlow<StudentDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: StudentDetailUiEvent) {
        when (event) {
            StudentDetailUiEvent.ToggleHistory -> toggleFullHistory()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            studentRepository.readStudentDataById(studentId).collectLatest { student ->
                _uiState.value = _uiState.value.copy(isLoading = false, student = student)
            }
        }

        viewModelScope.launch {
            feeRepository.readFeeProgressForStudentById(studentId).collectLatest { fees ->
                _uiState.value = _uiState.value.copy(fees = fees)
            }
        }

        viewModelScope.launch {
            financeRepository.readPaymentsForStudentById(studentId).collectLatest { payments ->
                _uiState.value = _uiState.value.copy(payments = payments)
            }
        }
    }

    private fun toggleFullHistory() {
        _uiState.value = _uiState.value.copy(showFullHistory = !_uiState.value.showFullHistory)
    }
}
