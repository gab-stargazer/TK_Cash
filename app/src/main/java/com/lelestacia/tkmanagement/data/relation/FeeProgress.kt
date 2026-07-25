package com.lelestacia.tkmanagement.data.relation

import com.lelestacia.tkmanagement.data.model.FeeType

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
    val totalAmount: Long,
    val paidAmount: Long,
    val remaining: Long
)
