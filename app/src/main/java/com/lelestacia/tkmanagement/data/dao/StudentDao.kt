package com.lelestacia.tkmanagement.data.dao

import androidx.room.*
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.StudentWithFees
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.relation.StudentWithPayments
import kotlinx.coroutines.flow.Flow

/** DAO penyimpanan data murid (students). */
@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    /** Menyimpan murid baru dan mengembalikan ID-nya (gagal bila ada konflik). */
    suspend fun insert(student: Student): Long

    @Update
    /** Memperbarui data murid. */
    suspend fun update(student: Student)

    @Delete
    /** Menghapus murid beserta data terkait (cascade). */
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students WHERE is_graduated = 0 ORDER BY name ASC")
    /** Seluruh murid aktif, urut nama. */
    fun getAllActive(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE is_graduated = 1 ORDER BY updated_at DESC")
    /** Seluruh murid lulus, terbaru di atas. */
    fun getAllGraduated(): Flow<List<Student>>

    @Query("SELECT id FROM students WHERE is_graduated = 0")
    /** ID seluruh murid yang belum lulus. */
    suspend fun getNonGraduatedIds(): List<Long>

    @Query("UPDATE students SET is_graduated = 1, updated_at = :updatedAt WHERE id = :studentId AND is_graduated = 0")
    /** Menandai satu murid lulus; mengembalikan jumlah baris yang terubah. */
    suspend fun graduateStudent(studentId: Long, updatedAt: Long): Int

    @Query("UPDATE students SET is_graduated = 1, updated_at = :updatedAt WHERE id IN (:studentIds) AND is_graduated = 0")
    /** Menandai banyak murid lulus sekaligus; mengembalikan jumlah baris yang terubah. */
    suspend fun graduateStudents(studentIds: List<Long>, updatedAt: Long): Int

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' AND is_graduated = :isGraduated ORDER BY name ASC")
    /** Mencari murid berdasarkan nama (sebagian) pada kelompok lulus atau aktif. */
    fun search(query: String, isGraduated: Boolean): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    /** Data satu murid berdasarkan ID, sebagai Flow agar UI ikut ter-update. */
    fun readById(studentId: Long): Flow<Student>

    // Untuk tombol "Lihat Riwayat Lengkap"
    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    /** Satu murid beserta seluruh tagihannya. */
    suspend fun getWithFees(studentId: Long): StudentWithFees?

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    /** Satu murid beserta seluruh pembayarannya. */
    suspend fun getWithPayments(studentId: Long): StudentWithPayments?

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    /** Riwayat lengkap satu murid: tagihan beserta pembayaran tiap tagihan. */
    suspend fun getFullHistory(studentId: Long): StudentWithFullHistory?
}
