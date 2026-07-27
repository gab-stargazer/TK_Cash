package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Query
import java.math.BigDecimal

/**
 * Saldo kas = total semua pemasukan (payments) - total semua pengeluaran (expenses).
 * Setiap kali Payment atau Expense baru di-insert, query ini otomatis
 * mencerminkan saldo terbaru (tidak perlu kolom saldo tersimpan terpisah).
 */
@Dao
interface CashDao {
    @Query(
        """
        SELECT CAST(
            (SELECT COALESCE(SUM(amount), 0) FROM payments) -
            (SELECT COALESCE(SUM(amount), 0) FROM expenses)
        AS TEXT)
        """
    )
    suspend fun getCurrentBalance(): BigDecimal

    @Query(
        """
        SELECT CAST(
            (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE paymentDate BETWEEN :start AND :end) -
            (SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE expenseDate BETWEEN :start AND :end)
        AS TEXT)
        """
    )
    suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal
}
