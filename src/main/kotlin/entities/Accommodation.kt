package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Accommodation(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var tipe: String,
    var deskripsi: String,
    var alamat: String,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var hargaMulai: Long = 0,
    var fasilitas: String,
    var kontak: String? = null,
    var pathGambar: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)