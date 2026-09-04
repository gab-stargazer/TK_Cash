package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** State layar riwayat lengkap: penanda pemuatan dan data riwayat murid. */
data class FullReceiptUiState(
    /** Penanda bahwa data masih dimuat. */
    val isLoading: Boolean = true,
    /** Riwayat lengkap murid, null selama masih memuat. */
    val history: StudentWithFullHistory? = null
)

/** Event UI layar riwayat lengkap (saat ini belum dipakai). */
sealed interface FullReceiptUiEvent

/**
 * ViewModel riwayat lengkap: memuat riwayat satu murid dari [StudentRepository]
 * dan mengeksposnya sebagai [uiState] untuk layar FullReceipt.
 */
class FullReceiptViewModel(
    private val studentRepository: StudentRepository,
    private val studentId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FullReceiptUiState())
    /** State UI terbaru untuk layar riwayat lengkap. */
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

    /** Menerima event UI; saat ini tidak melakukan apa pun. */
    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: FullReceiptUiEvent) { }
}
