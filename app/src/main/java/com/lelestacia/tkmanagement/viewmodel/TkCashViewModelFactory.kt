package com.lelestacia.tkmanagement.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.lelestacia.tkmanagement.data.AppDatabase
import com.lelestacia.tkmanagement.data.repository.CashRepository

/**
 * Factory sederhana tanpa Hilt/Koin. Setiap ViewModel di app ini menerima
 * satu CashRepository yang sama, dibangun dari AppDatabase singleton.
 */
class TkCashViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    private val repository: CashRepository by lazy {
        val db = AppDatabase.getInstance(application)
        CashRepository(
            studentDao = db.studentDao(),
            feeDao = db.feeDao(),
            paymentDao = db.paymentDao(),
            expenseDao = db.expenseDao(),
            cashDao = db.cashDao()
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(StudentListViewModel::class.java) ->
                StudentListViewModel(repository) as T
            modelClass.isAssignableFrom(StudentDetailViewModel::class.java) ->
                StudentDetailViewModel(repository) as T
            modelClass.isAssignableFrom(AddStudentViewModel::class.java) ->
                AddStudentViewModel(repository) as T
            modelClass.isAssignableFrom(AddPaymentViewModel::class.java) ->
                AddPaymentViewModel(repository) as T
            modelClass.isAssignableFrom(AddFeeViewModel::class.java) ->
                AddFeeViewModel(repository) as T
            modelClass.isAssignableFrom(AddExpenseViewModel::class.java) ->
                AddExpenseViewModel(repository) as T
            modelClass.isAssignableFrom(TunggakanViewModel::class.java) ->
                TunggakanViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
