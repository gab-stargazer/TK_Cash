package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import kotlinx.coroutines.flow.Flow

interface FeeRepository {
    fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>>
    fun readFeeProgressForStudentById(studentId: Long): Flow<List<FeeProgress>>
    suspend fun addFee(fee: StudentFee): Long
    fun getTunggakanList(): Flow<List<TunggakanItem>>
    suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem?
}

class FeeRepositoryImpl(
    private val feeDao: FeeDao
) : FeeRepository {
    override fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>> = feeDao.getFeesForStudent(studentId)
    override fun readFeeProgressForStudentById(studentId: Long): Flow<List<FeeProgress>> =
        feeDao.getFeeProgressForStudent(studentId)
    override suspend fun addFee(fee: StudentFee): Long = feeDao.insert(fee)
    override fun getTunggakanList(): Flow<List<TunggakanItem>> = feeDao.getTunggakanList()
    override suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem? = feeDao.getFeeStatus(studentFeeId)
}
