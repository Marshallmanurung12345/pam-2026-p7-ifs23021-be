package org.delcom.repositories

import org.delcom.dao.CulinaryDAO
import org.delcom.entities.Culinary
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.CulinaryTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class CulinaryRepository : ICulinaryRepository {

    private fun daoToModel(dao: CulinaryDAO) = Culinary(
        id             = dao.id.value.toString(),
        nama           = dao.nama,
        deskripsi      = dao.deskripsi,
        bahanUtama     = dao.bahanUtama,
        caraPenyajian  = dao.caraPenyajian,
        hargaRataRata  = dao.hargaRataRata,
        lokasiTersedia = dao.lokasiTersedia,
        pathGambar     = dao.pathGambar,
        createdAt      = dao.createdAt,
        updatedAt      = dao.updatedAt,
    )

    override suspend fun getCulinaries(search: String): List<Culinary> = suspendTransaction {
        if (search.isBlank()) {
            CulinaryDAO.all().orderBy(CulinaryTable.createdAt to SortOrder.DESC).limit(20).map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            CulinaryDAO.find { CulinaryTable.nama.lowerCase() like keyword }
                .orderBy(CulinaryTable.nama to SortOrder.ASC).limit(20).map(::daoToModel)
        }
    }

    override suspend fun getCulinaryById(id: String): Culinary? = suspendTransaction {
        CulinaryDAO.find { CulinaryTable.id eq UUID.fromString(id) }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun getCulinaryByName(nama: String): Culinary? = suspendTransaction {
        CulinaryDAO.find { CulinaryTable.nama eq nama }.limit(1).map(::daoToModel).firstOrNull()
    }

    override suspend fun addCulinary(culinary: Culinary): String = suspendTransaction {
        CulinaryDAO.new {
            nama           = culinary.nama
            deskripsi      = culinary.deskripsi
            bahanUtama     = culinary.bahanUtama
            caraPenyajian  = culinary.caraPenyajian
            hargaRataRata  = culinary.hargaRataRata
            lokasiTersedia = culinary.lokasiTersedia
            pathGambar     = culinary.pathGambar
            createdAt      = culinary.createdAt
            updatedAt      = culinary.updatedAt
        }.id.value.toString()
    }

    override suspend fun updateCulinary(id: String, newCulinary: Culinary): Boolean = suspendTransaction {
        val dao = CulinaryDAO.find { CulinaryTable.id eq UUID.fromString(id) }.limit(1).firstOrNull()
            ?: return@suspendTransaction false
        dao.nama           = newCulinary.nama
        dao.deskripsi      = newCulinary.deskripsi
        dao.bahanUtama     = newCulinary.bahanUtama
        dao.caraPenyajian  = newCulinary.caraPenyajian
        dao.hargaRataRata  = newCulinary.hargaRataRata
        dao.lokasiTersedia = newCulinary.lokasiTersedia
        dao.pathGambar     = newCulinary.pathGambar
        dao.updatedAt      = newCulinary.updatedAt
        true
    }

    override suspend fun removeCulinary(id: String): Boolean = suspendTransaction {
        CulinaryTable.deleteWhere { CulinaryTable.id eq UUID.fromString(id) } == 1
    }
}