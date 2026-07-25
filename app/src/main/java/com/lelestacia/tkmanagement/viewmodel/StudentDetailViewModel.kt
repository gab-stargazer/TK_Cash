package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.repository.CashRepository
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

/** studentId biasanya datang dari NavGraph lewat SavedStateHandle. */
class StudentDetailViewModel(
    private val repository: CashRepository,
    savedStateHandle: SavedStateHandle? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDetailUiState())
    val uiState: StateFlow<StudentDetailUiState> = _uiState.asStateFlow()

    private var loadedStudentId: Long? = null

    init {
        savedStateHandle?.get<Long>("studentId")?.let { load(it) }
    }

    fun load(studentId: Long) {
        if (loadedStudentId == studentId) return
        loadedStudentId = studentId

        viewModelScope.launch {
            val student = repository.getStudent(studentId)
            _uiState.value = _uiState.value.copy(isLoading = false, student = student)
        }

        // Live: recomposes automatically whenever student_fees or payments
        // change in Room — no manual reload after recording a payment.
        viewModelScope.launch {
            repository.getFeeProgressForStudent(studentId).collectLatest { fees ->
                _uiState.value = _uiState.value.copy(fees = fees)
            }
        }
        viewModelScope.launch {
            repository.getPaymentsForStudent(studentId).collectLatest { payments ->
                _uiState.value = _uiState.value.copy(payments = payments)
            }
        }
    }

    /** Tombol "Lihat Riwayat Lengkap". */
    fun toggleFullHistory() {
        _uiState.value = _uiState.value.copy(showFullHistory = !_uiState.value.showFullHistory)
    }
}
