package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.CashDao
import com.lelestacia.tkmanagement.data.dao.ExpenseDao
import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.dao.PaymentDao
import com.lelestacia.tkmanagement.data.dao.StudentDao
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.relation.StudentWithFees
import com.lelestacia.tkmanagement.data.relation.StudentWithPayments
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/**
 * Satu pintu masuk untuk semua akses data. ViewModel tidak menyentuh DAO
 * langsung supaya sumber kebenaran (dan aturan bisnis kecil, mis. validasi
 * nominal) terpusat di sini.
 */
class CashRepository(
    private val studentDao: StudentDao,
    private val feeDao: FeeDao,
    private val paymentDao: PaymentDao,
    private val expenseDao: ExpenseDao,
    private val cashDao: CashDao
) {
    // ---- Murid ----
    fun getActiveStudents(): Flow<List<Student>> = studentDao.getAllActive()
    fun searchStudents(query: String): Flow<List<Student>> = studentDao.search(query)
    fun readStudentDataById(id: Long): Flow<Student> = studentDao.readById(id)
    suspend fun addStudent(student: Student): Long = studentDao.insert(student)
    suspend fun updateStudent(student: Student) = studentDao.update(student)
    suspend fun getStudentWithFees(id: Long): StudentWithFees? = studentDao.getWithFees(id)
    suspend fun getStudentWithPayments(id: Long): StudentWithPayments? = studentDao.getWithPayments(id)

    // ---- Tagihan ----
    fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>> = feeDao.getFeesForStudent(studentId)
    fun readFeeProgressForStudentById(studentId: Long): Flow<List<FeeProgress>> =
        feeDao.getFeeProgressForStudent(studentId)
    suspend fun addFee(fee: StudentFee): Long = feeDao.insert(fee)
    fun getTunggakanList(): Flow<List<TunggakanItem>> = feeDao.getTunggakanList()
    suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem? = feeDao.getFeeStatus(studentFeeId)

    // ---- Pemasukan ----
    fun readPaymentsForStudentById(studentId: Long): Flow<List<Payment>> = paymentDao.getPaymentsForStudent(studentId)
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>> = paymentDao.getPaymentsForFee(studentFeeId)

    /** Mencatat pembayaran; menolak jika nominal melebihi sisa tagihan. */
    suspend fun recordPayment(payment: Payment): Result<Long> {
        if (payment.amount <= BigDecimal.ZERO) return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
        if (payment.studentFeeId != null) {
            val status = feeDao.getFeeStatus(payment.studentFeeId)
            if (status != null && payment.amount > status.remaining) {
                return Result.failure(IllegalArgumentException("Nominal melebihi sisa tagihan (Rp ${status.remaining})"))
            }
        }
        return Result.success(paymentDao.insert(payment))
    }

    // ---- Pengeluaran ----
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAll()
    suspend fun addExpense(expense: Expense): Result<Long> {
        if (expense.amount <= BigDecimal.ZERO) return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
        return Result.success(expenseDao.insert(expense))
    }

    // ---- Kas & Laporan ----
    suspend fun getCurrentBalance(): BigDecimal = cashDao.getCurrentBalance()
    suspend fun getTotalIncomeAllTime(): BigDecimal = paymentDao.getTotalIncomeAllTime()
    suspend fun getTotalExpenseAllTime(): BigDecimal = expenseDao.getTotalExpenseAllTime()
    suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal = cashDao.getNetCashBetween(start, end)
}
