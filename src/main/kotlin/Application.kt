package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.serialization.json.Json
import org.delcom.module.appModule
import org.delcom.helpers.configureDatabases
import org.koin.ktor.plugin.Koin
import java.io.File

fun main(args: Array<String>) {
    loadEnvironmentVariables()
    EngineMain.main(args)
}

private fun loadEnvironmentVariables() {
    val envFilename = when {
        File(".env").exists() -> ".env"
        File(".env.example").exists() -> ".env.example"
        else -> null
    } ?: return

    val env = dotenv {
        directory = "."
        filename = envFilename
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }

    env.entries().forEach { entry ->
        // Prioritas: env OS > system property > nilai dari file .env
        if (System.getenv(entry.key).isNullOrBlank() && System.getProperty(entry.key).isNullOrBlank()) {
            System.setProperty(entry.key, entry.value)
        }
    }
}

fun Application.module() {
    configureCors()

    install(ContentNegotiation) {
        json(
            Json {
                explicitNulls = false
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(Koin) {
        modules(appModule)
    }

    configureDatabases()
    configureRouting()
}

fun Application.configureCors() {
    // Baca CORS_ALLOWED_ORIGINS dari env OS terlebih dahulu, baru dari system property
    val rawOrigins = (System.getenv("CORS_ALLOWED_ORIGINS") ?: System.getProperty("CORS_ALLOWED_ORIGINS") ?: "")
        .split(",")
        .map(String::trim)
        .filter(String::isNotBlank)

    // Mode development: aktif jika CORS_ALLOWED_ORIGINS tidak diset sama sekali
    val isDevelopmentMode = rawOrigins.isEmpty()

    install(CORS) {
        if (!isDevelopmentMode && "*" in rawOrigins) {
            // Izinkan semua origin (production explicit wildcard)
            anyHost()
        } else if (!isDevelopmentMode) {
            // Hanya izinkan origin yang terdaftar
            rawOrigins.forEach { origin ->
                allowHost(
                    host = Url(origin).let { "${it.host}:${it.port}" }.trimEnd(':'),
                    schemes = listOf(Url(origin).protocol.name)
                )
            }
        } else {
            // Development mode: izinkan semua localhost/127.0.0.1 dengan port apapun
            // (termasuk Flutter Web yang menggunakan port acak)
            allowOrigins { origin ->
                isDevelopmentOrigin(origin)
            }
        }

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.Origin)

        allowNonSimpleContentTypes = true
        maxAgeInSeconds = 3_600
    }
}

private fun isDevelopmentOrigin(origin: String): Boolean = runCatching {
    val parsed = Url(origin)
    parsed.protocol.name in setOf("http", "https") && parsed.host in setOf("localhost", "127.0.0.1")
}.getOrDefault(false)
