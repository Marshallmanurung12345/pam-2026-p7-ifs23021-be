package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DestinationTable : UUIDTable("destinations") {
    val nama        = varchar("nama", 150)
    val slug        = varchar("slug", 150).uniqueIndex()
    val kategori    = varchar("kategori", 50)
    val deskripsi   = text("deskripsi")
    val lokasi      = varchar("lokasi", 255)
    val latitude    = double("latitude").nullable()
    val longitude   = double("longitude").nullable()
    val hargaTiket  = long("harga_tiket").default(0)
    val jamBuka     = varchar("jam_buka", 100).nullable()
    val kontak      = varchar("kontak", 100).nullable()
    val pathGambar  = varchar("path_gambar", 255).nullable()
    val createdAt   = timestamp("created_at")
    val updatedAt   = timestamp("updated_at")
}