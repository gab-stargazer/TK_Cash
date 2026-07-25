package com.lelestacia.tkmanagement.data.model

/**
 * Jenis tagihan / pemasukan.
 * PENDAFTARAN, SPP, SERAGAM, BUKU, KEGIATAN bisa dicicil (lihat StudentFee + Payment).
 */
enum class FeeType {
    PENDAFTARAN,
    SPP,
    SERAGAM,
    BUKU,
    KEGIATAN,
    LAINNYA
}
