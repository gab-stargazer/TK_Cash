package com.lelestacia.tkmanagement.ui.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.LineSeparator
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.lelestacia.tkmanagement.data.model.Payment
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.FeeWithPayments
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileExportUtils {

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/TKManagement")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        try {
            val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
        } catch (e: Exception) {
            resolver.delete(imageUri, null, null)
            return null
        }

        return imageUri
    }

    fun saveFullReceiptAsPdf(
        context: Context, data: StudentWithFullHistory, fileName: String
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/TKManagement"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val pdfUri = resolver.insert(collection, contentValues) ?: return null

        try {
            resolver.openOutputStream(pdfUri)?.use { outputStream ->
                StudentFeeReportGenerator.generate(outputStream, data, "TK AR-RAUDHA")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(pdfUri, contentValues, null, null)
            }
        } catch (e: Exception) {
            resolver.delete(pdfUri, null, null)
            return null
        }

        return pdfUri
    }

    fun saveSingleReceiptAsPdf(
        context: Context, feeStatus: TunggakanItem, payments: List<Payment>, fileName: String
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/TKManagement"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val pdfUri = resolver.insert(collection, contentValues) ?: return null

        try {
            resolver.openOutputStream(pdfUri)?.use { outputStream ->
                ReceiptReportGenerator.generate(outputStream, feeStatus, payments, "TK AR-RAUDHA")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(pdfUri, contentValues, null, null)
            }
        } catch (e: Exception) {
            resolver.delete(pdfUri, null, null)
            return null
        }

        return pdfUri
    }
}

object StudentFeeReportGenerator {

    private val RED = DeviceRgb(0xB2, 0x22, 0x22)
    private val GREEN = DeviceRgb(0x22, 0x8B, 0x22)
    private val HEADER_BG = DeviceRgb(0xF0, 0xF0, 0xF0)
    private val GRAY_TEXT = DeviceRgb(0x88, 0x88, 0x88)
    private val LIGHT_LINE = DeviceRgb(0xCC, 0xCC, 0xCC)

    fun generate(
        outputStream: OutputStream, data: StudentWithFullHistory, schoolName: String
    ) {
        val pdfDoc = PdfDocument(PdfWriter(outputStream))
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(40f, 40f, 40f, 40f)

        val fontRegular =
            PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA)
        val fontBold =
            PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD)

        addHeader(document, fontBold, fontRegular, schoolName)
        addStudentInfo(document, fontRegular, fontBold, data.student)
        addDivider(document, 1.5f, 20f)

        data.fees.forEach { feeWithPayments ->
            addFeeBlock(document, fontRegular, fontBold, feeWithPayments)
        }

        addSignature(document, fontRegular)

        document.close()
    }

    private fun addHeader(document: Document, bold: PdfFont, regular: PdfFont, schoolName: String) {
        document.add(
            Paragraph("LAPORAN PEMBAYARAN SISWA").setFont(bold).setFontSize(22f)
                .setMultipliedLeading(1.15f).setTextAlignment(TextAlignment.CENTER)
        )
        document.add(
            Paragraph(schoolName.uppercase()).setFont(regular).setFontSize(13f)
                .setFontColor(GRAY_TEXT).setTextAlignment(TextAlignment.CENTER).setMarginTop(4f)
                .setMarginBottom(18f)
        )
    }

    private fun addStudentInfo(
        document: Document, regular: PdfFont, bold: PdfFont, student: Student
    ) {
        val tglCetak = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID")).format(Date())
        document.add(labelValueLine("Nama Murid: ", student.name, regular, bold))
        document.add(labelValueLine("Nama Wali: ", student.guardianName ?: "-", regular, bold))
        document.add(labelValueLine("NIS: ", student.nis ?: "-", regular, bold))
        document.add(labelValueLine("Tgl Cetak: ", tglCetak, regular, bold))
    }

    private fun labelValueLine(
        label: String, value: String, regular: PdfFont, bold: PdfFont
    ): Paragraph {
        return Paragraph().add(Text(label).setFont(regular).setFontSize(12f))
            .add(Text(value).setFont(bold).setFontSize(12f)).setMarginBottom(3f)
    }

    private fun addDivider(document: Document, thickness: Float, marginBottom: Float) {
        document.add(
            LineSeparator(SolidLine(thickness)).setMarginTop(4f).setMarginBottom(marginBottom)
        )
    }

    private fun addFeeBlock(
        document: Document, regular: PdfFont, bold: PdfFont, fp: FeeWithPayments
    ) {
        val fee = fp.fee
        val totalPaid = fp.payments.fold(BigDecimal.ZERO) { acc, p -> acc + p.amount }
        val remaining = (fee.totalAmount - totalPaid).coerceAtLeast(BigDecimal.ZERO)
        val isLunas = remaining <= BigDecimal.ZERO

        val headerTable =
            Table(UnitValue.createPercentArray(floatArrayOf(55f, 45f))).useAllAvailableWidth()
                .setMarginTop(14f)

        headerTable.addCell(
            Cell().add(Paragraph(fee.label).setFont(bold).setFontSize(15f))
                .setBackgroundColor(HEADER_BG).setBorder(Border.NO_BORDER).setPadding(10f)
        )
        headerTable.addCell(
            Cell().add(
                Paragraph(if (isLunas) "LUNAS" else "BELUM LUNAS").setFont(bold).setFontSize(15f)
                    .setFontColor(if (isLunas) GREEN else RED).setTextAlignment(TextAlignment.RIGHT)
            ).setBackgroundColor(HEADER_BG).setBorder(Border.NO_BORDER).setPadding(10f)
        )
        document.add(headerTable)

        document.add(
            twoColRow(
                "Total Tagihan", formatRupiah(fee.totalAmount), bold, regular, boldLabel = true
            )
        )

        if (fp.payments.isNotEmpty()) {
            document.add(
                Paragraph("Riwayat Pembayaran:").setFont(regular).setFontSize(12f).setMarginTop(6f)
                    .setMarginBottom(2f)
            )
            val dateFmt = SimpleDateFormat("dd/MM/yy", Locale("in", "ID"))
            fp.payments.sortedBy { it.paymentDate }.forEach { payment ->
                val dateStr = "  • " + dateFmt.format(Date(payment.paymentDate))
                document.add(
                    twoColRow(
                        dateStr, formatRupiah(payment.amount), regular, regular, boldLabel = false
                    )
                )
            }

            if (!isLunas) {
                document.add(
                    LineSeparator(SolidLine(0.75f))
                        .setStrokeColor(LIGHT_LINE)
                        .setMarginTop(4f)
                        .setMarginBottom(4f)
                )

                document.add(
                    twoColRow(
                        "Sisa",
                        formatRupiah(remaining),
                        bold,
                        bold,
                        boldLabel = true
                    )
                )
            }
        }
    }

    private fun twoColRow(
        label: String, value: String, labelFont: PdfFont, valueFont: PdfFont, boldLabel: Boolean
    ): Table {
        val table =
            Table(UnitValue.createPercentArray(floatArrayOf(55f, 45f))).useAllAvailableWidth()
                .setMarginTop(2f)

        table.addCell(
            Cell().add(Paragraph(label).setFont(labelFont).setFontSize(12f))
                .setBorder(Border.NO_BORDER).setPaddingLeft(0f).setPaddingTop(3f)
                .setPaddingBottom(3f)
        )
        table.addCell(
            Cell().add(
                Paragraph(value).setFont(valueFont).setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
            ).setBorder(Border.NO_BORDER).setPaddingRight(0f).setPaddingTop(3f).setPaddingBottom(3f)
        )
        return table
    }

    private fun addSignature(document: Document, regular: PdfFont) {
        document.add(
            Paragraph("( " + "_".repeat(28) + " )").setFont(regular).setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(50f)
        )
    }

    private fun formatRupiah(amount: BigDecimal): String {
        val symbols =
            DecimalFormatSymbols(Locale.forLanguageTag("id-ID")).apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp" + formatter.format(amount.setScale(0, RoundingMode.HALF_UP))
    }
}

object ReceiptReportGenerator {

    private val GREEN = DeviceRgb(0x22, 0x8B, 0x22)

    fun generate(
        outputStream: OutputStream,
        feeStatus: TunggakanItem,
        payments: List<Payment>,
        schoolName: String
    ) {
        val pdfDoc = PdfDocument(PdfWriter(outputStream))
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(40f, 40f, 40f, 40f)

        val fontRegular =
            PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA)
        val fontBold =
            PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD)

        // Header
        document.add(
            Paragraph("KWITANSI PEMBAYARAN").setFont(fontBold).setFontSize(22f)
                .setTextAlignment(TextAlignment.CENTER)
        )
        document.add(
            Paragraph(schoolName.uppercase()).setFont(fontRegular).setFontSize(13f)
                .setFontColor(DeviceRgb(0x88, 0x88, 0x88)).setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(24f)
        )

        // Body
        val date = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(Date())
        document.add(row("Tanggal", date, fontRegular, fontBold))
        document.add(row("Nama Murid", feeStatus.studentName, fontRegular, fontBold))
        document.add(row("Nama Wali", feeStatus.guardianName, fontRegular, fontBold))
        document.add(row("Keterangan", feeStatus.feeLabel, fontRegular, fontBold))

        document.add(LineSeparator(SolidLine(1f)).setMarginTop(12f).setMarginBottom(12f))

        document.add(moneyRow("Total Tagihan", feeStatus.totalAmount, fontBold, fontRegular))
        document.add(moneyRow("Telah Dibayar", feeStatus.paidAmount, fontBold, fontRegular))

        if (feeStatus.remaining <= BigDecimal.ZERO) {
            document.add(
                Paragraph("LUNAS").setFont(fontBold).setFontSize(24f).setFontColor(GREEN)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(24f)
            )
        } else {
            document.add(moneyRow("Sisa Tagihan", feeStatus.remaining, fontBold, fontBold))
        }

        // Signature
        document.add(
            Paragraph("Bendahara,").setFont(fontRegular).setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(60f)
        )
        document.add(
            Paragraph("( " + "_".repeat(28) + " )").setFont(fontRegular).setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(40f)
        )

        document.close()
    }

    private fun row(label: String, value: String, reg: PdfFont, bold: PdfFont): Paragraph {
        return Paragraph().add(Text("$label : ").setFont(reg).setFontSize(12f))
            .add(Text(value).setFont(bold).setFontSize(12f)).setMarginBottom(4f)
    }

    private fun moneyRow(
        label: String, amount: BigDecimal, labelFont: PdfFont, valFont: PdfFont
    ): Table {
        val table =
            Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
        table.addCell(Cell().add(Paragraph(label).setFont(labelFont)).setBorder(Border.NO_BORDER))
        table.addCell(
            Cell().add(
                Paragraph(formatRupiah(amount)).setFont(valFont)
                    .setTextAlignment(TextAlignment.RIGHT)
            ).setBorder(Border.NO_BORDER)
        )
        return table
    }

    private fun formatRupiah(amount: BigDecimal): String {
        val symbols =
            DecimalFormatSymbols(Locale.forLanguageTag("id-ID")).apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp" + formatter.format(amount.setScale(0, RoundingMode.HALF_UP))
    }
}
