package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FeeDao {
    @Insert
    suspend fun insert(fee: StudentFee): Long

    @Query("SELECT * FROM student_fees WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>>

    /**
     * Progres tagihan untuk satu murid, live: Room melacak tabel student_fees
     * dan payments, jadi setiap insert Payment baru otomatis memicu emisi
     * ulang Flow ini — layar detail murid ter-update tanpa refresh manual.
     */
    @Query(
        """
        SELECT
            sf.id AS studentFeeId,
            sf.studentId AS studentId,
            sf.feeType AS feeType,
            sf.label AS label,
            sf.totalAmount AS totalAmount,
            COALESCE(SUM(p.amount), 0) AS paidAmount,
            sf.totalAmount - COALESCE(SUM(p.amount), 0) AS remaining
        FROM student_fees sf
        LEFT JOIN payments p ON p.studentFeeId = sf.id
        WHERE sf.studentId = :studentId
        GROUP BY sf.id
        ORDER BY sf.createdAt DESC
        """
    )
    fun getFeeProgressForStudent(studentId: Long): Flow<List<FeeProgress>>

    /**
     * Daftar tunggakan: setiap tagihan dikurangi total yang sudah dibayar,
     * hanya yang masih ada sisa, diurutkan dari sisa terbesar.
     */
    @Query(
        """
        SELECT
            sf.id AS studentFeeId,
            sf.studentId AS studentId,
            s.name AS studentName,
            sf.label AS feeLabel,
            sf.totalAmount AS totalAmount,
            COALESCE(SUM(p.amount), 0) AS paidAmount,
            sf.totalAmount - COALESCE(SUM(p.amount), 0) AS remaining
        FROM student_fees sf
        INNER JOIN students s ON s.id = sf.studentId
        LEFT JOIN payments p ON p.studentFeeId = sf.id
        GROUP BY sf.id
        HAVING remaining > 0
        ORDER BY remaining DESC
        """
    )
    fun getTunggakanList(): Flow<List<TunggakanItem>>

    /** Sisa tagihan untuk satu StudentFee spesifik, dipakai saat cetak kuitansi. */
    @Query(
        """
        SELECT
            sf.id AS studentFeeId,
            sf.studentId AS studentId,
            s.name AS studentName,
            sf.label AS feeLabel,
            sf.totalAmount AS totalAmount,
            COALESCE(SUM(p.amount), 0) AS paidAmount,
            sf.totalAmount - COALESCE(SUM(p.amount), 0) AS remaining
        FROM student_fees sf
        INNER JOIN students s ON s.id = sf.studentId
        LEFT JOIN payments p ON p.studentFeeId = sf.id
        WHERE sf.id = :studentFeeId
        GROUP BY sf.id
        """
    )
    suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem?
}
