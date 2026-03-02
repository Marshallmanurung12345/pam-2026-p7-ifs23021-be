package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Destination

@Serializable
data class DestinationRequest(
    var nama: String = "",
    var slug: String = "",
    var kategori: String = "",
    var deskripsi: String = "",
    var lokasi: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var hargaTiket: Long = 0,
    var jamBuka: String? = null,
    var kontak: String? = null,
    var pathGambar: String? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama"       to nama,
        "slug"       to slug,
        "kategori"   to kategori,
        "deskripsi"  to deskripsi,
        "lokasi"     to lokasi,
        "hargaTiket" to hargaTiket.toString(),
    )

    fun toEntity(): Destination = Destination(
        nama        = nama,
        slug        = slug,
        kategori    = kategori,
        deskripsi   = deskripsi,
        lokasi      = lokasi,
        latitude    = latitude,
        longitude   = longitude,
        hargaTiket  = hargaTiket,
        jamBuka     = jamBuka,
        kontak      = kontak,
        pathGambar  = pathGambar,
    )
}