package org.delcom.repositories

import org.delcom.dao.AccommodationDAO
import org.delcom.entities.Accommodation
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.AccommodationTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class AccommodationRepository : IAccommodationRepository {

    private fun daoToModel(dao: AccommodationDAO) = Accommodation(
        id          = dao.id.value.toString(),
        nama        = dao.nama,
        tipe        = dao.tipe,
        deskripsi   = dao.deskripsi,
        alamat      = dao.alamat,
        latitude    = dao.latitude,
        longitude   = dao.longitude,
        hargaMulai  = dao.hargaMulai,
        fasilitas   = dao.fasilitas,
        kontak      = dao.kontak,
        pathGambar  = dao.pathGambar,
        createdAt   = dao.createdAt,
        updatedAt   = dao.updatedAt,
    )

    override suspend fun getAccommodations(search: String, tipe: String): List<Accommodation> = suspendTransaction {
        val query = when {
            search.isNotBlank() && tipe.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                AccommodationDAO.find {
                    (AccommodationTable.nama.lowerCase() like keyword) and (AccommodationTable.tipe eq tipe)
                }
            }
            search.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                AccommodationDAO.find { AccommodationTable.nama.lowerCase() like keyword }
            }
            tipe.isNotBlank() -> AccommodationDAO.find { AccommodationTable.tipe eq tipe }
            else -> AccommodationDAO.all()
        }
        query.orderBy(AccommodationTable.createdAt to SortOrder.DESC).limit(20).map(::daoToModel)
    }

    override suspend fun getAccommodationById(id: String): Accommodation? = suspendTransaction {
        AccommodationDAO.find { AccommodationTable.id eq UUID.fromString(id) }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun getAccommodationByName(nama: String): Accommodation? = suspendTransaction {
        AccommodationDAO.find { AccommodationTable.nama eq nama }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun addAccommodation(accommodation: Accommodation): String = suspendTransaction {
        AccommodationDAO.new {
            nama        = accommodation.nama
            tipe        = accommodation.tipe
            deskripsi   = accommodation.deskripsi
            alamat      = accommodation.alamat
            latitude    = accommodation.latitude
            longitude   = accommodation.longitude
            hargaMulai  = accommodation.hargaMulai
            fasilitas   = accommodation.fasilitas
            kontak      = accommodation.kontak
            pathGambar  = accommodation.pathGambar
            createdAt   = accommodation.createdAt
            updatedAt   = accommodation.updatedAt
        }.id.value.toString()
    }

    override suspend fun updateAccommodation(id: String, newAccommodation: Accommodation): Boolean = suspendTransaction {
        val dao = AccommodationDAO.find { AccommodationTable.id eq UUID.fromString(id) }.limit(1).firstOrNull()
            ?: return@suspendTransaction false
        dao.nama        = newAccommodation.nama
        dao.tipe        = newAccommodation.tipe
        dao.deskripsi   = newAccommodation.deskripsi
        dao.alamat      = newAccommodation.alamat
        dao.latitude    = newAccommodation.latitude
        dao.longitude   = newAccommodation.longitude
        dao.hargaMulai  = newAccommodation.hargaMulai
        dao.fasilitas   = newAccommodation.fasilitas
        dao.kontak      = newAccommodation.kontak
        dao.pathGambar  = newAccommodation.pathGambar
        dao.updatedAt   = newAccommodation.updatedAt
        true
    }

    override suspend fun removeAccommodation(id: String): Boolean = suspendTransaction {
        AccommodationTable.deleteWhere { AccommodationTable.id eq UUID.fromString(id) } == 1
    }
}