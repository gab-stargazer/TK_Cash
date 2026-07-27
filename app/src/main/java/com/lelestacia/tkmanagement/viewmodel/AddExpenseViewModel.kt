package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

data class AddExpenseUiState(
    val category: ExpenseCategory = ExpenseCategory.ATK,
    val amountInput: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

sealed interface AddExpenseUiEvent {
    data class CategoryChange(val category: ExpenseCategory) : AddExpenseUiEvent
    data class AmountChange(val value: String) : AddExpenseUiEvent
    data class NoteChange(val value: String) : AddExpenseUiEvent
    object Save : AddExpenseUiEvent
}

class AddExpenseViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddExpenseUiEvent) {
        when (event) {
            is AddExpenseUiEvent.CategoryChange -> onCategoryChange(event.category)
            is AddExpenseUiEvent.AmountChange -> onAmountChange(event.value)
            is AddExpenseUiEvent.NoteChange -> onNoteChange(event.value)
            AddExpenseUiEvent.Save -> save()
        }
    }

    private fun onCategoryChange(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(category = category, error = null)
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
            val result = repository.addExpense(
                Expense(category = s.category, amount = amount, note = s.note.ifBlank { null })
            )
            result.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(saved = true, isSaving = false) },
                onFailure = { e -> _uiState.value = s.copy(isSaving = false, error = e.message) }
            )
        }
    }
}
