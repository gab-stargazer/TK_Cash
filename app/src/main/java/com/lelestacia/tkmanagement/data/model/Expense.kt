package com.lelestacia.tkmanagement.data.model

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

    val category: ExpenseCategory,
    val amount: BigDecimal,
    val expenseDate: Long = Clock.System.now().toEpochMilliseconds(),
    val note: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long? = null
)
