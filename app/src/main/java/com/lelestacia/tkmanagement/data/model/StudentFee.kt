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
/** Tagihan milik satu murid. */
data class StudentFee(
    /** ID unik, dibuat otomatis. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** ID murid pemilik tagihan. */
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    /** Jenis tagihan. */
    @ColumnInfo(name = "fee_type")
    val feeType: FeeType,
    /** Label tagihan (mis. "SPP Juli 2026"). */
    @ColumnInfo(name = "label")
    val label: String,
    /** Nilai total tagihan. */
    @ColumnInfo(name = "total_amount")
    val totalAmount: BigDecimal,
    /** Catatan opsional. */
    @ColumnInfo(name = "note")
    val note: String? = null,
    /** Batas akhir pembayaran, opsional. */
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,
    /** Waktu pembuatan tagihan (epoch ms). */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    /** Waktu pembaruan terakhir, null bila belum pernah diubah. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)