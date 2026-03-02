package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val plantService:         PlantService         by inject()
    val profileService:       ProfileService       by inject()
    val destinationService:   DestinationService   by inject()
    val culinaryService:      CulinaryService      by inject()
    val accommodationService: AccommodationService by inject()
    val cultureService:       CultureService       by inject()

    install(StatusPages) {
        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)
            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status  = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data    = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                status  = HttpStatusCode.fromValue(500),
                message = ErrorResponse(status = "error", message = cause.message ?: "Unknown error", data = "")
            )
        }
    }

    routing {
        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Marshall Manurung.")
        }

        // ── Delcom Plants (dipertahankan) ──────────────────────────────
        route("/plants") {
            get              { plantService.getAllPlants(call) }
            post             { plantService.createPlant(call) }
            get("/{id}")     { plantService.getPlantById(call) }
            put("/{id}")     { plantService.updatePlant(call) }
            delete("/{id}")  { plantService.deletePlant(call) }
            get("/{id}/image") { plantService.getPlantImage(call) }
        }

        // ── Profile ────────────────────────────────────────────────────
        route("/profile") {
            get        { profileService.getProfile(call) }
            get("/photo") { profileService.getProfilePhoto(call) }
        }

        // ── Wisata Samosir — Destinasi ─────────────────────────────────
        // GET    /destinations?search=&kategori=
        // POST   /destinations
        // GET    /destinations/{id}
        // PUT    /destinations/{id}
        // DELETE /destinations/{id}
        // GET    /destinations/{id}/image
        route("/destinations") {
            get              { destinationService.getAllDestinations(call) }
            post             { destinationService.createDestination(call) }
            get("/{id}")     { destinationService.getDestinationById(call) }
            put("/{id}")     { destinationService.updateDestination(call) }
            delete("/{id}")  { destinationService.deleteDestination(call) }
            get("/{id}/image") { destinationService.getDestinationImage(call) }
        }

        // ── Wisata Samosir — Kuliner ───────────────────────────────────
        // GET    /culinaries?search=
        // POST   /culinaries
        // GET    /culinaries/{id}
        // PUT    /culinaries/{id}
        // DELETE /culinaries/{id}
        // GET    /culinaries/{id}/image
        route("/culinaries") {
            get              { culinaryService.getAllCulinaries(call) }
            post             { culinaryService.createCulinary(call) }
            get("/{id}")     { culinaryService.getCulinaryById(call) }
            put("/{id}")     { culinaryService.updateCulinary(call) }
            delete("/{id}")  { culinaryService.deleteCulinary(call) }
            get("/{id}/image") { culinaryService.getCulinaryImage(call) }
        }

        // ── Wisata Samosir — Penginapan ────────────────────────────────
        // GET    /accommodations?search=&tipe=
        // POST   /accommodations
        // GET    /accommodations/{id}
        // PUT    /accommodations/{id}
        // DELETE /accommodations/{id}
        // GET    /accommodations/{id}/image
        route("/accommodations") {
            get              { accommodationService.getAllAccommodations(call) }
            post             { accommodationService.createAccommodation(call) }
            get("/{id}")     { accommodationService.getAccommodationById(call) }
            put("/{id}")     { accommodationService.updateAccommodation(call) }
            delete("/{id}")  { accommodationService.deleteAccommodation(call) }
            get("/{id}/image") { accommodationService.getAccommodationImage(call) }
        }

        // ── Wisata Samosir — Budaya ────────────────────────────────────
        // GET    /cultures?search=&jenis=
        // POST   /cultures
        // GET    /cultures/{id}
        // PUT    /cultures/{id}
        // DELETE /cultures/{id}
        // GET    /cultures/{id}/image
        route("/cultures") {
            get              { cultureService.getAllCultures(call) }
            post             { cultureService.createCulture(call) }
            get("/{id}")     { cultureService.getCultureById(call) }
            put("/{id}")     { cultureService.updateCulture(call) }
            delete("/{id}")  { cultureService.deleteCulture(call) }
            get("/{id}/image") { cultureService.getCultureImage(call) }
        }
    }
}