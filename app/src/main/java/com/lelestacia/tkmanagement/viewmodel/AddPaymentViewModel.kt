package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import com.lelestacia.tkmanagement.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal

/** State layar tambah pembayaran. */
data class AddPaymentUiState(
    val studentId: Long = 0,
    val studentName: String = "",
    val availableFees: List<StudentFee> = emptyList(),
    val selectedFeeId: Long? = null,
    val remainingForSelectedFee: BigDecimal? = null,
    val amountInput: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

/** Event UI untuk layar tambah pembayaran. */
sealed interface AddPaymentUiEvent {
    data class SelectFee(val feeId: Long?) : AddPaymentUiEvent
    data class AmountChange(val value: String) : AddPaymentUiEvent
    data class NoteChange(val value: String) : AddPaymentUiEvent
    object Save : AddPaymentUiEvent
}

/** ViewModel tambah pembayaran: memilih tagihan, memvalidasi nominal, dan menyimpan pembayaran. */
class AddPaymentViewModel(
    private val financeRepository: FinanceRepository,
    private val feeRepository: FeeRepository,
    private val studentId: Long,
    private val studentName: String,
    private val preselectedFeeId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPaymentUiState())
    val uiState: StateFlow<AddPaymentUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(studentId = studentId, studentName = studentName)
        viewModelScope.launch {
            feeRepository.getFeesForStudent(studentId).collectLatest { fees ->
                _uiState.value = _uiState.value.copy(availableFees = fees)
            }
        }
        if (preselectedFeeId != null) {
            onEvent(AddPaymentUiEvent.SelectFee(preselectedFeeId))
        }
    }

    fun onEvent(event: AddPaymentUiEvent) {
        when (event) {
            is AddPaymentUiEvent.SelectFee -> selectFee(event.feeId)
            is AddPaymentUiEvent.AmountChange -> onAmountChange(event.value)
            is AddPaymentUiEvent.NoteChange -> onNoteChange(event.value)
            AddPaymentUiEvent.Save -> save()
        }
    }

    private fun selectFee(feeId: Long?) {
        _uiState.value = _uiState.value.copy(selectedFeeId = feeId, error = null)
        if (feeId == null) {
            _uiState.value = _uiState.value.copy(remainingForSelectedFee = null)
            return
        }
        viewModelScope.launch {
            val status = feeRepository.getFeeStatus(feeId)
            _uiState.value = _uiState.value.copy(remainingForSelectedFee = status?.remaining)
        }
    }

    private fun onAmountChange(value: String) {
        if (value.any { !it.isDigit() }) return
        val cleanValue = if (value.startsWith("0") && value.length > 1) {
            value.trimStart('0')
        } else {
            value
        }
        _uiState.value = _uiState.value.copy(amountInput = cleanValue, error = null)
    }

    private fun onNoteChange(value: String) {
        _uiState.value = _uiState.value.copy(note = value)
    }

    private fun save() {
        val s = _uiState.value
        val amount = s.amountInput.toBigDecimalOrNull()
        if (amount == null || amount <= BigDecimal.ZERO) {
            _uiState.value = s.copy(error = "Masukkan nominal yang valid")
            return
        }

        _uiState.value = s.copy(isSaving = true)
        viewModelScope.launch {
            val result = financeRepository.recordPayment(
                Payment(
                    studentId = s.studentId,
                    studentFeeId = s.selectedFeeId,
                    amount = amount,
                    note = s.note.ifBlank { null }
                )
            )
            result.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(saved = true, isSaving = false) },
                onFailure = { e -> _uiState.value = s.copy(isSaving = false, error = e.message) }
            )
        }
    }
}
