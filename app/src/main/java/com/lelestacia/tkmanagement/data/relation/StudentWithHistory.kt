package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee

/** Untuk tombol "Lihat Riwayat Lengkap" di halaman satu murid. */
data class StudentWithFees(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_id"
    )
    val fees: List<StudentFee>
)

data class StudentWithPayments(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_id"
    )
    val payments: List<Payment>
)

data class StudentWithFullHistory(
    @Embedded val student: Student,
    @Relation(
        entity = StudentFee::class,
        parentColumn = "id",
        entityColumn = "student_id"
    )
    val fees: List<FeeWithPayments>
)
