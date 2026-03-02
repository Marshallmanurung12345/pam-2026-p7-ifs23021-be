package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Culture(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var jenis: String,
    var deskripsi: String,
    var asalDaerah: String,
    var waktuPelaksanaan: String? = null,
    var pathGambar: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)