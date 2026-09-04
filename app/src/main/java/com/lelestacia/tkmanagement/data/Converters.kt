package com.lelestacia.tkmanagement.data

import androidx.room.TypeConverter
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.UniformStatus
import java.math.BigDecimal

/** Konverter tipe Room agar enum dan [BigDecimal] dapat disimpan sebagai teks di database. */
class Converters {
    /** Mengubah [BigDecimal] menjadi String untuk disimpan di Room. */
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toString()

    /** Membaca [BigDecimal] dari String yang disimpan di Room. */
    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    /** Mengubah [FeeType] menjadi nama enum (String). */
    @TypeConverter
    fun fromFeeType(value: FeeType): String = value.name
    /** Membaca [FeeType] dari nama enum (String). */
    @TypeConverter
    fun toFeeType(value: String): FeeType = FeeType.valueOf(value)

    /** Mengubah [ExpenseCategory] menjadi nama enum (String). */
    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name
    /** Membaca [ExpenseCategory] dari nama enum (String). */
    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)

    /** Mengubah [UniformStatus] menjadi nama enum (String). */
    @TypeConverter
    fun fromUniformStatus(value: UniformStatus): String = value.name
    /** Membaca [UniformStatus] dari nama enum (String). */
    @TypeConverter
    fun toUniformStatus(value: String): UniformStatus = UniformStatus.valueOf(value)
}
