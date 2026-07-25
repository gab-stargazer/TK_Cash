package com.lelestacia.tkmanagement.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val studentId: Long,
    val studentFeeId: Long?,     // tagihan mana yang dibayar; null jika pemasukan umum
    val amount: Long,             // nominal dibayar hari ini
    val paymentDate: Long = System.currentTimeMillis(),
    val note: String? = null,
    val receiptNumber: String? = null,  // untuk cetak kuitansi
    val createdAt: Long = System.currentTimeMillis()
)
