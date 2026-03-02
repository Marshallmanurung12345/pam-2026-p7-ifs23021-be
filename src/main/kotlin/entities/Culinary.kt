package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Culinary(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var deskripsi: String,
    var bahanUtama: String,
    var caraPenyajian: String,
    var hargaRataRata: Long = 0,
    var lokasiTersedia: String,
    var pathGambar: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)