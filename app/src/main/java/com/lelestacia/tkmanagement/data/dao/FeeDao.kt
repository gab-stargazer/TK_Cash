package com.lelestacia.tkmanagement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lelestacia.tkmanagement.data.relation.FeeProgress
import com.lelestacia.tkmanagement.data.model.StudentFee
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import kotlinx.coroutines.flow.Flow

/** DAO penyimpanan tagihan murid dan perhitungan sisa pembayaran. */
@Dao
interface FeeDao {
    /** Menyimpan satu tagihan murid dan mengembalikan ID barunya. */
    @Insert
    suspend fun insert(fee: StudentFee): Long

    @Insert
    suspend fun insertBulk(fees: List<StudentFee>)

    @Query("SELECT * FROM student_fees WHERE student_id = :studentId ORDER BY created_at DESC")
    fun getFeesForStudent(studentId: Long): Flow<List<StudentFee>>

    /**
     * True kalau murid masih punya tagihan yang sisa > 0.
     * Dipakai untuk mengunci aksi "Luluskan" sampai semua tagihan lunas.
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM student_fees sf
            LEFT JOIN payments p ON p.student_fee_id = sf.id
            WHERE sf.student_id = :studentId
            GROUP BY sf.id
            HAVING sf.total_amount - COALESCE(SUM(p.amount), 0) > 0
        )
        """
    )
    fun hasOutstandingFees(studentId: Long): Flow<Boolean>

    /**
     * Daftar student_id yang masih punya tunggakan. Dipakai untuk memfilter
     * kelulusan massal: hanya murid yang SEMUA tagihannya sudah lunas yang boleh diluluskan.
     */
    @Query(
        """
        SELECT sf.student_id FROM student_fees sf
        LEFT JOIN payments p ON p.student_fee_id = sf.id
        GROUP BY sf.id
        HAVING sf.total_amount - COALESCE(SUM(p.amount), 0) > 0
        """
    )
    suspend fun getStudentIdsWithOutstandingFees(): List<Long>

    /**
     * Progres tagihan untuk satu murid, live: Room melacak tabel student_fees
     * dan payments, jadi setiap insert Payment baru otomatis memicu emisi
     * ulang Flow ini — layar detail murid ter-update tanpa refresh manual.
     */
    @Query(
        """
        SELECT
            sf.id AS studentFeeId,
            sf.student_id AS studentId,
            sf.fee_type AS feeType,
            sf.label AS label,
            CAST(sf.total_amount AS TEXT) AS totalAmount,
            CAST(COALESCE(SUM(p.amount), '0') AS TEXT) AS paidAmount,
            CAST(sf.total_amount - COALESCE(SUM(p.amount), 0) AS TEXT) AS remaining
        FROM student_fees sf
        LEFT JOIN payments p ON p.student_fee_id = sf.id
        WHERE sf.student_id = :studentId
        GROUP BY sf.id
        ORDER BY sf.created_at DESC
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
            sf.student_id AS studentId,
            s.guardian_name AS guardianName,
            s.name AS studentName,
            sf.label AS feeLabel,
            CAST(sf.total_amount AS TEXT) AS totalAmount,
            CAST(COALESCE(SUM(p.amount), '0') AS TEXT) AS paidAmount,
            CAST(sf.total_amount - COALESCE(SUM(p.amount), 0) AS TEXT) AS remaining
        FROM student_fees sf
        INNER JOIN students s ON s.id = sf.student_id
        LEFT JOIN payments p ON p.student_fee_id = sf.id
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
            sf.student_id AS studentId,
            s.name AS studentName,
            s.guardian_name AS guardianName,
            sf.label AS feeLabel,
            CAST(sf.total_amount AS TEXT) AS totalAmount,
            CAST(COALESCE(SUM(p.amount), '0') AS TEXT) AS paidAmount,
            CAST(sf.total_amount - COALESCE(SUM(p.amount), 0) AS TEXT) AS remaining
        FROM student_fees sf
        INNER JOIN students s ON s.id = sf.student_id
        LEFT JOIN payments p ON p.student_fee_id = sf.id
        WHERE sf.id = :studentFeeId
        GROUP BY sf.id
        """
    )
    suspend fun getFeeStatus(studentFeeId: Long): TunggakanItem?
}
