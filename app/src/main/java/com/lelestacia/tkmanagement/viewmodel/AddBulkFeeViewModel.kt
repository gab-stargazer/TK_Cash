package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import com.lelestacia.tkmanagement.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar

data class AddBulkFeeUiState(
    val feeType: FeeType = FeeType.SPP,
    val label: String = "",
    val amountInput: String = "",
    val note: String = "",
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

sealed interface AddBulkFeeUiEvent {
    data class TypeChange(val type: FeeType) : AddBulkFeeUiEvent
    data class LabelChange(val value: String) : AddBulkFeeUiEvent
    data class AmountChange(val value: String) : AddBulkFeeUiEvent
    data class NoteChange(val value: String) : AddBulkFeeUiEvent
    data class MonthChange(val month: Int) : AddBulkFeeUiEvent
    data class YearChange(val year: Int) : AddBulkFeeUiEvent
    object Save : AddBulkFeeUiEvent
}

class AddBulkFeeViewModel(
    private val feeRepository: FeeRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBulkFeeUiState())
    val uiState: StateFlow<AddBulkFeeUiState> = _uiState.asStateFlow()

    init {
        updateLabel()
    }

    fun onEvent(event: AddBulkFeeUiEvent) {
        when (event) {
            is AddBulkFeeUiEvent.TypeChange -> {
                _uiState.value = _uiState.value.copy(feeType = event.type)
                updateLabel()
            }
            is AddBulkFeeUiEvent.LabelChange -> {
                if (_uiState.value.feeType == FeeType.LAINNYA) {
                    _uiState.value = _uiState.value.copy(label = event.value)
                }
            }
            is AddBulkFeeUiEvent.AmountChange -> onAmountChange(event.value)
            is AddBulkFeeUiEvent.NoteChange -> _uiState.value = _uiState.value.copy(note = event.value)
            is AddBulkFeeUiEvent.MonthChange -> {
                _uiState.value = _uiState.value.copy(selectedMonth = event.month)
                updateLabel()
            }
            is AddBulkFeeUiEvent.YearChange -> {
                _uiState.value = _uiState.value.copy(selectedYear = event.year)
                updateLabel()
            }
            AddBulkFeeUiEvent.Save -> save()
        }
    }

    private fun updateLabel() {
        val s = _uiState.value
        val newLabel = when (s.feeType) {
            FeeType.SPP -> "SPP ${monthName(s.selectedMonth)} ${s.selectedYear}"
            FeeType.PEMBANGUNAN -> "Pendaftaran"
            FeeType.SERAGAM -> "Seragam"
            FeeType.BUKU -> "Buku"
            FeeType.KEGIATAN -> "Kegiatan"
            FeeType.LAINNYA -> s.label
        }
        _uiState.value = _uiState.value.copy(label = newLabel)
    }

    private fun monthName(month: Int): String = when (month) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> ""
    }

    private fun onAmountChange(value: String) {
        if (value.all { it.isDigit() }) {
            val cleanValue = if (value.startsWith("0") && value.length > 1) {
                value.trimStart('0')
            } else {
                value
            }
            _uiState.value = _uiState.value.copy(amountInput = cleanValue, error = null)
        }
    }

    private fun save() {
        val s = _uiState.value
        val amount = s.amountInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
        
        if (s.label.isBlank()) {
            _uiState.value = s.copy(error = "Label tidak boleh kosong")
            return
        }
        if (amount <= BigDecimal.ZERO) {
            _uiState.value = s.copy(error = "Nominal harus lebih dari 0")
            return
        }

        _uiState.value = s.copy(isSaving = true)
        viewModelScope.launch {
            try {
                val studentIds = studentRepository.getNonGraduatedStudentIds()
                if (studentIds.isEmpty()) {
                    _uiState.value = s.copy(error = "Tidak ada murid aktif", isSaving = false)
                    return@launch
                }
                
                val template = StudentFee(
                    studentId = 0, // Placeholder
                    feeType = s.feeType,
                    label = s.label,
                    totalAmount = amount,
                    note = s.note.ifBlank { null }
                )
                
                feeRepository.addBulkFee(template, studentIds)
                _uiState.value = _uiState.value.copy(saved = true, isSaving = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isSaving = false)
            }
        }
    }
}
