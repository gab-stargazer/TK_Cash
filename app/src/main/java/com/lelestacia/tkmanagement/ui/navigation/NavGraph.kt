package com.lelestacia.tkmanagement.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier

/** Tujuan navigasi aplikasi. */
sealed interface Destination {
    @Serializable object Dashboard : Destination
    @Serializable object StudentList : Destination
    @Serializable object AddStudent : Destination
    @Serializable data class StudentDetail(val studentId: Long) : Destination
    @Serializable data class AddFee(val studentId: Long, val studentName: String) : Destination
    @Serializable data class AddPayment(val studentId: Long, val studentName: String, val preselectedFeeId: Long? = null) : Destination
    @Serializable object AddExpense : Destination
    @Serializable object AddBulkFee : Destination
    @Serializable object Tunggakan : Destination
    @Serializable data class Receipt(val studentFeeId: Long) : Destination
    @Serializable data class FullReceipt(val studentId: Long) : Destination
}

/** Stack navigasi sederhana untuk NavDisplay. */
class Navigator(startDestination: Destination) {
    val backStack: SnapshotStateList<Any> = mutableStateListOf(startDestination)

    fun goTo(destination: Any) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun popBackStack() {
        goBack()
    }
}

@Composable
/** Host navigasi utama aplikasi TKManagement. */
fun TkCashNavGraph(
    navigator: Navigator,
    entryProvider: (Any) -> NavEntry<Any>
) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        modifier = Modifier
    )
}
