package org.delcom.repositories

import org.delcom.entities.Accommodation

interface IAccommodationRepository {
    suspend fun getAccommodations(search: String, tipe: String): List<Accommodation>
    suspend fun getAccommodationById(id: String): Accommodation?
    suspend fun getAccommodationByName(nama: String): Accommodation?
    suspend fun addAccommodation(accommodation: Accommodation): String
    suspend fun updateAccommodation(id: String, newAccommodation: Accommodation): Boolean
    suspend fun removeAccommodation(id: String): Boolean
}