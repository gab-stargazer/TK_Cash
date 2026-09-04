package com.lelestacia.tkmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Data murid yang tersimpan di tabel `students`. */
@OptIn(ExperimentalTime::class)
@Entity(tableName = "students")
data class Student(
    /** ID unik, dibuat otomatis. */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** Nomor Induk Siswa, opsional. */
    @ColumnInfo(name = "nis")
    val nis: String? = null,
    /** Nama murid. */
    @ColumnInfo(name = "name")
    val name: String,
    /** Nama orang tua/wali. */
    @ColumnInfo(name = "guardian_name")
    val guardianName: String,
    /** Nomor WhatsApp wali. */
    @ColumnInfo(name = "whatsapp_number")
    val whatsappNumber: String,
    /** Ukuran seragam atasan. */
    @ColumnInfo(name = "uniform_shirt_size")
    val uniformShirtSize: String,
    /** Ukuran seragam bawahan (celana/rok). */
    @ColumnInfo(name = "uniform_pants_or_skirt_size")
    val uniformPantsOrSkirtSize: String,
    /** Ukuran sepatu, opsional. */
    @ColumnInfo(name = "uniform_shoe_size")
    val uniformShoeSize: String? = null,
    /** Status pengambilan seragam. */
    @ColumnInfo(name = "uniform_status")
    val uniformStatus: UniformStatus = UniformStatus.BELUM_DIAMBIL,
    /** Penanda murid sudah lulus/pindah. */
    @ColumnInfo(name = "is_graduated")
    val isGraduated: Boolean = false,
    /** Waktu pembuatan data (epoch ms). */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    /** Waktu pembaruan terakhir, null bila belum pernah diubah. */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null
)