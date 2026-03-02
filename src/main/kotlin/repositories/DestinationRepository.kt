package org.delcom.repositories

import org.delcom.dao.DestinationDAO
import org.delcom.entities.Destination
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DestinationTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class DestinationRepository : IDestinationRepository {

    private fun daoToModel(dao: DestinationDAO) = Destination(
        id          = dao.id.value.toString(),
        nama        = dao.nama,
        slug        = dao.slug,
        kategori    = dao.kategori,
        deskripsi   = dao.deskripsi,
        lokasi      = dao.lokasi,
        latitude    = dao.latitude,
        longitude   = dao.longitude,
        hargaTiket  = dao.hargaTiket,
        jamBuka     = dao.jamBuka,
        kontak      = dao.kontak,
        pathGambar  = dao.pathGambar,
        createdAt   = dao.createdAt,
        updatedAt   = dao.updatedAt,
    )

    override suspend fun getDestinations(search: String, kategori: String): List<Destination> = suspendTransaction {
        val query = when {
            search.isNotBlank() && kategori.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                DestinationDAO.find {
                    (DestinationTable.nama.lowerCase() like keyword) and
                            (DestinationTable.kategori eq kategori)
                }
            }
            search.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                DestinationDAO.find { DestinationTable.nama.lowerCase() like keyword }
            }
            kategori.isNotBlank() -> {
                DestinationDAO.find { DestinationTable.kategori eq kategori }
            }
            else -> DestinationDAO.all()
        }
        query.orderBy(DestinationTable.createdAt to SortOrder.DESC).limit(20).map(::daoToModel)
    }

    override suspend fun getDestinationById(id: String): Destination? = suspendTransaction {
        DestinationDAO.find { DestinationTable.id eq UUID.fromString(id) }
            .limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun getDestinationBySlug(slug: String): Destination? = suspendTransaction {
        DestinationDAO.find { DestinationTable.slug eq slug }
            .limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun addDestination(destination: Destination): String = suspendTransaction {
        DestinationDAO.new {
            nama        = destination.nama
            slug        = destination.slug
            kategori    = destination.kategori
            deskripsi   = destination.deskripsi
            lokasi      = destination.lokasi
            latitude    = destination.latitude
            longitude   = destination.longitude
            hargaTiket  = destination.hargaTiket
            jamBuka     = destination.jamBuka
            kontak      = destination.kontak
            pathGambar  = destination.pathGambar
            createdAt   = destination.createdAt
            updatedAt   = destination.updatedAt
        }.id.value.toString()
    }

    override suspend fun updateDestination(id: String, newDestination: Destination): Boolean = suspendTransaction {
        val dao = DestinationDAO.find { DestinationTable.id eq UUID.fromString(id) }
            .limit(1).firstOrNull() ?: return@suspendTransaction false

        dao.nama        = newDestination.nama
        dao.slug        = newDestination.slug
        dao.kategori    = newDestination.kategori
        dao.deskripsi   = newDestination.deskripsi
        dao.lokasi      = newDestination.lokasi
        dao.latitude    = newDestination.latitude
        dao.longitude   = newDestination.longitude
        dao.hargaTiket  = newDestination.hargaTiket
        dao.jamBuka     = newDestination.jamBuka
        dao.kontak      = newDestination.kontak
        dao.pathGambar  = newDestination.pathGambar
        dao.updatedAt   = newDestination.updatedAt
        true
    }

    override suspend fun removeDestination(id: String): Boolean = suspendTransaction {
        DestinationTable.deleteWhere { DestinationTable.id eq UUID.fromString(id) } == 1
    }
}