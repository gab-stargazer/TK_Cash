package com.lelestacia.tkmanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nis: String? = null,
    val name: String,
    val guardianName: String,
    val whatsappNumber: String,
    val uniformShirtSize: String? = null,
    val uniformPantsOrSkirtSize: String? = null,
    val uniformShoeSize: String? = null,
    val uniformStatus: UniformStatus = UniformStatus.BELUM_DIAMBIL,
    val isActive: Boolean = true,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long? = null
)
