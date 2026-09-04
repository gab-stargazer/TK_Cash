package com.lelestacia.tkmanagement.data.relation

import java.math.BigDecimal

/** Satu baris tunggakan hasil gabungan tagihan, pembayaran, dan data murid.
 * remaining = totalAmount - paidAmount, hanya baris dengan remaining > 0 yang relevan.
 */
data class TunggakanItem(
    val studentFeeId: Long,
    val studentId: Long,
    val studentName: String,
    val guardianName: String,
    val feeLabel: String,
    val totalAmount: BigDecimal,
    val paidAmount: BigDecimal,
    val remaining: BigDecimal
)
