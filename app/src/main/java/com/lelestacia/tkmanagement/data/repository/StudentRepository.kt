package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.StudentDao
import com.lelestacia.tkmanagement.data.model.Student
import com.lelestacia.tkmanagement.data.relation.StudentWithFees
import com.lelestacia.tkmanagement.data.relation.StudentWithPayments
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getActiveStudents(): Flow<List<Student>>
    fun searchStudents(query: String): Flow<List<Student>>
    fun readStudentDataById(id: Long): Flow<Student>
    suspend fun addStudent(student: Student): Long
    suspend fun updateStudent(student: Student)
    suspend fun getStudentWithFees(id: Long): StudentWithFees?
    suspend fun getStudentWithPayments(id: Long): StudentWithPayments?
}

class StudentRepositoryImpl(
    private val studentDao: StudentDao
) : StudentRepository {
    override fun getActiveStudents(): Flow<List<Student>> = studentDao.getAllActive()
    override fun searchStudents(query: String): Flow<List<Student>> = studentDao.search(query)
    override fun readStudentDataById(id: Long): Flow<Student> = studentDao.readById(id)
    override suspend fun getStudentWithFees(id: Long): StudentWithFees? = studentDao.getWithFees(id)
    override suspend fun getStudentWithPayments(id: Long): StudentWithPayments? = studentDao.getWithPayments(id)
    override suspend fun addStudent(student: Student): Long = studentDao.insert(student)
    override suspend fun updateStudent(student: Student) = studentDao.update(student)
}
