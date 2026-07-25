package com.lelestacia.tkmanagement.data.relation

/**
 * Hasil query gabungan student_fees + SUM(payments) untuk daftar tunggakan.
 * remaining = totalAmount - paidAmount, hanya baris dengan remaining > 0 yang relevan.
 */
data class TunggakanItem(
    val studentFeeId: Long,
    val studentId: Long,
    val studentName: String,
    val feeLabel: String,
    val totalAmount: Long,
    val paidAmount: Long,
    val remaining: Long
)
