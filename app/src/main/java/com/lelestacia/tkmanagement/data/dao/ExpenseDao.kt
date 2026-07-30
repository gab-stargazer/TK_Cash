package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense): Long

    @Query("SELECT * FROM expenses ORDER BY expense_date DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY expense_date DESC")
    fun getByCategory(category: ExpenseCategory): Flow<List<Expense>>

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM expenses WHERE expense_date BETWEEN :start AND :end")
    suspend fun getTotalExpenseBetween(start: Long, end: Long): BigDecimal

    @Query("SELECT CAST(COALESCE(SUM(amount), '0') AS TEXT) FROM expenses")
    suspend fun getTotalExpenseAllTime(): BigDecimal
}
