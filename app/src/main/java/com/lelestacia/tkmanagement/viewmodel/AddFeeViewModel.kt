package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Calendar

data class AddFeeUiState(
    val studentId: Long = 0,
    val studentName: String = "",
    val feeType: FeeType = FeeType.SPP,
    val label: String = "",
    val labelManuallyEdited: Boolean = false,
    val amountInput: String = "",
    val note: String = "",
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

sealed interface AddFeeUiEvent {
    data class TypeChange(val type: FeeType) : AddFeeUiEvent
    data class LabelChange(val value: String) : AddFeeUiEvent
    data class AmountChange(val value: String) : AddFeeUiEvent
    data class NoteChange(val value: String) : AddFeeUiEvent
    data class MonthChange(val month: Int) : AddFeeUiEvent
    data class YearChange(val year: Int) : AddFeeUiEvent
    object Save : AddFeeUiEvent
}

/** Menambahkan satu tagihan baru untuk seorang murid: SPP, seragam, buku, kegiatan, dll. */
class AddFeeViewModel(
    private val repository: FeeRepository,
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
            label = generateLabel(s.feeType, s.selectedMonth, s.selectedYear)
        )
    }

    fun onEvent(event: AddFeeUiEvent) {
        when (event) {
            is AddFeeUiEvent.TypeChange -> onFeeTypeChange(event.type)
            is AddFeeUiEvent.LabelChange -> onLabelChange(event.value)
            is AddFeeUiEvent.AmountChange -> onAmountChange(event.value)
            is AddFeeUiEvent.NoteChange -> onNoteChange(event.value)
            is AddFeeUiEvent.MonthChange -> onMonthChange(event.month)
            is AddFeeUiEvent.YearChange -> onYearChange(event.year)
            AddFeeUiEvent.Save -> save()
        }
    }

    private fun generateLabel(type: FeeType, month: Int, year: Int): String {
        return when (type) {
            FeeType.SPP -> "SPP ${monthName(month)} $year"
            FeeType.PEMBANGUNAN -> "Pendaftaran"
            FeeType.SERAGAM -> "Seragam"
            FeeType.BUKU -> "Buku"
            FeeType.KEGIATAN -> "Kegiatan"
            FeeType.LAINNYA -> ""
        }
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

    private fun onFeeTypeChange(type: FeeType) {
        val s = _uiState.value
        _uiState.value = s.copy(
            feeType = type,
            label = generateLabel(type, s.selectedMonth, s.selectedYear),
            error = null
        )
    }

    private fun onLabelChange(value: String) {
        if (_uiState.value.feeType == FeeType.LAINNYA) {
            _uiState.value = _uiState.value.copy(label = value, labelManuallyEdited = true, error = null)
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

    private fun onMonthChange(month: Int) {
        val s = _uiState.value
        _uiState.value = s.copy(
            selectedMonth = month,
            label = generateLabel(s.feeType, month, s.selectedYear)
        )
    }

    private fun onYearChange(year: Int) {
        val s = _uiState.value
        _uiState.value = s.copy(
            selectedYear = year,
            label = generateLabel(s.feeType, s.selectedMonth, year)
        )
    }

    private fun save() {
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
                    totalAmount = amount,
                    note = s.note.ifBlank { null }
                )
            )
            _uiState.value = _uiState.value.copy(saved = true, isSaving = false)
        }
    }
}
