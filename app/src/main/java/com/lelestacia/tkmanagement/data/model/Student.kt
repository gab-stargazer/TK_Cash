package com.lelestacia.tkmanagement.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val nis: String? = null,               // Nomor Induk Siswa
    val name: String,                       // Nama murid
    val guardianName: String,               // Nama wali
    val whatsappNumber: String,              // Nomor WhatsApp wali

    // Ukuran & status seragam
    val uniformShirtSize: String? = null,
    val uniformPantsOrSkirtSize: String? = null,
    val uniformShoeSize: String? = null,
    val uniformStatus: UniformStatus = UniformStatus.BELUM_DIAMBIL,

    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
