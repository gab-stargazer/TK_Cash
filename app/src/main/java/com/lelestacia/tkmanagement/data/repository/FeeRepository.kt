package com.lelestacia.tkmanagement.data.repository

import com.lelestacia.tkmanagement.data.dao.FeeDao
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import kotlinx.coroutines.flow.Flow

/** Sumber data tagihan murid (student_fees); abstraksi di atas [FeeDao]. */
interface FeeRepository {
    /** Seluruh tagihan milik satu murid. */
    fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>>
    /** Progres (total/terbayar/sisa) seluruh tagihan satu murid. */
    fun readFeeProgressForStudentById(studentId: Long): Flow<List<FeeProgress>>
    /** Menyimpan satu tagihan baru dan mengembalikan ID-nya. */
    suspend fun addFee(fee: StudentFee): Long
    /** Membuat satu tagihan untuk banyak murid sekaligus (template disalin per murid). */
    suspend fun addBulkFee(feeTemplate: StudentFee, studentIds: List<Long>)
    /** Daftar tunggakan seluruh murid (hanya baris dengan sisa > 0). */
    fun getTunggakanList(): Flow<List<TunggakanItem>>
    /** Status tunggakan satu tagihan, null bila tagihan tidak ditemukan. */
    suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem?
}

/** Implementasi [FeeRepository] yang meneruskan panggilan ke [FeeDao]. */
class FeeRepositoryImpl(
    private val feeDao: FeeDao
) : FeeRepository {
    /** Meneruskan ke [FeeDao.getFeesForStudent]. */
    override fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>> = feeDao.getFeesForStudent(studentId)
    /** Meneruskan ke [FeeDao.getFeeProgressForStudent]. */
    override fun readFeeProgressForStudentById(studentId: Long): Flow<List<FeeProgress>> =
        feeDao.getFeeProgressForStudent(studentId)
    /** Meneruskan ke [FeeDao.insert]. */
    override suspend fun addFee(fee: StudentFee): Long = feeDao.insert(fee)
    
    /** Menyalin [feeTemplate] untuk setiap id murid lalu menyisipkan sekaligus ke [FeeDao]. */
    override suspend fun addBulkFee(feeTemplate: StudentFee, studentIds: List<Long>) {
        val fees = studentIds.map { id ->
            feeTemplate.copy(id = 0, studentId = id)
        }
        feeDao.insertBulk(fees)
    }

    /** Meneruskan ke [FeeDao.getTunggakanList]. */
        override fun getTunggakanList(): Flow<List<TunggakanItem>> = feeDao.getTunggakanList()
        /** Meneruskan ke [FeeDao.getFeeStatus]. */
        override suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem? = feeDao.getFeeStatus(studentFeeId)
}
