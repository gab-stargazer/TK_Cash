package com.lelestacia.tkmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "category")
    val category: ExpenseCategory,
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    @ColumnInfo(name = "expense_date")
    val expenseDate: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "note")
    val note: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)