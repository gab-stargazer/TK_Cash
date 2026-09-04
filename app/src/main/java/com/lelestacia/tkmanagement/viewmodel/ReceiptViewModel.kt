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

/** State layar kwitansi: status tagihan dan daftar pembayarannya. */
data class ReceiptUiState(
    /** Penanda bahwa data masih dimuat. */
    val isLoading: Boolean = true,
    /** Status tunggakan tagihan, null bila tagihan tidak ditemukan. */
    val feeStatus: TunggakanItem? = null,
    /** Daftar pembayaran tagihan (terlama di atas). */
    val payments: List<Payment> = emptyList()
)

/** Event layar kwitansi (saat ini belum dipakai). */
sealed interface ReceiptUiEvent

/** ViewModel kwitansi: memuat status tagihan dan pembayaran lalu mengeksposnya sebagai state layar. */
class ReceiptViewModel(
    private val feeRepository: FeeRepository,
    private val financeRepository: FinanceRepository,
    private val studentFeeId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiptUiState())
    /** State UI terbaru untuk layar kwitansi. */
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /** Menerima event UI; saat ini tidak melakukan apa pun. */
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
