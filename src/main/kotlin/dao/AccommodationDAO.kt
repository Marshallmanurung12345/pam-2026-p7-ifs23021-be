package org.delcom.dao

import org.delcom.tables.AccommodationTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class AccommodationDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, AccommodationDAO>(AccommodationTable)

    var nama        by AccommodationTable.nama
    var tipe        by AccommodationTable.tipe
    var deskripsi   by AccommodationTable.deskripsi
    var alamat      by AccommodationTable.alamat
    var latitude    by AccommodationTable.latitude
    var longitude   by AccommodationTable.longitude
    var hargaMulai  by AccommodationTable.hargaMulai
    var fasilitas   by AccommodationTable.fasilitas
    var kontak      by AccommodationTable.kontak
    var pathGambar  by AccommodationTable.pathGambar
    var createdAt   by AccommodationTable.createdAt
    var updatedAt   by AccommodationTable.updatedAt
}