---
id: open_and_search
name: Buka Aplikasi dan Cari
triggers:
  - "cari"
  - "temukan"
  - "cari tahu"
  - "mencari"
---

Saat pengguna ingin mencari sesuatu di aplikasi:

1. **open_app** → panggil open_app(package_name="[app]") untuk membuka aplikasi PERTAMA. JANGAN lewati langkah ini.
2. **get_screen_info** → lihat apa yang ada di layar setelah aplikasi terbuka
3. **find_and_tap** → panggil find_and_tap(text="Cari" atau "Search") untuk mengetuk bilah pencarian atau ikon
4. **input_text** → panggil input_text(text="[kueri pencarian]") untuk mengetik kueri
5. **system_key** → panggil system_key(key="enter") untuk mengirimkan pencarian
6. **get_screen_info** → baca hasilnya
7. **finish** → panggil finish(summary="[jelaskan apa yang Anda temukan]")

Jika pengguna mengatakan "cari [kueri] di [aplikasi]":
- aplikasi = nama aplikasi (YouTube, Chrome, Play Store, dll.)
- kueri = apa yang dicari

PENTING: Selalu buka aplikasi PERTAMA (langkah 1). Jangan pernah mengetik ke layar saat ini tanpa membuka aplikasi target.
