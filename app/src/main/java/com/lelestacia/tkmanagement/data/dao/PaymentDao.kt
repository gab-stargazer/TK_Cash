package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getPaymentsForStudent(studentId: Long): Flow<List<Payment>>

    @Query("SELECT * FROM payments WHERE studentFeeId = :studentFeeId ORDER BY paymentDate ASC")
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE paymentDate BETWEEN :start AND :end")
    suspend fun getTotalIncomeBetween(start: Long, end: Long): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments")
    suspend fun getTotalIncomeAllTime(): Long
}
