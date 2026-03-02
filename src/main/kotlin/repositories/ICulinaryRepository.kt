package org.delcom.repositories

import org.delcom.entities.Culinary

interface ICulinaryRepository {
    suspend fun getCulinaries(search: String): List<Culinary>
    suspend fun getCulinaryById(id: String): Culinary?
    suspend fun getCulinaryByName(nama: String): Culinary?
    suspend fun addCulinary(culinary: Culinary): String
    suspend fun updateCulinary(id: String, newCulinary: Culinary): Boolean
    suspend fun removeCulinary(id: String): Boolean
}