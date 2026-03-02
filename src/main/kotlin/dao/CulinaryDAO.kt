package org.delcom.dao

import org.delcom.tables.CulinaryTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class CulinaryDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, CulinaryDAO>(CulinaryTable)

    var nama           by CulinaryTable.nama
    var deskripsi      by CulinaryTable.deskripsi
    var bahanUtama     by CulinaryTable.bahanUtama
    var caraPenyajian  by CulinaryTable.caraPenyajian
    var hargaRataRata  by CulinaryTable.hargaRataRata
    var lokasiTersedia by CulinaryTable.lokasiTersedia
    var pathGambar     by CulinaryTable.pathGambar
    var createdAt      by CulinaryTable.createdAt
    var updatedAt      by CulinaryTable.updatedAt
}