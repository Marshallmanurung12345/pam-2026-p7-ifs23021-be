package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object AccommodationTable : UUIDTable("accommodations") {
    val nama        = varchar("nama", 150)
    val tipe        = varchar("tipe", 50)
    val deskripsi   = text("deskripsi")
    val alamat      = text("alamat")
    val latitude    = double("latitude").nullable()
    val longitude   = double("longitude").nullable()
    val hargaMulai  = long("harga_mulai").default(0)
    val fasilitas   = text("fasilitas")
    val kontak      = varchar("kontak", 100).nullable()
    val pathGambar  = varchar("path_gambar", 255).nullable()
    val createdAt   = timestamp("created_at")
    val updatedAt   = timestamp("updated_at")
}