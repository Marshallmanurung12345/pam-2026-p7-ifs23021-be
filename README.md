# pam-2026-p7-ifs23021-be

Backend Ktor untuk data profil, tanaman, destinasi, kuliner, penginapan, dan budaya.

## Menjalankan aplikasi

Gunakan salah satu perintah berikut:

- `./gradlew run`
- `./gradlew build`
- `./gradlew test`

Secara default aplikasi berjalan di `http://127.0.0.1:8000` sesuai konfigurasi [application.yaml](/D:/SEMESTER%206/PAM/pam-2026-p7-ifs23021-be/src/main/resources/application.yaml).

## HTTP Request Collection

File [app.http](/D:/SEMESTER%206/PAM/pam-2026-p7-ifs23021-be/app.http) sudah disusun untuk pengujian manual semua endpoint:

- `profile`
- `plants`
- `destinations`
- `culinaries`
- `accommodations`
- `cultures`

Jika ingin menguji deployment, ganti `@BaseUrl` di `app.http` ke URL server yang aktif.

