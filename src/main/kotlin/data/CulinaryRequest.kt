package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Culinary

@Serializable
data class CulinaryRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var bahanUtama: String = "",
    var caraPenyajian: String = "",
    var hargaRataRata: Long = 0,
    var lokasiTersedia: String = "",
    var pathGambar: String? = null,
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama"          to nama,
        "deskripsi"     to deskripsi,
        "bahanUtama"    to bahanUtama,
        "caraPenyajian" to caraPenyajian,
        "hargaRataRata" to hargaRataRata.toString(),
        "lokasiTersedia" to lokasiTersedia,
    )

    fun toEntity(): Culinary = Culinary(
        nama           = nama,
        deskripsi      = deskripsi,
        bahanUtama     = bahanUtama,
        caraPenyajian  = caraPenyajian,
        hargaRataRata  = hargaRataRata,
        lokasiTersedia = lokasiTersedia,
        pathGambar     = pathGambar,
    )
}

