package com.lelestacia.tkmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Catatan pengeluaran operasional sekolah yang tersimpan di tabel `expenses`. */
@OptIn(ExperimentalTime::class)
@Entity(tableName = "expenses")
data class Expense(
    /** ID unik, dibuat otomatis. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Kategori pengeluaran. */
    @ColumnInfo(name = "category")
    val category: ExpenseCategory,
    /** Nominal pengeluaran. */
    @ColumnInfo(name = "amount")
    val amount: BigDecimal,
    /** Waktu pengeluaran (epoch ms). */
    @ColumnInfo(name = "expense_date")
    val expenseDate: Long = Clock.System.now().toEpochMilliseconds(),
    /** Catatan opsional. */
    @ColumnInfo(name = "note")
    val note: String? = null,
    /** Waktu pembuatan catatan (epoch ms). */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    /** Waktu pembaruan terakhir, null bila belum pernah diubah. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)