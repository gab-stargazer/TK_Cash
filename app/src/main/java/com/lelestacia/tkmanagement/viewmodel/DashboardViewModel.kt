package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.data.repository.FeeRepository
import com.lelestacia.tkmanagement.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal

/** State ringkas dashboard berisi saldo kas, total transaksi, dan tunggakan teratas. */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val saldoKas: BigDecimal = BigDecimal.ZERO,
    val totalPemasukan: BigDecimal = BigDecimal.ZERO,
    val totalPengeluaran: BigDecimal = BigDecimal.ZERO,
    val topTunggakan: List<TunggakanItem> = emptyList()
)

/** Event UI untuk dashboard. */
sealed interface DashboardUiEvent {
    object Refresh : DashboardUiEvent
}

/** ViewModel dashboard: memuat ringkasan kas dan daftar tunggakan teratas. */
class DashboardViewModel(
    private val financeRepository: FinanceRepository,
    private val feeRepository: FeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        onEvent(DashboardUiEvent.Refresh)
        observeTunggakan()
    }

    fun onEvent(event: DashboardUiEvent) {
        when (event) {
            DashboardUiEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val saldo = financeRepository.getCurrentBalance()
            val income = financeRepository.getTotalIncomeAllTime()
            val expense = financeRepository.getTotalExpenseAllTime()
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
            feeRepository.getTunggakanList().collectLatest { list ->
                _uiState.value = _uiState.value.copy(topTunggakan = list.take(5))
            }
        }
    }
}
