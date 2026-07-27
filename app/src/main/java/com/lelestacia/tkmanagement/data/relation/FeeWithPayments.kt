package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.StudentFee

data class FeeWithPayments(
    @Embedded val fee: StudentFee,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentFeeId"
    )
    val payments: List<Payment>
)
