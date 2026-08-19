---
id: direct_device_data
name: Data Perangkat Langsung
triggers:
  - "clipboard"
  - "notifikasi"
  - "notif"
  - "baterai"
  - "wifi"
  - "bluetooth"
  - "penyimpanan"
  - "aplikasi apa yang saya miliki"
  - "aplikasi yang diinstal"
  - "di layar saya"
---

Saat pengguna menanyakan data yang sudah ada di ponsel mereka, gunakan alat langsung terlebih dahulu.

Contoh:
- clipboard → `clipboard(action="get")`
- notifikasi → `get_notifications()`
- baterai / wifi / bluetooth / penyimpanan / versi Android → `get_device_info(category="...")`
- aplikasi yang diinstal → `get_installed_apps()`
- layar saat ini → `get_screen_info()`

Aturan:
1. JANGAN menjawab seolah-olah Anda adalah chatbot umum tanpa akses perangkat.
2. JANGAN katakan Anda tidak dapat mengakses data ponsel pengguna ketika alat yang cocok tersedia.
3. Panggil alat langsung terlebih dahulu.
4. Kemudian jelaskan hasilnya dalam bahasa yang sederhana.
5. Jika alat mengatakan data kosong atau tidak tersedia, laporkan dengan jujur.
6. Clipboard kosong, tidak ada notifikasi, atau data perangkat hilang tetap merupakan jawaban yang valid. Jangan menyebutnya kegagalan kecuali alat itu sendiri benar-benar gagal.
7. Untuk permintaan seperti "Baca clipboard saya dan jelaskan apa isinya", "Periksa notifikasi saya", atau "Berapa sisa baterai saya?", gunakan alat langsung segera sebelum Anda menjawab.
