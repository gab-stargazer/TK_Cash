package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AddPaymentUiState(
    val studentId: Long = 0,
    val studentName: String = "",
    val availableFees: List<StudentFee> = emptyList(),
    val selectedFeeId: Long? = null,
    val remainingForSelectedFee: Long? = null,
    val amountInput: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

class AddPaymentViewModel(private val repository: CashRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPaymentUiState())
    val uiState: StateFlow<AddPaymentUiState> = _uiState.asStateFlow()

    fun load(studentId: Long, studentName: String) {
        _uiState.value = _uiState.value.copy(studentId = studentId, studentName = studentName)
        viewModelScope.launch {
            repository.getFeesForStudent(studentId).collectLatest { fees ->
                _uiState.value = _uiState.value.copy(availableFees = fees)
            }
        }
    }

    fun selectFee(feeId: Long?) {
        _uiState.value = _uiState.value.copy(selectedFeeId = feeId, error = null)
        if (feeId == null) {
            _uiState.value = _uiState.value.copy(remainingForSelectedFee = null)
            return
        }
        viewModelScope.launch {
            val status = repository.getFeeStatus(feeId)
            _uiState.value = _uiState.value.copy(remainingForSelectedFee = status?.remaining)
        }
    }

    /** Membuat tagihan baru langsung dari form ini (mis. "SPP Agustus 2026"). */
    fun createFeeAndSelect(feeType: FeeType, label: String, totalAmount: Long) {
        viewModelScope.launch {
            val id = repository.addFee(
                StudentFee(studentId = _uiState.value.studentId, feeType = feeType, label = label, totalAmount = totalAmount)
            )
            selectFee(id)
        }
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amountInput = value.filter { it.isDigit() }, error = null)
    }

    fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    fun save() {
        val s = _uiState.value
        val amount = s.amountInput.toLongOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = s.copy(error = "Masukkan nominal yang valid")
            return
        }

        _uiState.value = s.copy(isSaving = true)
        viewModelScope.launch {
            val result = repository.recordPayment(
                Payment(
                    studentId = s.studentId,
                    studentFeeId = s.selectedFeeId,
                    amount = amount,
                    note = s.note.ifBlank { null }
                )
            )
            result.fold(
                onSuccess = { _uiState.value = AddPaymentUiState(saved = true) },
                onFailure = { e -> _uiState.value = s.copy(isSaving = false, error = e.message) }
            )
        }
    }
}
