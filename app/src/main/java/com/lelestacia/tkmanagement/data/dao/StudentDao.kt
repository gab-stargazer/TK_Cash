package com.lelestacia.tkmanagement.data.dao

import androidx.room.*
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.StudentWithFees
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.relation.StudentWithPayments
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students WHERE is_graduated = 0 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE is_graduated = 1 ORDER BY updated_at DESC")
    fun getAllGraduated(): Flow<List<Student>>

    @Query("SELECT id FROM students WHERE is_graduated = 0")
    suspend fun getNonGraduatedIds(): List<Long>

    @Query("UPDATE students SET is_graduated = 1, updated_at = :updatedAt WHERE id = :studentId AND is_graduated = 0")
    suspend fun graduateStudent(studentId: Long, updatedAt: Long): Int

    @Query("UPDATE students SET is_graduated = 1, updated_at = :updatedAt WHERE id IN (:studentIds) AND is_graduated = 0")
    suspend fun graduateStudents(studentIds: List<Long>, updatedAt: Long): Int

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' AND is_graduated = :isGraduated ORDER BY name ASC")
    fun search(query: String, isGraduated: Boolean): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun readById(studentId: Long): Flow<Student>

    // Untuk tombol "Lihat Riwayat Lengkap"
    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getWithFees(studentId: Long): StudentWithFees?

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getWithPayments(studentId: Long): StudentWithPayments?

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getFullHistory(studentId: Long): StudentWithFullHistory?
}
