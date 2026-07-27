package com.lelestacia.tkmanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Satu tagihan untuk seorang murid, misalnya:
 * - "SPP Juli 2026" total Rp 350.000
 * - "Pendaftaran" total Rp 1.500.000
 * - "Seragam" total Rp 400.000
 *
 * Payment (pemasukan) di-link ke StudentFee ini agar cicilan & pelunasan
 * bisa dihitung: sisaTagihan = totalAmount - SUM(payments.amount)
 */
@OptIn(ExperimentalTime::class)
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
    val label: String,
    val totalAmount: BigDecimal,
    val note: String? = null,
    val dueDate: Long? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long? = null
)
