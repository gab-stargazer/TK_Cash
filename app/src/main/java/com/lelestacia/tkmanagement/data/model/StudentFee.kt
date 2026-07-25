package com.lelestacia.tkmanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Satu tagihan untuk seorang murid, misalnya:
 * - "SPP Juli 2026" total Rp 350.000
 * - "Pendaftaran" total Rp 1.500.000
 * - "Seragam" total Rp 400.000
 *
 * Payment (pemasukan) di-link ke StudentFee ini agar cicilan & pelunasan
 * bisa dihitung: sisaTagihan = totalAmount - SUM(payments.amount)
 */
@Entity(
    tableName = "student_fees",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId")]
)
data class StudentFee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val studentId: Long,
    val feeType: FeeType,
    val label: String,                  // contoh: "SPP Juli 2026"
    val totalAmount: Long,              // nominal tagihan, dalam Rupiah (Long, hindari Double)
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
