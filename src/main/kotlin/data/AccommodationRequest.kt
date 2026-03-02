package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Accommodation

@Serializable
data class AccommodationRequest(
    var nama: String = "",
    var tipe: String = "",
    var deskripsi: String = "",
    var alamat: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var hargaMulai: Long = 0,
    var fasilitas: String = "",
    var kontak: String? = null,
    var pathGambar: String? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama"       to nama,
        "tipe"       to tipe,
        "deskripsi"  to deskripsi,
        "alamat"     to alamat,
        "hargaMulai" to hargaMulai.toString(),
        "fasilitas"  to fasilitas,
    )

    fun toEntity(): Accommodation = Accommodation(
        nama        = nama,
        tipe        = tipe,
        deskripsi   = deskripsi,
        alamat      = alamat,
        latitude    = latitude,
        longitude   = longitude,
        hargaMulai  = hargaMulai,
        fasilitas   = fasilitas,
        kontak      = kontak,
        pathGambar  = pathGambar,
    )
}