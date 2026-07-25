package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddExpenseUiState(
    val category: ExpenseCategory = ExpenseCategory.ATK,
    val amountInput: String = "",
    val note: String = "",
    val error: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

class AddExpenseViewModel(private val repository: CashRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun onCategoryChange(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(category = category, error = null)
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
            val result = repository.addExpense(
                Expense(category = s.category, amount = amount, note = s.note.ifBlank { null })
            )
            result.fold(
                onSuccess = { _uiState.value = AddExpenseUiState(saved = true) },
                onFailure = { e -> _uiState.value = s.copy(isSaving = false, error = e.message) }
            )
        }
    }
}
