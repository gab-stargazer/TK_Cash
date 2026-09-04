package com.lelestacia.tkmanagement.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee

/** Untuk tombol "Lihat Riwayat Lengkap" di halaman satu murid. */
data class StudentWithFees(
    /** Data murid yang di-embed. */
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_id"
    )
    /** Seluruh tagihan murid. */
    val fees: List<StudentFee>
)

/** Satu murid beserta seluruh pembayarannya. */
data class StudentWithPayments(
    /** Data murid yang di-embed. */
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "student_id"
    )
    /** Seluruh pembayaran murid. */
    val payments: List<Payment>
)

/** Riwayat lengkap satu murid: data murid beserta tagihan dan pembayaran tiap tagihan. */
data class StudentWithFullHistory(
    /** Data murid yang di-embed. */
    @Embedded val student: Student,
    @Relation(
        entity = StudentFee::class,
        parentColumn = "id",
        entityColumn = "student_id"
    )
    /** Seluruh tagihan murid beserta pembayarannya masing-masing. */
    val fees: List<FeeWithPayments>
)
