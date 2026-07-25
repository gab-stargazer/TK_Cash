package com.lelestacia.tkmanagement.data

import androidx.room.TypeConverter
import com.lelestacia.tkmanagement.data.model.ExpenseCategory
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.UniformStatus

class Converters {
    @TypeConverter
    fun fromFeeType(value: FeeType): String = value.name
    @TypeConverter
    fun toFeeType(value: String): FeeType = FeeType.valueOf(value)

    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory): String = value.name
    @TypeConverter
    fun toExpenseCategory(value: String): ExpenseCategory = ExpenseCategory.valueOf(value)

    @TypeConverter
    fun fromUniformStatus(value: UniformStatus): String = value.name
    @TypeConverter
    fun toUniformStatus(value: String): UniformStatus = UniformStatus.valueOf(value)
}
