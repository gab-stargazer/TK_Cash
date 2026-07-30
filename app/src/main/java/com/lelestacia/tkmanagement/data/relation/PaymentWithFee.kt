package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee

data class PaymentWithFee(
    @Embedded val payment: Payment,

    @Relation(
        parentColumn = "student_fee_id",
        entityColumn = "id"
    )
    val fee: StudentFee?
)
