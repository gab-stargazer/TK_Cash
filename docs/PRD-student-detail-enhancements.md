# PRD — Penyempurnaan Halaman Murid (Ukuran Sepatu, Status Seragam, Riwayat Lengkap)

**Project:** TKManagement (com.lelestacia.tkmanagement)
**Status:** Draft v1 · **Date:** 2026-09-01
**Source requirement:** "Saran tambahan untuk halaman murid TK" (ChatGPT spec follow-up)

## Problem & Audience

Bendahara TK melayani orang tua langsung di halaman detail murid. ChatGPT's second spec asks that one page show *semua* informasi penting murid: NIS, ukuran seragam lengkap (baju, celana/rok, **sepatu**), status penerimaan seragam, status pembayaran per jenis biaya, riwayat transaksi, dan kuitansi — tanpa berpindah layar.

Current source truth (verified 2026-09-01):
- `Student.nis`, `uniformShirtSize`, `uniformPantsOrSkirtSize` — fully wired (model → form → display) ✅
- `UniformStatus` enum + DB column exist (**dead code** — zero references in `ui/`; user cannot see or set "belum/sudah diambil") ❌
- Ukuran sepatu — **does not exist** anywhere in the codebase ❌
- Per-type payment status (Lunas/Tunggakan chips + `FeeProgress`) — implemented ✅
- Full per-student transaction history — exists as pager tab 2, but there is no explicit **"Lihat Riwayat Lengkap"** button; receipts open in a separate screen (per-fee "Cetak Kuitansi" buttons exist) ⚠️

## Desired User Outcome

Dari halaman detail satu murid, bendahara dapat langsung melihat dan mengubah **ukuran sepatu**, melihat/menandai **status penerimaan seragam**, dan membuka **seluruh riwayat transaksi** dengan satu tombol eksplisit — tanpa keluar halaman.

## V1 Scope (5 items)

1. **`uniformShoeSize` field** — add to `Student` model + `AddStudentScreen` form (with the existing two size fields) + "Seragam" line on `StudentDetailScreen` (renders `baju / celana-rok / sepatu`).
2. **Uniform status picker** — in the add/edit-student form: "Status Penerimaan Seragam" selector (Belum Diambil / Sudah Diambil) bound to `UniformStatus`.
3. **Status display on detail page** — "Seragam: M / M / 33 · Belum Diambil" on `StudentDetailScreen`; distinct visual for Belum vs Sudah.
4. **"Lihat Riwayat Lengkap" button** — explicit CTA on the student detail page (opens the existing history tab / full transaction list in one view).
5. **Room migration safekeeping** — nullable column added without destructive migration; verify `exportSchema`/migration path before applying (assumption: `uniformShoeSize TEXT` nullable; fallback only if schema export confirms).

## Primary Journey

```mermaid
flowchart LR
    A[Buka Halaman Murid] --> B[NIS + Wali + WhatsApp visible]
    B --> C[Ukuran Seragam baju / celana-rok / sepatu]
    C --> D[Status Seragam: Belum/Sudah Diambil]
    D --> E[Tombol Lihat Riwayat Lengkap]
    E --> F[Semua transaksi + kuitansi dalam satu view]
    D -. edit .-> G[Form murid: ubah ukuran / status]
    G -.> D
```

## Acceptance Criteria (observable)

1. **CR1:** Adding a student lets you enter shoe size; detail page shows all three sizes as `baju / celana-rok / sepatu`.
2. **CR2:** The form lets you pick Belum Diambil / Sudah Diambil; picking it persists (app restart keeps value) and the detail page shows the status text.
3. **CR3:** "Lihat Riwayat Lengkap" button is visible on the student detail page and opens the full transaction list without navigation errors; receipts for each fee remain reachable from that view.
4. **CR4:** Existing students (DB rows written before this change) load with `uniformShoeSize = null` and default `BELUM_DIAMBIL` — **no crash, no data loss, no destructive migration**.
5. **CR5:** No regression: NIS, payment chips, tabs, graduation status behave exactly as before.

## Non-Goals

- No uniform inventory/stock management (quantities per size).
- No shoe-size validation rules (free text, consistent with existing sizes).
- No per-student uniform photos.
- No changes to payment/receipt logic (out of scope for this PRD).

## Constraints & Assumptions

- Kotlin/Compose, Room 2.7.2 (KSP), Koin, Nav3 — existing architecture unchanged.
- Free-text size values (consistent with `uniformShirtSize`).
- UI copy in Indonesian ("Ukuran Sepatu", "Status Penerimaan Seragam", "Belum Diambil", "Sudah Diambil", "Lihat Riwayat Lengkap").
- Single-device local DB; no sync.

## Success Signal

Bendahara menyelesaikan layanan orang tua (cek status, seragam, dan riwayat) dari satu halaman murid — tanpa berpindah layar untuk data seragam — dan status seragam selalu terkini karena dapat diubah langsung dari form.