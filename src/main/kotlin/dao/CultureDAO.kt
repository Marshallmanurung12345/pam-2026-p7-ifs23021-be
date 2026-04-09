package org.delcom.dao

import org.delcom.tables.CultureTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CultureDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, CultureDAO>(CultureTable)

    var nama               by CultureTable.nama
    var jenis              by CultureTable.jenis
    var deskripsi          by CultureTable.deskripsi
    var asalDaerah         by CultureTable.asalDaerah
    var waktuPelaksanaan   by CultureTable.waktuPelaksanaan
    var pathGambar         by CultureTable.pathGambar
    var createdAt          by CultureTable.createdAt
    var updatedAt          by CultureTable.updatedAt
}






