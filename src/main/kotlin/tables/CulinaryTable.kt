package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CulinaryTable : UUIDTable("culinaries") {
    val nama           = varchar("nama", 150)
    val deskripsi      = text("deskripsi")
    val bahanUtama     = text("bahan_utama")
    val caraPenyajian  = text("cara_penyajian")
    val hargaRataRata  = long("harga_rata_rata").default(0)
    val lokasiTersedia = text("lokasi_tersedia")
    val pathGambar     = varchar("path_gambar", 255).nullable()
    val createdAt      = timestamp("created_at")
    val updatedAt      = timestamp("updated_at")
}