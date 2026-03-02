package org.delcom.module

import org.delcom.repositories.*
import org.delcom.services.*
import org.koin.dsl.module

val appModule = module {

    // ── Delcom Plants (dipertahankan) ──────────────────────────────────
    single<IPlantRepository> { PlantRepository() }
    single { PlantService(get()) }

    // ── Wisata Samosir ─────────────────────────────────────────────────
    // Destinasi
    single<IDestinationRepository>   { DestinationRepository() }
    single { DestinationService(get()) }

    // Kuliner
    single<ICulinaryRepository>      { CulinaryRepository() }
    single { CulinaryService(get()) }

    // Penginapan
    single<IAccommodationRepository> { AccommodationRepository() }
    single { AccommodationService(get()) }

    // Budaya
    single<ICultureRepository>       { CultureRepository() }
    single { CultureService(get()) }

    // Profile
    single { ProfileService() }
}