package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee

/**
 * Satu pembayaran beserta tagihan yang dibayarnya (nullable bila tagihan tidak ditemukan).
 */
data class PaymentWithFee(
    /** Pembayaran yang di-embed. */
    @Embedded val payment: Payment,

    @Relation(
        parentColumn = "student_fee_id",
        entityColumn = "id"
    )
    /** Tagihan terkait pembayaran ini, null bila tagihan sudah tidak ada. */
    val fee: StudentFee?
)
