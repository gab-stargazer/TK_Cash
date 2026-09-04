package com.lelestacia.tkmanagement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lelestacia.tkmanagement.data.dao.CashDao
import com.lelestacia.tkmanagement.data.dao.ExpenseDao
import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.dao.PaymentDao
import com.lelestacia.tkmanagement.data.dao.StudentDao
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee

/** Database Room aplikasi (murid, tagihan, pembayaran, pengeluaran) beserta DAO-nya. */
@Database(
    entities = [
        Student::class,
        StudentFee::class,
        Payment::class,
        Expense::class
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /** DAO data murid. */
    abstract fun studentDao(): StudentDao
    /** DAO tagihan murid. */
    abstract fun feeDao(): FeeDao
    /** DAO pembayaran. */
    abstract fun paymentDao(): PaymentDao
    /** DAO pengeluaran. */
    abstract fun expenseDao(): ExpenseDao
    /** DAO saldo kas. */
    abstract fun cashDao(): CashDao

    companion object {
        /** Migrasi v1 ke v2: menambahkan kolom `uniform_shoe_size` pada tabel students. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            /** Mengeksekusi ALTER TABLE untuk menambah kolom ukuran sepatu seragam. */
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE students ADD COLUMN uniform_shoe_size TEXT DEFAULT NULL")
            }
        }
    }
}
