package com.lelestacia.tkmanagement.data.relation

import com.lelestacia.tkmanagement.data.model.FeeType
import java.math.BigDecimal

/**
 * Progres satu tagihan (dipakai di layar detail murid).
 * Dihasilkan langsung dari JOIN student_fees + payments sebagai Flow,
 * jadi begitu ada Payment baru masuk ke Room, baris ini otomatis
 * ter-emit ulang tanpa perlu query manual per-fee.
 */
data class FeeProgress(
    val studentFeeId: Long,
    val studentId: Long,
    val feeType: FeeType,
    val label: String,
    val totalAmount: BigDecimal,
    val paidAmount: BigDecimal,
    val remaining: BigDecimal
)
