package org.delcom.services

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.delcom.data.DataResponse
import java.io.File

class ProfileService {
    suspend fun getProfile(call: ApplicationCall) {
        val response = DataResponse(
            "success",
            "Berhasil mengambil profile pengembang",
            mapOf(
                "username" to "marshall.manurung",
                "nama"     to "Marshall Manurung",
                "tentang"  to "Saya adalah seorang developer yang tertarik pada mobile development, backend API, dan pengembangan aplikasi wisata daerah khususnya Samosir.",
            )
        )
        call.respond(response)
    }

    suspend fun getProfilePhoto(call: ApplicationCall) {
        val file = File("uploads/profile/me.png")
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}