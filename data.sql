-- ============================================================
-- Schema untuk Wisata Samosir
-- ============================================================

-- Tabel Plants (Delcom Plants - dipertahankan)
CREATE TABLE IF NOT EXISTS plants (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Tabel Destinasi Wisata
CREATE TABLE IF NOT EXISTS destinations (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL UNIQUE,
    kategori VARCHAR(50) NOT NULL,
    deskripsi TEXT NOT NULL,
    lokasi VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    harga_tiket BIGINT NOT NULL DEFAULT 0,
    jam_buka VARCHAR(100),
    kontak VARCHAR(100),
    path_gambar VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Tabel Kuliner
CREATE TABLE IF NOT EXISTS culinaries (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(150) NOT NULL,
    deskripsi TEXT NOT NULL,
    bahan_utama TEXT NOT NULL,
    cara_penyajian TEXT NOT NULL,
    harga_rata_rata BIGINT NOT NULL DEFAULT 0,
    lokasi_tersedia TEXT NOT NULL,
    path_gambar VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Tabel Penginapan
CREATE TABLE IF NOT EXISTS accommodations (
                                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(150) NOT NULL,
    tipe VARCHAR(50) NOT NULL,
    deskripsi TEXT NOT NULL,
    alamat TEXT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    harga_mulai BIGINT NOT NULL DEFAULT 0,
    fasilitas TEXT NOT NULL,
    kontak VARCHAR(100),
    path_gambar VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Tabel Budaya
CREATE TABLE IF NOT EXISTS cultures (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(150) NOT NULL,
    jenis VARCHAR(50) NOT NULL,
    deskripsi TEXT NOT NULL,
    asal_daerah VARCHAR(100) NOT NULL,
    waktu_pelaksanaan VARCHAR(100),
    path_gambar VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );





