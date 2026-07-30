package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.dao.StudentDao
import com.lelestacia.tkmanagement.data.model.FeeType
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.StudentWithFees
import com.lelestacia.tkmanagement.data.relation.StudentWithFullHistory
import com.lelestacia.tkmanagement.data.relation.StudentWithPayments
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

sealed class GraduateResult {
    data class Success(val graduatedCount: Int) : GraduateResult()
    data class Skipped(val skippedIds: List<Long>) : GraduateResult()
}

interface StudentRepository {
    fun getActiveStudents(): Flow<List<Student>>
    fun searchStudents(query: String): Flow<List<Student>>
    fun readStudentDataById(id: Long): Flow<Student>
    suspend fun addStudent(student: Student, isAlumniFamily: Boolean): Long
    suspend fun updateStudent(student: Student)
    suspend fun getStudentWithFees(id: Long): StudentWithFees?
    suspend fun getStudentWithPayments(id: Long): StudentWithPayments?
    suspend fun getFullHistory(id: Long): StudentWithFullHistory?
    fun hasOutstandingFees(id: Long): Flow<Boolean>
    suspend fun graduateStudent(id: Long): GraduateResult
    suspend fun graduateStudents(ids: List<Long>): GraduateResult
    suspend fun getNonGraduatedStudentIds(): List<Long>
}

class StudentRepositoryImpl(
    private val studentDao: StudentDao,
    private val feeDao: FeeDao
) : StudentRepository {
    override fun getActiveStudents(): Flow<List<Student>> = studentDao.getAllActive()
    override fun searchStudents(query: String): Flow<List<Student>> = studentDao.search(query)
    override fun readStudentDataById(id: Long): Flow<Student> = studentDao.readById(id)
    override suspend fun getStudentWithFees(id: Long): StudentWithFees? = studentDao.getWithFees(id)
    override suspend fun getStudentWithPayments(id: Long): StudentWithPayments? = studentDao.getWithPayments(id)
    override suspend fun getFullHistory(id: Long): StudentWithFullHistory? = studentDao.getFullHistory(id)

    override fun hasOutstandingFees(id: Long): Flow<Boolean> = feeDao.hasOutstandingFees(id)

    override suspend fun graduateStudent(id: Long): GraduateResult {
        val outstanding = feeDao.getStudentIdsWithOutstandingFees()
        if (id in outstanding) return GraduateResult.Skipped(listOf(id))
        val count = studentDao.graduateStudent(id, System.currentTimeMillis())
        return GraduateResult.Success(count)
    }

    override suspend fun graduateStudents(ids: List<Long>): GraduateResult {
        if (ids.isEmpty()) return GraduateResult.Success(0)
        val outstanding = feeDao.getStudentIdsWithOutstandingFees().toSet()
        val eligible = ids.filterNot { it in outstanding }
        val skipped = ids.filter { it in outstanding }
        if (eligible.isEmpty()) return GraduateResult.Skipped(skipped)
        val count = studentDao.graduateStudents(eligible, System.currentTimeMillis())
        return GraduateResult.Success(count).let { result ->
            if (skipped.isEmpty()) result else GraduateResult.Skipped(skipped)
        }
    }

    override suspend fun getNonGraduatedStudentIds(): List<Long> {
        return studentDao.getNonGraduatedIds()
    }

    override suspend fun addStudent(student: Student, isAlumniFamily: Boolean): Long {
        val studentId = studentDao.insert(student)

        // Pembangunan: 250k, diskon 75k jika alumni -> 175k
        val pembangunanAmount = if (isAlumniFamily) BigDecimal("175000") else BigDecimal("250000")

        val defaultFees = listOf(
            StudentFee(studentId = studentId, feeType = FeeType.PEMBANGUNAN, label = "Pembangunan", totalAmount = pembangunanAmount),
            StudentFee(studentId = studentId, feeType = FeeType.BUKU, label = "Modul 1 Tahun", totalAmount = BigDecimal("150000")),
            StudentFee(studentId = studentId, feeType = FeeType.SPP, label = "SPP Juli", totalAmount = BigDecimal("85000")),
            StudentFee(studentId = studentId, feeType = FeeType.SERAGAM, label = "Seragam Biru", totalAmount = BigDecimal("190000")),
            StudentFee(studentId = studentId, feeType = FeeType.SERAGAM, label = "Seragam Olahraga", totalAmount = BigDecimal("135000")),
            StudentFee(studentId = studentId, feeType = FeeType.SERAGAM, label = "Seragam Batik", totalAmount = BigDecimal("175000"))
        )

        defaultFees.forEach { feeDao.insert(it) }

        return studentId
    }

    override suspend fun updateStudent(student: Student) = studentDao.update(student)
}