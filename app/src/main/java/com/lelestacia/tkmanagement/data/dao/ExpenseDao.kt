package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/** DAO penyimpanan pengeluaran (expenses). */
@Dao
interface ExpenseDao {
    @Insert
    /** Menyimpan pengeluaran baru dan mengembalikan ID-nya. */
    suspend fun insert(expense: Expense): Long

    @Query("SELECT * FROM expenses ORDER BY expense_date DESC")
    /** Seluruh pengeluaran, terbaru di atas. */
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY expense_date DESC")
    /** Pengeluaran untuk satu kategori, terbaru di atas. */
    fun getByCategory(category: ExpenseCategory): Flow<List<Expense>>

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM expenses WHERE expense_date BETWEEN :start AND :end")
    /** Total nominal pengeluaran antara dua waktu (epoch ms). */
    suspend fun getTotalExpenseBetween(start: Long, end: Long): BigDecimal

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM expenses")
    /** Total nominal seluruh pengeluaran. */
    suspend fun getTotalExpenseAllTime(): BigDecimal
}
