package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class AddFeeUiState(
    val studentId: Long = 0,
    val studentName: String = "",
    val feeType: FeeType = FeeType.SPP,
    val label: String = "",
    val labelManuallyEdited: Boolean = false,
    val amountInput: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

/** Menambahkan satu tagihan baru untuk seorang murid: SPP, seragam, buku, kegiatan, dll. */
class AddFeeViewModel(
    private val repository: CashRepository,
    private val studentId: Long,
    private val studentName: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFeeUiState())
    val uiState: StateFlow<AddFeeUiState> = _uiState.asStateFlow()

    init {
        val s = _uiState.value
        _uiState.value = s.copy(
            studentId = studentId,
            studentName = studentName,
            label = if (s.labelManuallyEdited) s.label else defaultLabelFor(s.feeType)
        )
    }

    private fun defaultLabelFor(type: FeeType) = when (type) {
        FeeType.PENDAFTARAN -> "Pendaftaran"
        FeeType.SPP -> "SPP"
        FeeType.SERAGAM -> "Seragam"
        FeeType.BUKU -> "Buku"
        FeeType.KEGIATAN -> "Kegiatan"
        FeeType.LAINNYA -> ""
    }

    fun onFeeTypeChange(type: FeeType) {
        val s = _uiState.value
        _uiState.value = s.copy(
            feeType = type,
            label = if (s.labelManuallyEdited) s.label else defaultLabelFor(type),
            error = null
        )
    }

    fun onLabelChange(value: String) {
        _uiState.value = _uiState.value.copy(label = value, labelManuallyEdited = true, error = null)
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amountInput = value.filter { it.isDigit() }, error = null)
    }

    fun save() {
        val s = _uiState.value
        val amount = s.amountInput.toBigDecimalOrNull()
        if (s.label.isBlank()) {
            _uiState.value = s.copy(error = "Nama tagihan wajib diisi")
            return
        }
        if (amount == null || amount <= BigDecimal.ZERO) {
            _uiState.value = s.copy(error = "Masukkan nominal yang valid")
            return
        }

        _uiState.value = s.copy(isSaving = true)
        viewModelScope.launch {
            repository.addFee(
                StudentFee(
                    studentId = s.studentId,
                    feeType = s.feeType,
                    label = s.label.trim(),
                    totalAmount = amount
                )
            )
            _uiState.value = AddFeeUiState(saved = true)
        }
    }
}
