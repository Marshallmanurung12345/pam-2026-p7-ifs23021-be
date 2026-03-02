package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Culture

@Serializable
data class CultureRequest(
    var nama: String = "",
    var jenis: String = "",
    var deskripsi: String = "",
    var asalDaerah: String = "",
    var waktuPelaksanaan: String? = null,
    var pathGambar: String? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama"       to nama,
        "jenis"      to jenis,
        "deskripsi"  to deskripsi,
        "asalDaerah" to asalDaerah,
    )

    fun toEntity(): Culture = Culture(
        nama               = nama,
        jenis              = jenis,
        deskripsi          = deskripsi,
        asalDaerah         = asalDaerah,
        waktuPelaksanaan   = waktuPelaksanaan,
        pathGambar         = pathGambar,
    )
}