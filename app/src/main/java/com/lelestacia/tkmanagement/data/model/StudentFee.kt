package com.lelestacia.tkmanagement.data.model

import androidx.room.ColumnInfo
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
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("student_id")]
)
data class StudentFee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "fee_type")
    val feeType: FeeType,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "total_amount")
    val totalAmount: BigDecimal,
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)