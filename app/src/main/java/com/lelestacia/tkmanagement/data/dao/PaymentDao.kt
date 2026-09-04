package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.relation.PaymentWithFee
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/** DAO penyimpanan pembayaran (payments). */
@Dao
interface PaymentDao {
    @Insert
    /** Menyimpan pembayaran baru dan mengembalikan ID-nya. */
    suspend fun insert(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE student_id = :studentId ORDER BY payment_date DESC")
    /** Seluruh pembayaran milik satu murid, terbaru di atas. */
    fun getPaymentsForStudent(studentId: Long): Flow<List<Payment>>

    @Transaction
    @Query("SELECT * FROM payments WHERE student_id = :studentId ORDER BY payment_date DESC")
    /** Pembayaran milik satu murid beserta tagihan terkait, terbaru di atas. */
    fun getPaymentsWithFeeForStudent(studentId: Long): Flow<List<PaymentWithFee>>

    @Query("SELECT * FROM payments WHERE student_fee_id = :studentFeeId ORDER BY payment_date ASC")
    /** Pembayaran untuk satu tagihan, terlama di atas. */
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>>

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM payments WHERE payment_date BETWEEN :start AND :end")
    /** Total pemasukan antara dua waktu (epoch ms). */
    suspend fun getTotalIncomeBetween(start: Long, end: Long): BigDecimal

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM payments")
    /** Total seluruh pemasukan. */
    suspend fun getTotalIncomeAllTime(): BigDecimal
}
