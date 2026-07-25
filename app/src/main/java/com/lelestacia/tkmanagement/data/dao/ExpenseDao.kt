package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense): Long

    @Query("SELECT * FROM expenses ORDER BY expenseDate DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY expenseDate DESC")
    fun getByCategory(category: ExpenseCategory): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE expenseDate BETWEEN :start AND :end")
    suspend fun getTotalExpenseBetween(start: Long, end: Long): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses")
    suspend fun getTotalExpenseAllTime(): Long
}
