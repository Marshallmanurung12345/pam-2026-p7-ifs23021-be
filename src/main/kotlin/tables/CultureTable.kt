package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CultureTable : UUIDTable("cultures") {
    val nama               = varchar("nama", 150)
    val jenis              = varchar("jenis", 50)
    val deskripsi          = text("deskripsi")
    val asalDaerah         = varchar("asal_daerah", 100)
    val waktuPelaksanaan   = varchar("waktu_pelaksanaan", 100).nullable()
    val pathGambar         = varchar("path_gambar", 255).nullable()
    val createdAt          = timestamp("created_at")
    val updatedAt          = timestamp("updated_at")
}