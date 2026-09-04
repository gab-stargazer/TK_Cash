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
    /** ID tagihan murid (student_fees.id). */
    val studentFeeId: Long,
    /** ID murid pemilik tagihan. */
    val studentId: Long,
    /** Jenis tagihan. */
    val feeType: FeeType,
    /** Label tagihan (misalnya "SPP Januari 2025"). */
    val label: String,
    /** Nilai total tagihan. */
    val totalAmount: BigDecimal,
    /** Total nominal yang sudah dibayar. */
    val paidAmount: BigDecimal,
    /** Sisa tagihan = totalAmount - paidAmount. */
    val remaining: BigDecimal
)
