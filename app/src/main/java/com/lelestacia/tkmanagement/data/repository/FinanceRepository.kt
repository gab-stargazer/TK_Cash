package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.CashDao
import com.lelestacia.tkmanagement.data.dao.ExpenseDao
import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.dao.PaymentDao
import com.lelestacia.tkmanagement.data.model.Expense
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.relation.PaymentWithFee
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

/** Sumber data keuangan (pembayaran, pengeluaran, saldo kas); abstraksi di atas DAO terkait. */
interface FinanceRepository {
    /** Seluruh pembayaran milik satu murid, terbaru di atas. */
    fun readPaymentsForStudentById(studentId: Long): Flow<List<Payment>>
    /** Pembayaran milik satu murid beserta tagihan terkait, terbaru di atas. */
    fun readPaymentsWithFeeForStudentById(studentId: Long): Flow<List<PaymentWithFee>>
    /** Seluruh pembayaran untuk satu tagihan, terlama di atas. */
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>>
    /** Mencatat pembayaran setelah validasi nominal; [Result] berisi ID pembayaran baru. */
    suspend fun recordPayment(payment: Payment): Result<Long>
    /** Seluruh pengeluaran, terbaru di atas. */
    fun getAllExpenses(): Flow<List<Expense>>
    /** Menyimpan pengeluaran setelah validasi nominal; [Result] berisi ID-nya. */
    suspend fun addExpense(expense: Expense): Result<Long>
    /** Saldo kas saat ini (pemasukan - pengeluaran). */
    suspend fun getCurrentBalance(): BigDecimal
    /** Total seluruh pemasukan. */
    suspend fun getTotalIncomeAllTime(): BigDecimal
    /** Total seluruh pengeluaran. */
    suspend fun getTotalExpenseAllTime(): BigDecimal
    /** Arus kas bersih pada rentang waktu (epoch ms). */
    suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal
}

/** Implementasi [FinanceRepository] yang meneruskan panggilan ke DAO keuangan. */
class FinanceRepositoryImpl(
    private val paymentDao: PaymentDao,
    private val expenseDao: ExpenseDao,
    private val cashDao: CashDao,
    private val feeDao: FeeDao
) : FinanceRepository {
    /** Meneruskan ke [PaymentDao.getPaymentsForStudent]. */
    override fun readPaymentsForStudentById(studentId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForStudent(studentId)

    /** Meneruskan ke [PaymentDao.getPaymentsWithFeeForStudent]. */
    override fun readPaymentsWithFeeForStudentById(studentId: Long): Flow<List<PaymentWithFee>> =
        paymentDao.getPaymentsWithFeeForStudent(studentId)

    /** Meneruskan ke [PaymentDao.getPaymentsForFee]. */
    override fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForFee(studentFeeId)

    /** Memvalidasi nominal (harus > 0 dan tidak melebihi sisa tagihan) lalu menyimpan via [PaymentDao]. */
    override suspend fun recordPayment(payment: Payment): Result<Long> {
        if (payment.amount <= BigDecimal.ZERO) return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
        if (payment.studentFeeId != null) {
            val status = feeDao.getFeeStatus(payment.studentFeeId)
            if (status != null && payment.amount > status.remaining) {
                return Result.failure(IllegalArgumentException("Nominal melebihi sisa tagihan (Rp ${status.remaining})"))
            }
        }
        return Result.success(paymentDao.insert(payment))
    }

    /** Meneruskan ke [ExpenseDao.getAll]. */
        override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAll()

        /** Memvalidasi nominal > 0 lalu menyimpan via [ExpenseDao]. */
        override suspend fun addExpense(expense: Expense): Result<Long> {
            if (expense.amount <= BigDecimal.ZERO) return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
            return Result.success(expenseDao.insert(expense))
        }

        /** Meneruskan ke [CashDao.getCurrentBalance]. */
        override suspend fun getCurrentBalance(): BigDecimal = cashDao.getCurrentBalance()

        /** Meneruskan ke [PaymentDao.getTotalIncomeAllTime]. */
        override suspend fun getTotalIncomeAllTime(): BigDecimal = paymentDao.getTotalIncomeAllTime()

        /** Meneruskan ke [ExpenseDao.getTotalExpenseAllTime]. */
        override suspend fun getTotalExpenseAllTime(): BigDecimal = expenseDao.getTotalExpenseAllTime()

        /** Meneruskan ke [CashDao.getNetCashBetween]. */
        override suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal =
            cashDao.getNetCashBetween(start, end)
}
