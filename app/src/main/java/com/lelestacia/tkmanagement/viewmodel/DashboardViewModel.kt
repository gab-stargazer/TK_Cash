package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val saldoKas: Long = 0,
    val totalPemasukan: Long = 0,
    val totalPengeluaran: Long = 0,
    val topTunggakan: List<TunggakanItem> = emptyList()
)

class DashboardViewModel(private val repository: CashRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
        observeTunggakan()
    }

    /** Panggil ulang setiap kembali ke Dashboard (mis. setelah input transaksi). */
    fun refresh() {
        viewModelScope.launch {
            val saldo = repository.getCurrentBalance()
            val income = repository.getTotalIncomeAllTime()
            val expense = repository.getTotalExpenseAllTime()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                saldoKas = saldo,
                totalPemasukan = income,
                totalPengeluaran = expense
            )
        }
    }

    private fun observeTunggakan() {
        viewModelScope.launch {
            repository.getTunggakanList().collectLatest { list ->
                _uiState.value = _uiState.value.copy(topTunggakan = list.take(5))
            }
        }
    }
}
