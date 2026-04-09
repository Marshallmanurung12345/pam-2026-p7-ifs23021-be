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
    }

    if (envFilename == null) {
        return
    }

    val env = dotenv {
        directory = "."
        filename = envFilename
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }

    env.entries().forEach {
        if (System.getProperty(it.key).isNullOrBlank()) {
            System.setProperty(it.key, it.value)
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
    val allowedOrigins = loadAllowedOrigins(environment)
    val useDevelopmentOriginFallback = System.getProperty("CORS_ALLOWED_ORIGINS").isNullOrBlank()

    install(CORS) {
        if ("*" in allowedOrigins) {
            anyHost()
        } else {
            allowOrigins { origin ->
                origin in allowedOrigins || (useDevelopmentOriginFallback && isDevelopmentOrigin(origin))
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
    val parsedOrigin = Url(origin)
    parsedOrigin.protocol.name == "http" && parsedOrigin.host in setOf("localhost", "127.0.0.1")
}.getOrDefault(false)

private fun loadAllowedOrigins(environment: ApplicationEnvironment): List<String> {
    val configuredOrigins = System.getProperty("CORS_ALLOWED_ORIGINS")
        ?.split(",")
        ?.map(String::trim)
        ?.filter(String::isNotBlank)
        .orEmpty()

    if (configuredOrigins.isNotEmpty()) {
        return configuredOrigins
    }

    val deploymentHost = environment.config.propertyOrNull("ktor.deployment.host")?.getString()
    return buildList {
        add("http://localhost:3000")
        add("http://127.0.0.1:3000")
        add("http://localhost:4173")
        add("http://127.0.0.1:4173")
        add("http://localhost:5173")
        add("http://127.0.0.1:5173")
        add("http://localhost:8080")
        add("http://127.0.0.1:8080")

        if (!deploymentHost.isNullOrBlank() && deploymentHost != "0.0.0.0") {
            add("http://$deploymentHost:3000")
            add("http://$deploymentHost:8080")
        }
    }.distinct()
}
