// Copyright 2026 PokeClaw (agents.io). All rights reserved.
// Licensed under the Apache License, Version 2.0.

package io.agents.pokeclaw.agent

enum class LlmProvider { OPENAI, ANTHROPIC, LOCAL }

data class AgentConfig(
    val apiKey: String,
    val baseUrl: String,
    val modelName: String = "",
    val systemPrompt: String = DEFAULT_SYSTEM_PROMPT,
    val maxIterations: Int = 1000,
    val keepRecentRounds: Int = 3,
    val temperature: Double = 0.1,
    val provider: LlmProvider = LlmProvider.OPENAI,
    val streaming: Boolean = false
) {
    companion object {
        const val DEFAULT_SYSTEM_PROMPT =
            """## PERAN
Anda adalah asisten AI di ponsel Android. 
Jika pengguna hanya mengobrol/bertanya, langsung jawab dengan teks (panggil finish). JANGAN gunakan alat ponsel.
Pengecualian: minta info perangkat (baterai, clipboard, notifikasi) gunakan alat langsung (get_device_info, clipboard, get_notifications).

## EKSEKUSI (REFLECTION LOOP)
1. SEE: Panggil get_screen_info.
2. THINK: Rencanakan langkah selanjutnya.
3. ACT: Panggil alat (tap, input, dll).
4. VERIFY: Pastikan aksi berhasil sebelum lanjut. Ulangi sampai selesai.

## ATURAN (PENTING!)
- Selalu panggil get_screen_info sebelum bertindak, kecuali baru saja memanggil system_key/open_app.
- Tutup popup (iklan, izin) segera. Jika butuh login/bayar, berhenti (finish).
- Gunakan wait_after (misal tap(..., wait_after=2000)) agar tidak perlu panggil alat 'wait' terpisah.
- Gunakan scroll_to_find untuk mencari item di layar panjang (jangan swipe manual).
- Gunakan open_app untuk buka aplikasi. Jika tersesat, pakai system_key(back).
- Laporkan DATA, bukan aksi (mis. "25°C Cerah", BUKAN "Saya membuka aplikasi cuaca").
- Gunakan alat langsung (clipboard, get_notifications, get_device_info) BUKAN navigasi manual jika memungkinkan.
- Panggil finish(summary) jika tugas selesai ATAU tidak bisa dilanjutkan. Jangan melooping error.
"""

    }

    /** Java-friendly Builder, maintains compatibility with existing Java callers */
    class Builder {
        private var apiKey: String = ""
        private var baseUrl: String = ""
        private var modelName: String = ""
        private var systemPrompt: String = DEFAULT_SYSTEM_PROMPT
        private var maxIterations: Int = 1000
        private var keepRecentRounds: Int = 3
        private var temperature: Double = 0.1
        private var provider: LlmProvider = LlmProvider.OPENAI
        private var streaming: Boolean = false

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun baseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }
        fun modelName(modelName: String) = apply { this.modelName = modelName }
        fun systemPrompt(systemPrompt: String) = apply { this.systemPrompt = systemPrompt }
        fun maxIterations(maxIterations: Int) = apply { this.maxIterations = maxIterations }
        fun keepRecentRounds(keepRecentRounds: Int) = apply { this.keepRecentRounds = keepRecentRounds }
        fun temperature(temperature: Double) = apply { this.temperature = temperature }
        fun provider(provider: LlmProvider) = apply { this.provider = provider }
        fun streaming(streaming: Boolean) = apply { this.streaming = streaming }

        fun build(): AgentConfig {
            require(apiKey.isNotEmpty() || baseUrl.isNotEmpty()) {
                "Either API key or base URL is required"
            }
            // Inject persistent global instructions (#45) ahead of whatever
            // caller-specific systemPrompt was set. No-op if user hasn't set one.
            val finalSystemPrompt = PromptUtils.applyGlobalPrompt(systemPrompt)
            return AgentConfig(apiKey, baseUrl, modelName, finalSystemPrompt, maxIterations, keepRecentRounds, temperature, provider, streaming)
        }
    }
}
