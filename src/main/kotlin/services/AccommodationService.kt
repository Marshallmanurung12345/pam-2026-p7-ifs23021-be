package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AccommodationRequest
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IAccommodationRepository
import java.io.File
import java.util.UUID

class AccommodationService(private val repo: IAccommodationRepository) {

    suspend fun getAllAccommodations(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val tipe   = call.request.queryParameters["tipe"] ?: ""
        val items  = repo.getAccommodations(search, tipe)
        call.respond(DataResponse("success", "Berhasil mengambil daftar penginapan", mapOf("accommodations" to items)))
    }

    suspend fun getAccommodationById(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: throw AppException(400, "ID penginapan tidak boleh kosong!")
        val item = repo.getAccommodationById(id) ?: throw AppException(404, "Data penginapan tidak ditemukan!")
        call.respond(DataResponse("success", "Berhasil mengambil data penginapan", mapOf("accommodation" to item)))
    }

    private suspend fun getRequest(call: ApplicationCall): AccommodationRequest {
        val req = AccommodationRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"       -> req.nama       = part.value.trim()
                    "tipe"       -> req.tipe       = part.value.trim()
                    "deskripsi"  -> req.deskripsi  = part.value
                    "alamat"     -> req.alamat     = part.value
                    "latitude"   -> req.latitude   = part.value.toDoubleOrNull()
                    "longitude"  -> req.longitude  = part.value.toDoubleOrNull()
                    "hargaMulai" -> req.hargaMulai = part.value.toLongOrNull() ?: 0L
                    "fasilitas"  -> req.fasilitas  = part.value
                    "kontak"     -> req.kontak     = part.value
                }
                is PartData.FileItem -> {
                    val ext  = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val path = "uploads/accommodations/${UUID.randomUUID()}$ext"
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

    private fun validate(req: AccommodationRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama",      "Nama penginapan tidak boleh kosong")
        v.required("tipe",      "Tipe penginapan tidak boleh kosong")
        v.required("deskripsi", "Deskripsi tidak boleh kosong")
        v.required("alamat",    "Alamat tidak boleh kosong")
        v.required("fasilitas", "Fasilitas tidak boleh kosong")
        v.validate()
    }

    suspend fun createAccommodation(call: ApplicationCall) {
        val req = getRequest(call)
        validate(req)
        if (repo.getAccommodationByName(req.nama) != null) {
            req.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Penginapan dengan nama ini sudah terdaftar!")
        }
        val id = repo.addAccommodation(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan penginapan", mapOf("accommodationId" to id)))
    }

    suspend fun updateAccommodation(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID penginapan tidak boleh kosong!")
        val old = repo.getAccommodationById(id) ?: throw AppException(404, "Data penginapan tidak ditemukan!")

        val req = getRequest(call)
        if (req.pathGambar.isNullOrEmpty()) req.pathGambar = old.pathGambar
        validate(req)

        if (req.nama != old.nama && repo.getAccommodationByName(req.nama) != null) {
            req.pathGambar?.let { if (it != old.pathGambar) File(it).takeIf { f -> f.exists() }?.delete() }
            throw AppException(409, "Penginapan dengan nama ini sudah terdaftar!")
        }

        if (!req.pathGambar.isNullOrEmpty() && req.pathGambar != old.pathGambar) {
            old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        }

        if (!repo.updateAccommodation(id, req.toEntity())) throw AppException(400, "Gagal memperbarui penginapan!")
        call.respond(DataResponse("success", "Berhasil mengubah data penginapan", null))
    }

    suspend fun deleteAccommodation(call: ApplicationCall) {
        val id  = call.parameters["id"] ?: throw AppException(400, "ID penginapan tidak boleh kosong!")
        val old = repo.getAccommodationById(id) ?: throw AppException(404, "Data penginapan tidak ditemukan!")
        if (!repo.removeAccommodation(id)) throw AppException(400, "Gagal menghapus penginapan!")
        old.pathGambar?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        call.respond(DataResponse("success", "Berhasil menghapus penginapan", null))
    }

    suspend fun getAccommodationImage(call: ApplicationCall) {
        val id   = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val item = repo.getAccommodationById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(item.pathGambar ?: return call.respond(HttpStatusCode.NotFound))
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}