package org.delcom.repositories

import org.delcom.dao.CultureDAO
import org.delcom.entities.Culture
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.CultureTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class CultureRepository : ICultureRepository {

    private fun daoToModel(dao: CultureDAO) = Culture(
        id                 = dao.id.value.toString(),
        nama               = dao.nama,
        jenis              = dao.jenis,
        deskripsi          = dao.deskripsi,
        asalDaerah         = dao.asalDaerah,
        waktuPelaksanaan   = dao.waktuPelaksanaan,
        pathGambar         = dao.pathGambar,
        createdAt          = dao.createdAt,
        updatedAt          = dao.updatedAt,
    )

    override suspend fun getCultures(search: String, jenis: String): List<Culture> = suspendTransaction {
        val query = when {
            search.isNotBlank() && jenis.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                CultureDAO.find {
                    (CultureTable.nama.lowerCase() like keyword) and (CultureTable.jenis eq jenis)
                }
            }
            search.isNotBlank() -> {
                val keyword = "%${search.lowercase()}%"
                CultureDAO.find { CultureTable.nama.lowerCase() like keyword }
            }
            jenis.isNotBlank() -> CultureDAO.find { CultureTable.jenis eq jenis }
            else -> CultureDAO.all()
        }
        query.orderBy(CultureTable.createdAt to SortOrder.DESC).limit(20).map(::daoToModel)
    }

    override suspend fun getCultureById(id: String): Culture? = suspendTransaction {
        CultureDAO.find { CultureTable.id eq UUID.fromString(id) }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun getCultureByName(nama: String): Culture? = suspendTransaction {
        CultureDAO.find { CultureTable.nama eq nama }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun addCulture(culture: Culture): String = suspendTransaction {
        CultureDAO.new {
            nama               = culture.nama
            jenis              = culture.jenis
            deskripsi          = culture.deskripsi
            asalDaerah         = culture.asalDaerah
            waktuPelaksanaan   = culture.waktuPelaksanaan
            pathGambar         = culture.pathGambar
            createdAt          = culture.createdAt
            updatedAt          = culture.updatedAt
        }.id.value.toString()
    }

    override suspend fun updateCulture(id: String, newCulture: Culture): Boolean = suspendTransaction {
        val dao = CultureDAO.find { CultureTable.id eq UUID.fromString(id) }.limit(1).firstOrNull()
            ?: return@suspendTransaction false
        dao.nama               = newCulture.nama
        dao.jenis              = newCulture.jenis
        dao.deskripsi          = newCulture.deskripsi
        dao.asalDaerah         = newCulture.asalDaerah
        dao.waktuPelaksanaan   = newCulture.waktuPelaksanaan
        dao.pathGambar         = newCulture.pathGambar
        dao.updatedAt          = newCulture.updatedAt
        true
    }

    override suspend fun removeCulture(id: String): Boolean = suspendTransaction {
        CultureTable.deleteWhere { CultureTable.id eq UUID.fromString(id) } == 1
    }
}