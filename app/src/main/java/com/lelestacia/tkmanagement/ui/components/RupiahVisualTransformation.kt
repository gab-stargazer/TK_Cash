package com.lelestacia.tkmanagement.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

/** VisualTransformation yang menampilkan input digit sebagai format Rupiah (mis. "Rp 1.500.000"). */
class RupiahVisualTransformation : VisualTransformation {
    /** Memformat teks digit menjadi "Rp <angka>" sambil menjaga offset kursor tetap mengarah ke digit yang tepat. */
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // We assume originalText contains only digits.
        val number = originalText.toLongOrNull() ?: 0L
        val formatted = NumberFormat.getNumberInstance(Locale("in", "ID")).format(number)
        
        // If the original text has leading zeros, the NumberFormat will strip them.
        // To keep the mapping consistent, we'll use the formatted string as the base.
        // However, usually money doesn't have leading zeros except for "0" itself.
        
        val out = "Rp $formatted"

        val offsetMapping = object : OffsetMapping {
            /** Memetakan offset teks asli (digit) ke offset teks terformat. */
            override fun originalToTransformed(offset: Int): Int {
                // Number of digits in original text before this offset
                val digitsBefore = originalText.take(offset).length

                // Find the position in 'formatted' that contains exactly 'digitsBefore' digits
                var digitsFound = 0
                var transformedOffset = 0
                for (char in formatted) {
                    if (char.isDigit()) {
                        digitsFound++
                    }
                    transformedOffset++
                    if (digitsFound == digitsBefore) break
                }

                // If originalText has leading zeros that were stripped by format(),
                // they all map to the first digit of formatted.
                // But since we filter for digits and toLongOrNull(), let's simplify.

                return transformedOffset + 3 // +3 for "Rp "
            }

            /** Memetakan offset teks terformat kembali ke offset digit asli. */
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return 0
                val transformedOffset = (offset - 3).coerceAtMost(formatted.length)
                val substring = formatted.take(transformedOffset)
                return substring.count { it.isDigit() }
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
