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
 * Satu transaksi pemasukan (bendahara menerima uang dari wali murid).
 * Bisa berupa cicilan sebagian atau pelunasan penuh dari sebuah StudentFee.
 */
@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudentFee::class,
            parentColumns = ["id"],
            childColumns = ["student_fee_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("student_id"), Index("student_fee_id")]
)
@OptIn(ExperimentalTime::class)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "student_id")
    val studentId: Long,
    @ColumnInfo(name = "student_fee_id")
    val studentFeeId: Long?,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "payment_date")
    val paymentDate: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "receipt_number")
    val receiptNumber: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)