package org.delcom.dao

import org.delcom.tables.DestinationTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class DestinationDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, DestinationDAO>(DestinationTable)

    var nama        by DestinationTable.nama
    var slug        by DestinationTable.slug
    var kategori    by DestinationTable.kategori
    var deskripsi   by DestinationTable.deskripsi
    var lokasi      by DestinationTable.lokasi
    var latitude    by DestinationTable.latitude
    var longitude   by DestinationTable.longitude
    var hargaTiket  by DestinationTable.hargaTiket
    var jamBuka     by DestinationTable.jamBuka
    var kontak      by DestinationTable.kontak
    var pathGambar  by DestinationTable.pathGambar
    var createdAt   by DestinationTable.createdAt
    var updatedAt   by DestinationTable.updatedAt
}