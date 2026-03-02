package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.CultureRequest
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICultureRepository
import java.io.File
import java.util.UUID

class CultureService(private val repo: ICultureRepository) {

    suspend fun getAllCultures(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val jenis  = call.request.queryParameters["jenis"] ?: ""
        val items  = repo.getCultures(search, jenis)
        call.respond(DataResponse("success", "Berhasil mengambil daftar budaya", mapOf("cultures" to items)))
    }

    suspend fun getCultureById(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: throw AppException(400, "ID budaya tidak boleh kosong!")
        val item = repo.getCultureById(id) ?: throw AppException(404, "Data budaya tidak ditemukan!")
        call.respond(DataResponse("success", "Berhasil mengambil data budaya", mapOf("culture" to item)))
    }

    private suspend fun getRequest(call: ApplicationCall): CultureRequest {
        val req = CultureRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"               -> req.nama               = part.value.trim()
                    "jenis"              -> req.jenis              = part.value.trim()
                    "deskripsi"          -> req.deskripsi          = part.value
                    "asalDaerah"         -> req.asalDaerah         = part.value
                    "waktuPelaksanaan"   -> req.waktuPelaksanaan   = part.value
                }
                is PartData.FileItem -> {
                    val ext  = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val path = "uploads/cultures/${UUID.randomUUID()}$ext"
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

    private fun validate(req: CultureRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama",       "Nama budaya tidak boleh kosong")
        v.required("jenis",      "Jenis budaya tidak boleh kosong")
        v.required("deskripsi",  "Deskripsi tidak boleh kosong")
        v.required("asalDaerah", "Asal daerah tidak boleh kosong")
        v.validate()
    }

    suspend fun createCulture(call: ApplicationCall) {
        val req = getRequest(call)
        validate(req)
        if (repo.getCultureByName(req.nama) != null) {
            req.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Budaya dengan nama ini sudah terdaftar!")
        }
        val id = repo.addCulture(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan budaya", mapOf("cultureId" to id)))
    }

    suspend fun updateCulture(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID budaya tidak boleh kosong!")
        val old = repo.getCultureById(id) ?: throw AppException(404, "Data budaya tidak ditemukan!")

        val req = getRequest(call)
        if (req.pathGambar.isNullOrEmpty()) req.pathGambar = old.pathGambar
        validate(req)

        if (req.nama != old.nama && repo.getCultureByName(req.nama) != null) {
            req.pathGambar?.let { if (it != old.pathGambar) File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Budaya dengan nama ini sudah terdaftar!")
        }

        if (!req.pathGambar.isNullOrEmpty() && req.pathGambar != old.pathGambar) {
            old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        }

        if (!repo.updateCulture(id, req.toEntity())) throw AppException(400, "Gagal memperbarui budaya!")
        call.respond(DataResponse("success", "Berhasil mengubah data budaya", null))
    }

    suspend fun deleteCulture(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID budaya tidak boleh kosong!")
        val old = repo.getCultureById(id) ?: throw AppException(404, "Data budaya tidak ditemukan!")
        if (!repo.removeCulture(id)) throw AppException(400, "Gagal menghapus budaya!")
        old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        call.respond(DataResponse("success", "Berhasil menghapus budaya", null))
    }

    suspend fun getCultureImage(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val item = repo.getCultureById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(item.pathGambar ?: return call.respond(HttpStatusCode.NotFound))
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}