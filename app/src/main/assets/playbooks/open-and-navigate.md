---
id: open_and_navigate
name: Buka Aplikasi dan Lakukan Sesuatu
triggers:
  - "buka"
  - "pergi ke"
  - "cek"
  - "navigasi"
---

Saat pengguna ingin membuka aplikasi dan melakukan sesuatu di dalamnya:

1. **open_app** → panggil open_app(package_name="[app]") untuk membuka aplikasi PERTAMA
2. **get_screen_info** → lihat apa yang ada di layar
3. Lakukan apa yang diminta pengguna (ketuk, gulir, baca, dll.) menggunakan alat yang sesuai
4. **finish** → panggil finish(summary="[apa yang Anda temukan atau lakukan]")

Pemetaan aplikasi umum:
- "email" atau "gmail" → open_app(package_name="com.google.android.gm")
- "youtube" → open_app(package_name="com.google.android.youtube")
- "chrome" atau "browser" → open_app(package_name="com.android.chrome")
- "kamera" → open_app(package_name="com.android.camera2")
- "pengaturan" → open_app(package_name="com.android.settings")
- Untuk aplikasi lainnya → panggil get_installed_apps(keyword="[nama aplikasi]") terlebih dahulu untuk menemukan nama paket

PENTING: Selalu buka aplikasi PERTAMA. Jangan pernah bertindak di layar saat ini jika pengguna menginginkan Anda berada di aplikasi yang berbeda.
