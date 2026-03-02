package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DestinationRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDestinationRepository
import java.io.File
import java.util.UUID

class DestinationService(private val repo: IDestinationRepository) {

    suspend fun getAllDestinations(call: ApplicationCall) {
        val search   = call.request.queryParameters["search"] ?: ""
        val kategori = call.request.queryParameters["kategori"] ?: ""
        val items    = repo.getDestinations(search, kategori)
        call.respond(DataResponse("success", "Berhasil mengambil daftar destinasi", mapOf("destinations" to items)))
    }

    suspend fun getDestinationById(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: throw AppException(400, "ID destinasi tidak boleh kosong!")
        val item = repo.getDestinationById(id) ?: throw AppException(404, "Data destinasi tidak ditemukan!")
        call.respond(DataResponse("success", "Berhasil mengambil data destinasi", mapOf("destination" to item)))
    }

    private suspend fun getRequest(call: ApplicationCall): DestinationRequest {
        val req = DestinationRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"      -> req.nama      = part.value.trim()
                    "slug"      -> req.slug      = part.value.trim()
                    "kategori"  -> req.kategori  = part.value.trim()
                    "deskripsi" -> req.deskripsi = part.value
                    "lokasi"    -> req.lokasi    = part.value
                    "latitude"  -> req.latitude  = part.value.toDoubleOrNull()
                    "longitude" -> req.longitude = part.value.toDoubleOrNull()
                    "hargaTiket"-> req.hargaTiket= part.value.toLongOrNull() ?: 0L
                    "jamBuka"   -> req.jamBuka   = part.value
                    "kontak"    -> req.kontak    = part.value
                }
                is PartData.FileItem -> {
                    val ext  = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val path = "uploads/destinations/${UUID.randomUUID()}$ext"
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

    private fun validate(req: DestinationRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama",      "Nama destinasi tidak boleh kosong")
        v.required("slug",      "Slug tidak boleh kosong")
        v.required("kategori",  "Kategori tidak boleh kosong")
        v.required("deskripsi", "Deskripsi tidak boleh kosong")
        v.required("lokasi",    "Lokasi tidak boleh kosong")
        v.validate()
    }

    suspend fun createDestination(call: ApplicationCall) {
        val req = getRequest(call)
        validate(req)

        if (repo.getDestinationBySlug(req.slug) != null) {
            req.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Destinasi dengan slug ini sudah terdaftar!")
        }

        val id = repo.addDestination(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan destinasi", mapOf("destinationId" to id)))
    }

    suspend fun updateDestination(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID destinasi tidak boleh kosong!")
        val old = repo.getDestinationById(id) ?: throw AppException(404, "Data destinasi tidak ditemukan!")

        val req = getRequest(call)
        if (req.pathGambar.isNullOrEmpty()) req.pathGambar = old.pathGambar
        if (req.slug.isBlank()) req.slug = old.slug
        validate(req)

        if (req.slug != old.slug && repo.getDestinationBySlug(req.slug) != null) {
            req.pathGambar?.let { if (it != old.pathGambar) File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Destinasi dengan slug ini sudah terdaftar!")
        }

        if (!req.pathGambar.isNullOrEmpty() && req.pathGambar != old.pathGambar) {
            old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        }

        if (!repo.updateDestination(id, req.toEntity())) throw AppException(400, "Gagal memperbarui destinasi!")
        call.respond(DataResponse("success", "Berhasil mengubah data destinasi", null))
    }

    suspend fun deleteDestination(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID destinasi tidak boleh kosong!")
        val old = repo.getDestinationById(id) ?: throw AppException(404, "Data destinasi tidak ditemukan!")
        if (!repo.removeDestination(id)) throw AppException(400, "Gagal menghapus destinasi!")
        old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        call.respond(DataResponse("success", "Berhasil menghapus destinasi", null))
    }

    suspend fun getDestinationImage(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val item = repo.getDestinationById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(item.pathGambar ?: return call.respond(HttpStatusCode.NotFound))
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}