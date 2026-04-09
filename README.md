# pam-2026-p7-ifs23021-be

Backend Ktor untuk data profil, tanaman, destinasi, kuliner, penginapan, dan budaya.

## Menjalankan aplikasi

Gunakan salah satu perintah berikut:

- `./gradlew run`
- `./gradlew build`
- `./gradlew test`

Secara default aplikasi berjalan di `http://127.0.0.1:8000` sesuai konfigurasi [application.yaml](/D:/SEMESTER%206/PAM/pam-2026-p7-ifs23021-be/src/main/resources/application.yaml).

## Konfigurasi CORS

Backend membaca origin frontend yang diizinkan dari env `CORS_ALLOWED_ORIGINS`.
Jika env ini dikosongkan, backend akan mengizinkan origin local development dari `http://localhost:*` dan `http://127.0.0.1:*`.

Contoh:

- `CORS_ALLOWED_ORIGINS=http://localhost:3000`
- `CORS_ALLOWED_ORIGINS=http://localhost:3000,https://frontend.example.com`
- kosongkan `CORS_ALLOWED_ORIGINS` untuk mengizinkan frontend lokal dengan port acak

Method yang diizinkan: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`.
Header yang diizinkan: `Content-Type`, `Authorization`, `Accept`, `Origin`.

## HTTP Request Collection

File [app.http](/D:/SEMESTER%206/PAM/pam-2026-p7-ifs23021-be/app.http) sudah disusun untuk pengujian manual semua endpoint:

- `profile`
- `plants`
- `destinations`
- `culinaries`
- `accommodations`
- `cultures`

Jika ingin menguji deployment, ganti `@BaseUrl` di `app.http` ke URL server yang aktif.

