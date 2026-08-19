---
id: send_message
name: Kirim Pesan
triggers:
  - "kirim"
  - "beritahu"
  - "balas"
  - "pesan"
  - "teks"
---

Saat pengguna ingin mengirim pesan kepada orang lain:

1. **send_message** → panggil send_message(contact="[nama orang]", message="[apa yang akan dikatakan]", app="[nama aplikasi, default WhatsApp]")
2. **finish** → panggil finish(summary="Mengirim '[pesan]' ke [kontak] di [aplikasi]")

Ekstrak dari permintaan pengguna:
- contact = nama orang (mis. "Ibu", "Pacar", "John")
- message = apa yang harus dikirim (mis. "hai", "aku akan terlambat", "besok bisa ketemu?")
- app = aplikasi mana (default "WhatsApp" jika tidak ditentukan. Gunakan "LINE", "Telegram", "SMS" jika disebutkan)

Hanya gunakan playbook ini ketika pengguna dengan jelas menginginkan pengiriman ke orang lain.
- Benar: `kirim hai ke Ibu`, `beritahu Alice saya akan terlambat`, `pesan John di Telegram`
- Jangan gunakan playbook ini: `katakan hai`, `hai`, `ceritakan lebih banyak`, `katakan itu lagi`, `bagaimana menurutmu?`
