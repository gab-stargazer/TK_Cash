package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import com.lelestacia.tkmanagement.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ReceiptUiState(
    val isLoading: Boolean = true,
    val feeStatus: TunggakanItem? = null,
    val payments: List<Payment> = emptyList()
)

sealed interface ReceiptUiEvent

class ReceiptViewModel(
    private val feeRepository: FeeRepository,
    private val financeRepository: FinanceRepository,
    private val studentFeeId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEvent(event: ReceiptUiEvent) { }

    private fun loadData() {
        viewModelScope.launch {
            val status = feeRepository.getFeeStatus(studentFeeId)
            _uiState.value = _uiState.value.copy(isLoading = false, feeStatus = status)
        }

        viewModelScope.launch {
            financeRepository.getPaymentsForFee(studentFeeId).collectLatest { list ->
                _uiState.value = _uiState.value.copy(payments = list)
            }
        }
    }
}
