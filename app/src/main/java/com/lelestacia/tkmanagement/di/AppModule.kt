package com.lelestacia.tkmanagement.di

import com.lelestacia.tkmanagement.data.AppDatabase
import com.lelestacia.tkmanagement.data.repository.*
import com.lelestacia.tkmanagement.ui.navigation.Destination
import com.lelestacia.tkmanagement.ui.navigation.Navigator
import com.lelestacia.tkmanagement.ui.screens.*
import com.lelestacia.tkmanagement.viewmodel.*
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val appModule = module {
    // Database
    single { AppDatabase.getInstance(get()) }

    // DAOs
    single { get<AppDatabase>().studentDao() }
    single { get<AppDatabase>().feeDao() }
    single { get<AppDatabase>().paymentDao() }
    single { get<AppDatabase>().expenseDao() }
    single { get<AppDatabase>().cashDao() }

    // Repositories
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<FeeRepository> { FeeRepositoryImpl(get()) }
    single<FinanceRepository> { FinanceRepositoryImpl(get(), get(), get(), get()) }

    // Navigator
    activityRetainedScope {
        scoped { Navigator(startDestination = Destination.Dashboard) }

        // Navigation Entries
        navigation<Destination.Dashboard> {
            val nav = get<Navigator>()
            DashboardScreen(
                viewModel = get(),
                onAddPayment = { nav.goTo(Destination.StudentList) },
                onAddExpense = { nav.goTo(Destination.AddExpense) },
                onOpenStudents = { nav.goTo(Destination.StudentList) },
                onOpenTunggakan = { nav.goTo(Destination.Tunggakan) }
            )
        }
        navigation<Destination.StudentList> {
            val nav = get<Navigator>()
            StudentListScreen(
                viewModel = get(),
                onOpenStudent = { id -> nav.goTo(Destination.StudentDetail(id)) },
                onAddStudent = { nav.goTo(Destination.AddStudent) }
            )
        }
        navigation<Destination.AddStudent> {
            val nav = get<Navigator>()
            AddStudentScreen(
                viewModel = get(),
                onSaved = { nav.popBackStack() },
                onBack = { nav.popBackStack() }
            )
        }
        navigation<Destination.StudentDetail> { key ->
            val nav = get<Navigator>()
            val vm = get<StudentDetailViewModel> { parametersOf(key.studentId) }
            StudentDetailScreen(
                viewModel = vm,
                onAddFee = {
                    val name = vm.uiState.value.student?.name ?: ""
                    nav.goTo(Destination.AddFee(key.studentId, name))
                },
                onAddPayment = { feeId ->
                    val name = vm.uiState.value.student?.name ?: ""
                    nav.goTo(Destination.AddPayment(key.studentId, name, feeId))
                },
                onOpenReceipt = { feeId ->
                    val name = vm.uiState.value.student?.name ?: ""
                    nav.goTo(Destination.AddPayment(key.studentId, name, feeId))
                },
                onBack = { nav.popBackStack() }
            )
        }
        navigation<Destination.AddFee> { key ->
            val nav = get<Navigator>()
            AddFeeScreen(
                viewModel = get { parametersOf(key.studentId, key.studentName) },
                onSaved = { nav.popBackStack() }
            )
        }
        navigation<Destination.AddPayment> { key ->
            val nav = get<Navigator>()
            AddPaymentScreen(
                viewModel = get { parametersOf(key.studentId, key.studentName, key.preselectedFeeId) },
                onSaved = { nav.popBackStack() }
            )
        }
        navigation<Destination.AddExpense> {
            val nav = get<Navigator>()
            AddExpenseScreen(
                viewModel = get(),
                onSaved = { nav.popBackStack() }
            )
        }
        navigation<Destination.Tunggakan> {
            val nav = get<Navigator>()
            TunggakanScreen(
                viewModel = get(),
                onOpenStudent = { id -> nav.goTo(Destination.StudentDetail(id)) }
            )
        }
    }

    // ViewModels
    viewModelOf(::DashboardViewModel)
    viewModelOf(::StudentListViewModel)
    viewModel { params -> StudentDetailViewModel(get(), get(), get(), params.get()) }
    viewModelOf(::AddStudentViewModel)
    viewModel { params ->
        AddPaymentViewModel(get(), get(), params.get(), params.get(), params.getOrNull())
    }
    viewModel { params ->
        AddFeeViewModel(get(), params.get(), params.get())
    }
    viewModelOf(::AddExpenseViewModel)
    viewModelOf(::TunggakanViewModel)
}
