package com.lelestacia.tkmanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lelestacia.tkmanagement.ui.screens.AddExpenseScreen
import com.lelestacia.tkmanagement.ui.screens.AddFeeScreen
import com.lelestacia.tkmanagement.ui.screens.AddPaymentScreen
import com.lelestacia.tkmanagement.ui.screens.AddStudentScreen
import com.lelestacia.tkmanagement.ui.screens.DashboardScreen
import com.lelestacia.tkmanagement.ui.screens.StudentDetailScreen
import com.lelestacia.tkmanagement.ui.screens.StudentListScreen
import com.lelestacia.tkmanagement.ui.screens.TunggakanScreen
import com.lelestacia.tkmanagement.viewmodel.AddExpenseViewModel
import com.lelestacia.tkmanagement.viewmodel.AddFeeViewModel
import com.lelestacia.tkmanagement.viewmodel.AddPaymentViewModel
import com.lelestacia.tkmanagement.viewmodel.AddStudentViewModel
import com.lelestacia.tkmanagement.viewmodel.DashboardViewModel
import com.lelestacia.tkmanagement.viewmodel.StudentDetailViewModel
import com.lelestacia.tkmanagement.viewmodel.StudentListViewModel
import com.lelestacia.tkmanagement.viewmodel.TkCashViewModelFactory
import com.lelestacia.tkmanagement.viewmodel.TunggakanViewModel
import java.net.URLDecoder
import java.net.URLEncoder

private object Routes {
    const val DASHBOARD = "dashboard"
    const val STUDENTS = "students"
    const val ADD_STUDENT = "students/add"
    const val STUDENT_DETAIL = "students/{studentId}"
    const val ADD_PAYMENT = "payment/add/{studentId}/{studentName}?feeId={feeId}"
    const val ADD_FEE = "fee/add/{studentId}/{studentName}"
    const val ADD_EXPENSE = "expense/add"
    const val TUNGGAKAN = "tunggakan"

    fun addPayment(studentId: Long, studentName: String, feeId: Long?): String {
        val name = URLEncoder.encode(studentName, "UTF-8")
        return "payment/add/$studentId/$name" + (feeId?.let { "?feeId=$it" } ?: "")
    }

    fun addFee(studentId: Long, studentName: String): String {
        val name = URLEncoder.encode(studentName, "UTF-8")
        return "fee/add/$studentId/$name"
    }
}

@Composable
fun TkCashNavGraph(factory: TkCashViewModelFactory) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {

        composable(Routes.DASHBOARD) {
            val vm: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                viewModel = vm,
                onAddPayment = { navController.navigate(Routes.STUDENTS) },
                onAddExpense = { navController.navigate(Routes.ADD_EXPENSE) },
                onOpenStudents = { navController.navigate(Routes.STUDENTS) },
                onOpenTunggakan = { navController.navigate(Routes.TUNGGAKAN) }
            )
        }

        composable(Routes.STUDENTS) {
            val vm: StudentListViewModel = viewModel(factory = factory)
            StudentListScreen(
                viewModel = vm,
                onOpenStudent = { id -> navController.navigate("students/$id") },
                onAddStudent = { navController.navigate(Routes.ADD_STUDENT) }
            )
        }

        composable(Routes.ADD_STUDENT) {
            val vm: AddStudentViewModel = viewModel(factory = factory)
            AddStudentScreen(
                viewModel = vm,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.STUDENT_DETAIL,
            arguments = listOf(navArgument("studentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getLong("studentId") ?: 0L
            val vm: StudentDetailViewModel = viewModel(factory = factory)
            StudentDetailScreen(
                studentId = studentId,
                viewModel = vm,
                onAddFee = { navController.navigate(Routes.addFee(studentId, vm.uiState.value.student?.name ?: "")) },
                onAddPayment = { feeId ->
                    val name = vm.uiState.value.student?.name ?: ""
                    navController.navigate(Routes.addPayment(studentId, name, feeId))
                },
                // Cetak Kuitansi opens the same payment screen pre-filled on that fee,
                // where the current status (total/dibayar/sisa) is already shown.
                onOpenReceipt = { feeId ->
                    val name = vm.uiState.value.student?.name ?: ""
                    navController.navigate(Routes.addPayment(studentId, name, feeId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADD_FEE,
            arguments = listOf(
                navArgument("studentId") { type = NavType.LongType },
                navArgument("studentName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getLong("studentId") ?: 0L
            val studentName = URLDecoder.decode(backStackEntry.arguments?.getString("studentName") ?: "", "UTF-8")
            val vm: AddFeeViewModel = viewModel(factory = factory)
            AddFeeScreen(
                studentId = studentId,
                studentName = studentName,
                viewModel = vm,
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.ADD_PAYMENT,
            arguments = listOf(
                navArgument("studentId") { type = NavType.LongType },
                navArgument("studentName") { type = NavType.StringType },
                navArgument("feeId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getLong("studentId") ?: 0L
            val studentName = URLDecoder.decode(backStackEntry.arguments?.getString("studentName") ?: "", "UTF-8")
            val feeId = backStackEntry.arguments?.getString("feeId")?.toLongOrNull()
            val vm: AddPaymentViewModel = viewModel(factory = factory)
            AddPaymentScreen(
                studentId = studentId,
                studentName = studentName,
                preselectedFeeId = feeId,
                viewModel = vm,
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_EXPENSE) {
            val vm: AddExpenseViewModel = viewModel(factory = factory)
            AddExpenseScreen(
                viewModel = vm,
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.TUNGGAKAN) {
            val vm: TunggakanViewModel = viewModel(factory = factory)
            TunggakanScreen(
                viewModel = vm,
                onOpenStudent = { id -> navController.navigate("students/$id") }
            )
        }
    }
}
