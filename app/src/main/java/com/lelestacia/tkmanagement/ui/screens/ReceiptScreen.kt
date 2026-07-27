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
import androidx.compose.ui.unit.sp
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.ui.components.MoneyText
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.ui.utils.FileExportUtils
import com.lelestacia.tkmanagement.viewmodel.ReceiptUiState
import com.lelestacia.tkmanagement.viewmodel.ReceiptViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptScreen(
    viewModel: ReceiptViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ReceiptContent(
        state = state,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReceiptContent(
    state: ReceiptUiState,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kuitansi Digital", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (state.feeStatus != null) {
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
                                                    "Kwitansi_${state.feeStatus.studentName.replace(" ", "_")}_${System.currentTimeMillis()}"
                                                )
                                                if (uri != null) {
                                                    Toast.makeText(context, "Kuitansi disimpan ke Galeri", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Gagal menyimpan kuitansi", Toast.LENGTH_SHORT).show()
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
                                                val uri = FileExportUtils.saveSingleReceiptAsPdf(
                                                    context,
                                                    state.feeStatus,
                                                    state.payments,
                                                    "Kwitansi_${state.feeStatus.studentName.replace(" ", "_")}_${System.currentTimeMillis()}"
                                                )
                                                if (uri != null) {
                                                    Toast.makeText(context, "Kuitansi disimpan ke Unduhan", Toast.LENGTH_SHORT).show()
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
            } else if (state.feeStatus == null) {
                Text("Data tidak ditemukan", modifier = Modifier.padding(32.dp))
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // This is the part we will capture
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
                        ReceiptHeader()
                        Spacer(modifier = Modifier.height(24.dp))
                        ReceiptBody(state.feeStatus)
                        Spacer(modifier = Modifier.height(24.dp))
                        ReceiptFooter()
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "KWITANSI PEMBAYARAN",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "TK AR-RAUDHA",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
    }
}

@Composable
private fun ReceiptBody(item: TunggakanItem) {
    val date = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(Date())

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReceiptRow("Tanggal", date)
        ReceiptRow("Nama Murid", item.studentName)
        ReceiptRow("Nama Wali", item.guardianName)
        ReceiptRow("Keterangan", item.feeLabel)

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Total Tagihan",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )

            )
            MoneyText(item.totalAmount, color = MaterialTheme.colorScheme.onSurface)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Telah Dibayar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            MoneyText(item.paidAmount, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (item.remaining <= BigDecimal.ZERO) {
            Text(
                text = "LUNAS",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = Color(0xFF1B5E20)
            )
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$label : ",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface.copy(0.8F)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun ReceiptFooter() {
    Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(128.dp))
        Text("( ____________________ )", color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceiptPreview() {
    TkCashTheme {
        ReceiptContent(
            state = ReceiptUiState(
                isLoading = false,
                feeStatus = TunggakanItem(
                    studentFeeId = 1,
                    studentId = 1,
                    studentName = "Kamil",
                    guardianName = "Malik",
                    feeLabel = "SPP Agustus 2026",
                    totalAmount = BigDecimal("350000"),
                    paidAmount = BigDecimal("150000"),
                    remaining = BigDecimal("200000")
                )
            ),
            onBack = {}
        )
    }
}
