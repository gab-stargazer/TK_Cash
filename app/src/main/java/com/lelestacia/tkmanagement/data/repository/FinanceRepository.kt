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

interface FinanceRepository {
    fun readPaymentsForStudentById(studentId: Long): Flow<List<Payment>>
    fun readPaymentsWithFeeForStudentById(studentId: Long): Flow<List<PaymentWithFee>>
    fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>>
    suspend fun recordPayment(payment: Payment): Result<Long>
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Result<Long>
    suspend fun getCurrentBalance(): BigDecimal
    suspend fun getTotalIncomeAllTime(): BigDecimal
    suspend fun getTotalExpenseAllTime(): BigDecimal
    suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal
}

class FinanceRepositoryImpl(
    private val paymentDao: PaymentDao,
    private val expenseDao: ExpenseDao,
    private val cashDao: CashDao,
    private val feeDao: FeeDao
) : FinanceRepository {
    override fun readPaymentsForStudentById(studentId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForStudent(studentId)

    override fun readPaymentsWithFeeForStudentById(studentId: Long): Flow<List<PaymentWithFee>> =
        paymentDao.getPaymentsWithFeeForStudent(studentId)

    override fun getPaymentsForFee(studentFeeId: Long): Flow<List<Payment>> =
        paymentDao.getPaymentsForFee(studentFeeId)

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

    override fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAll()

    override suspend fun addExpense(expense: Expense): Result<Long> {
        if (expense.amount <= BigDecimal.ZERO) return Result.failure(IllegalArgumentException("Nominal harus lebih dari 0"))
        return Result.success(expenseDao.insert(expense))
    }

    override suspend fun getCurrentBalance(): BigDecimal = cashDao.getCurrentBalance()
    override suspend fun getTotalIncomeAllTime(): BigDecimal = paymentDao.getTotalIncomeAllTime()
    override suspend fun getTotalExpenseAllTime(): BigDecimal = expenseDao.getTotalExpenseAllTime()
    override suspend fun getNetCashBetween(start: Long, end: Long): BigDecimal =
        cashDao.getNetCashBetween(start, end)
}
