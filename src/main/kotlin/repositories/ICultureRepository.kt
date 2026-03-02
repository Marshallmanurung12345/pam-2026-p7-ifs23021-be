package org.delcom.repositories

import org.delcom.entities.Culture

interface ICultureRepository {
    suspend fun getCultures(search: String, jenis: String): List<Culture>
    suspend fun getCultureById(id: String): Culture?
    suspend fun getCultureByName(nama: String): Culture?
    suspend fun addCulture(culture: Culture): String
    suspend fun updateCulture(id: String, newCulture: Culture): Boolean
    suspend fun removeCulture(id: String): Boolean
}