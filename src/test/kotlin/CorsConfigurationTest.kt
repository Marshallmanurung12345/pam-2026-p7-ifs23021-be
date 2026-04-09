package org.delcom

import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CorsConfigurationTest {

    @AfterTest
    fun tearDown() {
        System.clearProperty("CORS_ALLOWED_ORIGINS")
    }

    @Test
    fun `preflight delete request returns cors headers for configured frontend origin`() = testApplication {
        System.setProperty("CORS_ALLOWED_ORIGINS", "http://localhost:3000")

        application {
            configureCors()
            routing {
                get("/") { }
            }
        }

        val response = client.options("/") {
            header(HttpHeaders.Origin, "http://localhost:3000")
            header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Delete.value)
            header(
                HttpHeaders.AccessControlRequestHeaders,
                listOf(
                    HttpHeaders.ContentType,
                    HttpHeaders.Authorization,
                    HttpHeaders.Accept,
                    HttpHeaders.Origin
                ).joinToString(", ")
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:3000", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `preflight delete request returns cors headers for localhost with random port in development`() = testApplication {
        application {
            configureCors()
            routing {
                get("/") { }
            }
        }

        val response = client.options("/") {
            header(HttpHeaders.Origin, "http://localhost:54298")
            header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Delete.value)
            header(
                HttpHeaders.AccessControlRequestHeaders,
                listOf(
                    HttpHeaders.ContentType,
                    HttpHeaders.Authorization,
                    HttpHeaders.Accept,
                    HttpHeaders.Origin
                ).joinToString(", ")
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:54298", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }

    @Test
    fun `preflight delete request returns cors headers for https localhost with random port in development`() = testApplication {
        application {
            configureCors()
            routing {
                get("/") { }
            }
        }

        val response = client.options("/") {
            header(HttpHeaders.Origin, "https://localhost:54298")
            header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Delete.value)
            header(
                HttpHeaders.AccessControlRequestHeaders,
                listOf(
                    HttpHeaders.ContentType,
                    HttpHeaders.Authorization,
                    HttpHeaders.Accept,
                    HttpHeaders.Origin
                ).joinToString(", ")
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("https://localhost:54298", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }
}
