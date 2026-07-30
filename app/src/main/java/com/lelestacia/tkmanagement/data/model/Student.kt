package com.lelestacia.tkmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nis")
    val nis: String? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "guardian_name")
    val guardianName: String,
    @ColumnInfo(name = "whatsapp_number")
    val whatsappNumber: String,
    @ColumnInfo(name = "uniform_shirt_size")
    val uniformShirtSize: String,
    @ColumnInfo(name = "uniform_pants_or_skirt_size")
    val uniformPantsOrSkirtSize: String,
    @ColumnInfo(name = "uniform_status")
    val uniformStatus: UniformStatus = UniformStatus.BELUM_DIAMBIL,
    @ColumnInfo(name = "is_graduated")
    val isGraduated: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)