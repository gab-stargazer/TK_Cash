package com.lelestacia.tkmanagement.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.FeeWithPayments
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.ui.utils.FileExportUtils
import com.lelestacia.tkmanagement.viewmodel.FullReceiptUiState
import com.lelestacia.tkmanagement.viewmodel.FullReceiptViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FullReceiptScreen(
    viewModel: FullReceiptViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    FullReceiptContent(
        state = state,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullReceiptContent(
    state: FullReceiptUiState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Pembayaran", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (state.history != null) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Opsi Ekspor")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Simpan Gambar (PNG)") },
                                    onClick = {
                                        showMenu = false
                                        coroutineScope.launch {
                                            try {
                                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                                val uri = FileExportUtils.saveBitmapToGallery(
                                                    context,
                                                    bitmap,
                                                    "Laporan_${state.history.student.name.replace(" ", "_")}_${System.currentTimeMillis()}"
                                                )
                                                if (uri != null) {
                                                    Toast.makeText(context, "Laporan disimpan ke Galeri", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Gagal menyimpan laporan", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Simpan Dokumen (PDF)") },
                                    onClick = {
                                        showMenu = false
                                        coroutineScope.launch {
                                            try {
                                                val uri = FileExportUtils.saveFullReceiptAsPdf(
                                                    context,
                                                    state.history,
                                                    "Laporan_${state.history.student.name.replace(" ", "_")}_${System.currentTimeMillis()}"
                                                )
                                                if (uri != null) {
                                                    Toast.makeText(context, "Laporan disimpan ke Unduhan", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            } else if (state.history == null) {
                Text("Data tidak ditemukan", modifier = Modifier.padding(32.dp))
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Capture area
                    Column(
                        modifier = Modifier
                            .drawWithContent {
                                graphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                            .background(Color.White)
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        FullReceiptHeader(state.history)
                        Spacer(modifier = Modifier.height(24.dp))

                        state.history.fees.forEach { feeWithPayments ->
                            FeeSection(feeWithPayments)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        FullReceiptFooter()
                    }
                }
            }
        }
    }
}

@Composable
private fun FullReceiptHeader(history: StudentWithFullHistory) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LAPORAN PEMBAYARAN SISWA",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Text(
            text = "TK AR-RAUDHA",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        ReceiptInfoRow("Nama Murid", history.student.name)
        ReceiptInfoRow("Nama Wali", history.student.guardianName)
        ReceiptInfoRow("NIS", history.student.nis ?: "-")
        val date = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID")).format(Date())
        ReceiptInfoRow("Tgl Cetak", date)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
    }
}

@Composable
private fun FeeSection(item: FeeWithPayments) {
    val totalPaid = item.payments.sumOf { it.amount }
    val remaining = item.fee.totalAmount - totalPaid

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.fee.label, fontWeight = FontWeight.Bold, color = Color.Black)
            if (remaining <= BigDecimal.ZERO) {
                Text(text = "LUNAS", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            } else {
                Text(text = "BELUM LUNAS", fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
            }
        }

        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total Tagihan",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                )
                MoneyText(item.fee.totalAmount, small = true, color = Color.Black)
            }

            if (item.payments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Riwayat Pembayaran:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(0.85F)
                    )
                )
                item.payments.forEach { payment ->
                    val date = SimpleDateFormat(
                        "dd/MM/yy",
                        Locale("in", "ID")
                    ).format(Date(payment.paymentDate))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• $date ${payment.note ?: ""}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        MoneyText(payment.amount, small = true, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            if (item.fee.totalAmount > item.payments.sumOf { it.amount } && item.payments.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5F)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Sisa",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        MoneyText(
                            remaining, small = true, color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(0.8F)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun FullReceiptFooter() {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(128.dp))
        Text(
            "( ____________________ )",
            color = Color.Black,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullReceiptPreview() {
    val mockStudent = Student(
        id = 1,
        name = "Kamil Ahmad",
        guardianName = "Wali Kamil",
        whatsappNumber = "08123456789",
        nis = "123456",
        uniformShirtSize = "M",
        uniformPantsOrSkirtSize = "M"
    )

    val mockFees = listOf(
        FeeWithPayments(
            fee = StudentFee(
                id = 1,
                studentId = 1,
                feeType = FeeType.PEMBANGUNAN,
                label = "Pembangunan",
                totalAmount = BigDecimal("250000")
            ),
            payments = listOf(
                Payment(
                    id = 1,
                    studentId = 1,
                    studentFeeId = 1,
                    amount = BigDecimal("150000"),
                    note = "Cicilan 1"
                )
            )
        ),
        FeeWithPayments(
            fee = StudentFee(
                id = 2,
                studentId = 1,
                feeType = FeeType.BUKU,
                label = "Modul 1 Tahun",
                totalAmount = BigDecimal("150000")
            ),
            payments = listOf(
                Payment(
                    id = 2,
                    studentId = 1,
                    studentFeeId = 2,
                    amount = BigDecimal("150000"),
                    note = "Lunas"
                )
            )
        ),
        FeeWithPayments(
            fee = StudentFee(
                id = 3,
                studentId = 1,
                feeType = FeeType.SPP,
                label = "SPP Juli",
                totalAmount = BigDecimal("85000")
            ),
            payments = emptyList()
        ),
        FeeWithPayments(
            fee = StudentFee(
                id = 4,
                studentId = 1,
                feeType = FeeType.SERAGAM,
                label = "Seragam Biru",
                totalAmount = BigDecimal("190000")
            ),
            payments = emptyList()
        ),
        FeeWithPayments(
            fee = StudentFee(
                id = 5,
                studentId = 1,
                feeType = FeeType.SERAGAM,
                label = "Seragam Olahraga",
                totalAmount = BigDecimal("135000")
            ),
            payments = emptyList()
        ),
        FeeWithPayments(
            fee = StudentFee(
                id = 6,
                studentId = 1,
                feeType = FeeType.SERAGAM,
                label = "Seragam Batik",
                totalAmount = BigDecimal("175000")
            ),
            payments = emptyList()
        )
    )

    val mockHistory = StudentWithFullHistory(
        student = mockStudent,
        fees = mockFees
    )

    TkCashTheme {
        FullReceiptContent(
            state = FullReceiptUiState(
                isLoading = false,
                history = mockHistory
            ),
            onBack = {}
        )
    }
}
