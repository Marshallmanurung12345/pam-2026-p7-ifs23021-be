package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.CulinaryRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICulinaryRepository
import java.io.File
import java.util.UUID

class CulinaryService(private val repo: ICulinaryRepository) {

    suspend fun getAllCulinaries(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val items  = repo.getCulinaries(search)
        call.respond(DataResponse("success", "Berhasil mengambil daftar kuliner", mapOf("culinaries" to items)))
    }

    suspend fun getCulinaryById(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: throw AppException(400, "ID kuliner tidak boleh kosong!")
        val item = repo.getCulinaryById(id) ?: throw AppException(404, "Data kuliner tidak ditemukan!")
        call.respond(DataResponse("success", "Berhasil mengambil data kuliner", mapOf("culinary" to item)))
    }

    private suspend fun getRequest(call: ApplicationCall): CulinaryRequest {
        val req = CulinaryRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"          -> req.nama          = part.value.trim()
                    "deskripsi"     -> req.deskripsi     = part.value
                    "bahanUtama"    -> req.bahanUtama    = part.value
                    "caraPenyajian" -> req.caraPenyajian = part.value
                    "hargaRataRata" -> req.hargaRataRata = part.value.toLongOrNull() ?: 0L
                    "lokasiTersedia"-> req.lokasiTersedia= part.value
                }
                is PartData.FileItem -> {
                    val ext  = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val path = "uploads/culinaries/${UUID.randomUUID()}$ext"
                    val file = File(path).also { it.parentFile.mkdirs() }
                    part.provider().copyAndClose(file.writeChannel())
                    req.pathGambar = path
                }
                else -> {}
            }
            part.dispose()
        }
        return req
    }

    private fun validate(req: CulinaryRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama",           "Nama kuliner tidak boleh kosong")
        v.required("deskripsi",      "Deskripsi tidak boleh kosong")
        v.required("bahanUtama",     "Bahan utama tidak boleh kosong")
        v.required("caraPenyajian",  "Cara penyajian tidak boleh kosong")
        v.required("lokasiTersedia", "Lokasi tersedia tidak boleh kosong")
        v.validate()
    }

    suspend fun createCulinary(call: ApplicationCall) {
        val req = getRequest(call)
        validate(req)
        if (repo.getCulinaryByName(req.nama) != null) {
            req.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Kuliner dengan nama ini sudah terdaftar!")
        }
        val id = repo.addCulinary(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan kuliner", mapOf("culinaryId" to id)))
    }

    suspend fun updateCulinary(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID kuliner tidak boleh kosong!")
        val old = repo.getCulinaryById(id) ?: throw AppException(404, "Data kuliner tidak ditemukan!")

        val req = getRequest(call)
        if (req.pathGambar.isNullOrEmpty()) req.pathGambar = old.pathGambar
        validate(req)

        if (req.nama != old.nama && repo.getCulinaryByName(req.nama) != null) {
            req.pathGambar?.let { if (it != old.pathGambar) File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Kuliner dengan nama ini sudah terdaftar!")
        }

        if (!req.pathGambar.isNullOrEmpty() && req.pathGambar != old.pathGambar) {
            old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        }

        if (!repo.updateCulinary(id, req.toEntity())) throw AppException(400, "Gagal memperbarui kuliner!")
        call.respond(DataResponse("success", "Berhasil mengubah data kuliner", null))
    }

    suspend fun deleteCulinary(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID kuliner tidak boleh kosong!")
        val old = repo.getCulinaryById(id) ?: throw AppException(404, "Data kuliner tidak ditemukan!")
        if (!repo.removeCulinary(id)) throw AppException(400, "Gagal menghapus kuliner!")
        old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        call.respond(DataResponse("success", "Berhasil menghapus kuliner", null))
    }

    suspend fun getCulinaryImage(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val item = repo.getCulinaryById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(item.pathGambar ?: return call.respond(HttpStatusCode.NotFound))
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}