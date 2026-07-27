package com.lelestacia.tkmanagement.data.model

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
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudentFee::class,
            parentColumns = ["id"],
            childColumns = ["studentFeeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("studentId"), Index("studentFeeId")]
)
@OptIn(ExperimentalTime::class)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentFeeId: Long?,
    val amount: BigDecimal,
    val paymentDate: Long = Clock.System.now().toEpochMilliseconds(),
    val note: String? = null,
    val receiptNumber: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long? = null
)
