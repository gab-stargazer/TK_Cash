package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.relation.PaymentWithFee
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE student_id = :studentId ORDER BY payment_date DESC")
    fun getPaymentsForStudent(studentId: Long): Flow<List<Payment>>

    @Transaction
    @Query("SELECT * FROM payments WHERE student_id = :studentId ORDER BY payment_date DESC")
    fun getPaymentsWithFeeForStudent(studentId: Long): Flow<List<PaymentWithFee>>

    @Query("SELECT * FROM payments WHERE student_fee_id = :studentFeeId ORDER BY payment_date ASC")
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>>

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM payments WHERE payment_date BETWEEN :start AND :end")
    suspend fun getTotalIncomeBetween(start: Long, end: Long): BigDecimal

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM payments")
    suspend fun getTotalIncomeAllTime(): BigDecimal
}
