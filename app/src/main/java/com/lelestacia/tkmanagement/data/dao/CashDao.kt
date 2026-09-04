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
    /** Saldo kas saat ini = total pemasukan dikurangi total pengeluaran. */
    suspend fun getCurrentBalance(): BigDecimal

    @Query(
        """
        SELECT CAST(
            (SELECT COALESCE(SUM(amount), 0) FROM payments WHERE payment_date BETWEEN :start AND :end) -
            (SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE expense_date BETWEEN :start AND :end)
        AS TEXT)
        """
    )
    /** Arus kas bersih (pemasukan - pengeluaran) pada rentang waktu (epoch ms). */
    suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal
}
