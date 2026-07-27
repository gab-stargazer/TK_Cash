package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
    private val studentId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDetailUiState())
    val uiState: StateFlow<StudentDetailUiState> = combine(
        flow = _uiState,
        flow2 = repository.readStudentDataById(studentId),
        flow3 = repository.readFeeProgressForStudentById(studentId),
        flow4 = repository.readPaymentsForStudentById(studentId)
    ) { state, student, feeProgress, payments ->
        state.copy(
            student = student,
            fees = feeProgress,
            payments = payments,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        StudentDetailUiState(
            isLoading = true,
            student = null,
            fees = emptyList(),
            payments = emptyList()
        )
    )

    /** Tombol "Lihat Riwayat Lengkap". */
    fun toggleFullHistory() {
        _uiState.value = _uiState.value.copy(showFullHistory = !_uiState.value.showFullHistory)
    }
}
