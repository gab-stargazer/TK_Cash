package com.lelestacia.tkmanagement.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.ui.components.LunasChip
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.components.TunggakanChip
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.StudentDetailUiEvent
import com.lelestacia.tkmanagement.viewmodel.StudentDetailUiState
import com.lelestacia.tkmanagement.viewmodel.StudentDetailViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun StudentDetailScreen(
    viewModel: StudentDetailViewModel,
    onAddFee: () -> Unit,
    onAddPayment: (feeId: Long?) -> Unit,
    onOpenReceipt: (feeId: Long) -> Unit,
    onOpenFullReceipt: (studentId: Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    StudentDetailContent(
        state = state,
        onEvent = viewModel::onEvent,
        onAddFee = onAddFee,
        onAddPayment = onAddPayment,
        onOpenReceipt = onOpenReceipt,
        onOpenFullReceipt = onOpenFullReceipt,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentDetailContent(
    state: StudentDetailUiState,
    onEvent: (StudentDetailUiEvent) -> Unit,
    onAddFee: () -> Unit,
    onAddPayment: (feeId: Long?) -> Unit,
    onOpenReceipt: (feeId: Long) -> Unit,
    onOpenFullReceipt: (studentId: Long) -> Unit,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.student?.name ?: "Detail Murid",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    state.student?.let { s ->
                        IconButton(onClick = { onOpenFullReceipt(s.id) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = "Laporan Lengkap"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) {
                ExtendedFloatingActionButton(
                    onClick = onAddFee,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Tambah Tagihan") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header: Student Profile
            state.student?.let { s ->
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            "Data Wali dan Informasi Seragam:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            "Wali: ${s.guardianName}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            "WhatsApp: ${s.whatsappNumber}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        s.nis?.let { nis ->
                            Text(
                                "NIS: ${s.nis}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            "Seragam: ${s.uniformShirtSize} / ${s.uniformPantsOrSkirtSize}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1F)) {
                                Text(
                                    text = if (s.isGraduated) "Status: Sudah Lulus" else "Status: Masih Aktif",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                if (!s.isGraduated && state.hasOutstandingFees) {
                                    Text(
                                        "Selesaikan semua tagihan sebelum meluluskan murid.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                state.graduationMessage?.let { msg ->
                                    Text(
                                        msg,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (!s.isGraduated) {
                                val canGraduate = !state.hasOutstandingFees
                                Button(
                                    onClick = { onEvent(StudentDetailUiEvent.Graduate) },
                                    enabled = canGraduate,
                                    modifier = Modifier.padding(start = 12.dp)
                                ) {
                                    Text("Luluskan")
                                }
                            }
                        }
                    }
                }
            }

            // TabRow
            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Tagihan") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Pembayaran") }
                )
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> FeeListPage(state, onAddFee, onAddPayment, onOpenReceipt)
                    1 -> PaymentHistoryPage(state)
                }
            }
        }
    }
}

@Composable
private fun FeeListPage(
    state: StudentDetailUiState,
    onAddFee: () -> Unit,
    onAddPayment: (feeId: Long?) -> Unit,
    onOpenReceipt: (feeId: Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 120.dp,
            start = 12.dp,
            end = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.fees.isEmpty()) {
            item {
                Column {
                    Text(
                        "Belum ada tagihan untuk murid ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = onAddFee,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Tambah Tagihan Pertama")
                    }
                }
            }
        } else {
            items(state.fees, key = { it.studentFeeId }) { feeProgress ->
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = feeProgress.label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            if (feeProgress.remaining <= BigDecimal.ZERO) LunasChip() else TunggakanChip()
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column {
                                Text(
                                    "Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                MoneyText(feeProgress.totalAmount, small = true)
                            }
                            Column {
                                Text(
                                    "Sudah dibayar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                MoneyText(feeProgress.paidAmount, small = true)
                            }
                            Column {
                                Text(
                                    "Sisa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                MoneyText(feeProgress.remaining, small = true)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Button(
                                onClick = { onAddPayment(feeProgress.studentFeeId) },
                                modifier = Modifier.weight(1f),
                                enabled = feeProgress.remaining > BigDecimal.ZERO
                            ) {
                                Text(
                                    if (feeProgress.remaining <= BigDecimal.ZERO) "Lunas" else "Bayar Cicilan",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OutlinedButton(
                                onClick = { onOpenReceipt(feeProgress.studentFeeId) },
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cetak Kuitansi")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentHistoryPage(state: StudentDetailUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.payments.isEmpty()) {
            item {
                Text(
                    "Belum ada riwayat pembayaran.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(state.payments, key = { it.id }) { payment ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            payment.note ?: "Pembayaran",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    MoneyText(payment.amount, small = true)
                }
                HorizontalDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentDetailPreview() {
    TkCashTheme {
        StudentDetailContent(
            state = StudentDetailUiState(
                isLoading = false,
                student = Student(
                    id = 1,
                    name = "Kamil",
                    guardianName = "Wali Kamil",
                    whatsappNumber = "08123456789",
                    uniformShirtSize = "M",
                    uniformPantsOrSkirtSize = "M"
                ),
                fees = listOf(
                    FeeProgress(
                        1,
                        1,
                        FeeType.SPP,
                        "SPP Juli 2026",
                        BigDecimal("350000"),
                        BigDecimal("100000"),
                        BigDecimal("250000")
                    )
                ),
                payments = listOf(
                    Payment(
                        id = 1,
                        studentId = 1,
                        studentFeeId = 1,
                        amount = BigDecimal("100000"),
                        note = "Bayar SPP"
                    )
                )
            ),
            onEvent = {},
            onAddFee = {},
            onAddPayment = {},
            onOpenReceipt = {},
            onOpenFullReceipt = {},
            onBack = {}
        )
    }
}
