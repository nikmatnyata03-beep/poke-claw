// Copyright 2026 PokeClaw (agents.io). All rights reserved.
// Licensed under the Apache License, Version 2.0.

package io.agents.pokeclaw.agent

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.agents.pokeclaw.utils.XLog

/**
 * Tier 2: LLM-based task classifier.
 * Uses a single short LLM call (~200 word prompt) to classify the task
 * into: intent, skill, agent, chat, or impossible.
 *
 * Single-call LLM classification for task routing.
 * Handles ~20% of tasks with 1 LLM call, < 3 seconds.
 */
object TaskClassifier {

    private const val TAG = "TaskClassifier"

    data class Classification(
        @SerializedName("type") val type: String = "agent",
        @SerializedName("app") val app: String? = null,
        @SerializedName("skill_id") val skillId: String? = null,
        @SerializedName("sub_goal") val subGoal: String? = null,
        @SerializedName("params") val params: Map<String, String>? = null
    )

    /**
     * Build the classifier system prompt.
     * This prompt is much shorter than the agent loop's system prompt (~200 words vs ~2000 words).
     *
     * @param skillSummaries list of "id: description" for available skills
     */
    fun buildClassifierPrompt(skillSummaries: List<String>): String {
        val skillList = if (skillSummaries.isEmpty()) "Tidak tersedia"
            else skillSummaries.joinToString("\n") { "- $it" }

        return """Anda mengklasifikasikan tugas ponsel. Kembalikan JSON saja, tanpa penjelasan.

Format output: {"type": "...", "app": "...", "skill_id": "...", "sub_goal": "...", "params": {...}}

Tipe:
- "intent": dapat dilakukan dengan satu intent Android (panggilan, alarm, buka URL, buka pengaturan)
- "skill": cocok dengan keterampilan yang tersedia di bawah ini — atur skill_id dan params
- "agent": memerlukan interaksi UI (mengetuk, menggulir, membaca layar). Atur app dan sub_goal
- "chat": pertanyaan percakapan, tidak perlu kontrol ponsel
- "impossible": tidak dapat dilakukan di ponsel

Keterampilan yang tersedia:
$skillList

Aturan:
- Jika tugas menyebutkan "cari" atau "temukan" dalam sebuah aplikasi → skill_id: "search_in_app"
- Jika tugas melibatkan perpesanan (WhatsApp, SMS, email) → type: "agent" (memerlukan navigasi UI)
- Jika tugas ambigu antara skill dan agent, pilih agent untuk tugas kompleks, skill untuk yang sederhana
- sub_goal harus berupa versi tugas yang disederhanakan untuk loop agen
- app harus berupa nama aplikasi umum seperti "YouTube", "WhatsApp", "Chrome", "Clock"
"""
    }

    /**
     * Parse the LLM's JSON response into a Classification.
     * Handles common LLM response issues (markdown wrapping, extra text).
     */
    fun parseResponse(response: String): Classification {
        try {
            // Strip markdown code blocks if present
            var json = response.trim()
            if (json.startsWith("```")) {
                json = json.substringAfter("\n").substringBeforeLast("```").trim()
            }
            // Find JSON object
            val start = json.indexOf('{')
            val end = json.lastIndexOf('}')
            if (start >= 0 && end > start) {
                json = json.substring(start, end + 1)
            }
            return Gson().fromJson(json, Classification::class.java)
        } catch (e: Exception) {
            XLog.w(TAG, "Failed to parse classifier response: $response", e)
            return Classification(type = "agent", subGoal = response)
        }
    }
}
