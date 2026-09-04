package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee

/**
 * Satu tagihan murid beserta seluruh pembayaran yang tercatat untuk tagihan tersebut.
 */
data class FeeWithPayments(
    /** Tagihan murid yang di-embed. */
    @Embedded val fee: StudentFee,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_fee_id"
    )
    /** Daftar pembayaran yang sudah dilakukan untuk tagihan ini. */
    val payments: List<Payment>
)
