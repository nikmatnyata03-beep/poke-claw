// Copyright 2026 PokeClaw (agents.io). All rights reserved.
// Licensed under the Apache License, Version 2.0.

package io.agents.pokeclaw.agent

import io.agents.pokeclaw.utils.XLog
import java.util.ArrayDeque

/**
 * Detects stuck agent loops using 5 signals.
 *
 * Architecture reference:
 * - Sliding window (8 steps), same-action, screen-unchanged, high-repetition
 * - ralph-claude-code: repeated-error detection
 * - PokeClaw original: screen diff (previousScreenTexts)
 *
 * Recovery is 3-level:
 *   Level 1: Inject recovery hint into prompt
 *   Level 2: Suggest strategy switch (different tool)
 *   Level 3: Auto-kill (force finish)
 */
class StuckDetector(private val windowSize: Int = 20) {

    private val actions = ArrayDeque<String>(windowSize + 1)
    private val screenHashes = ArrayDeque<Int>(windowSize + 1)
    private val screenDiffCounts = ArrayDeque<Int>(windowSize + 1)
    private val errors = ArrayDeque<String>(windowSize + 1)
    private var consecutiveStuckSteps = 0

    sealed class Signal(val description: String) {
        class SameAction(val action: String, val count: Int) :
            Signal("Same action '$action' repeated $count times consecutively")

        class ScreenUnchanged(val steps: Int) :
            Signal("Screen unchanged for $steps consecutive steps")

        class ZeroDiff(val steps: Int) :
            Signal("Zero screen text diff for $steps consecutive steps")

        class HighRepetition(val action: String, val count: Int, val window: Int) :
            Signal("Action '$action' appeared $count times in last $window steps")

        class RepeatedError(val error: String, val count: Int) :
            Signal("Same error repeated $count times consecutively")
    }

    enum class RecoveryLevel {
        HINT,           // Level 1: inject recovery hint
        STRATEGY_SWITCH, // Level 2: suggest different approach
        AUTO_KILL        // Level 3: force finish
    }

    data class Detection(
        val signal: Signal,
        val level: RecoveryLevel,
        val recoveryHint: String
    )

    /**
     * Record one agent loop step and check for stuck patterns.
     *
     * @param action tool name + args fingerprint (e.g. "find_and_tap:cat videos")
     * @param screenHash hash of current screen content
     * @param screenDiffCount number of text lines changed vs previous screen
     * @param error error message if tool failed, null otherwise
     * @return Detection if stuck, null if OK
     */
    fun record(action: String, screenHash: Int, screenDiffCount: Int, error: String?): Detection? {
        // Add to sliding windows
        actions.addLast(action)
        if (actions.size > windowSize) actions.removeFirst()

        screenHashes.addLast(screenHash)
        if (screenHashes.size > windowSize) screenHashes.removeFirst()

        screenDiffCounts.addLast(screenDiffCount)
        if (screenDiffCounts.size > windowSize) screenDiffCounts.removeFirst()

        if (error != null) {
            errors.addLast(error)
            if (errors.size > windowSize) errors.removeFirst()
        } else {
            errors.clear() // consecutive errors broken
        }

        // Check all 5 signals
        val signal = checkSameAction()
            ?: checkScreenUnchanged()
            ?: checkZeroDiff()
            ?: checkHighRepetition()
            ?: checkRepeatedError()

        if (signal != null) {
            consecutiveStuckSteps++
            val level = when {
                consecutiveStuckSteps >= 15 -> RecoveryLevel.AUTO_KILL
                consecutiveStuckSteps >= 8 -> RecoveryLevel.STRATEGY_SWITCH
                else -> RecoveryLevel.HINT
            }
            val hint = generateRecoveryHint(signal, level)
            val detection = Detection(signal, level, hint)
            XLog.w(TAG, "[StuckDetector] ${signal.description} → Level ${level.name}")
            return detection
        }

        // No stuck signal → reset counter
        consecutiveStuckSteps = 0
        return null
    }

    private fun checkSameAction(): Signal? {
        if (actions.size < 3) return null
        val last3 = actions.toList().takeLast(3)
        return if (last3.all { it == last3[0] }) {
            Signal.SameAction(last3[0].take(50), 3)
        } else null
    }

    private fun checkScreenUnchanged(): Signal? {
        if (screenHashes.size < 3) return null
        val last3 = screenHashes.toList().takeLast(3)
        return if (last3.all { it == last3[0] }) {
            Signal.ScreenUnchanged(3)
        } else null
    }

    private fun checkZeroDiff(): Signal? {
        if (screenDiffCounts.size < 3) return null
        val last3 = screenDiffCounts.toList().takeLast(3)
        return if (last3.all { it == 0 }) {
            Signal.ZeroDiff(3)
        } else null
    }

    private fun checkHighRepetition(): Signal? {
        if (actions.size < windowSize) return null
        val counts = actions.groupingBy { it }.eachCount()
        val maxEntry = counts.maxByOrNull { it.value } ?: return null
        return if (maxEntry.value >= 3) {
            Signal.HighRepetition(maxEntry.key.take(50), maxEntry.value, windowSize)
        } else null
    }

    private fun checkRepeatedError(): Signal? {
        if (errors.size < 3) return null
        val last3 = errors.toList().takeLast(3)
        return if (last3.all { it == last3[0] }) {
            Signal.RepeatedError(last3[0].take(80), 3)
        } else null
    }

    private fun generateRecoveryHint(signal: Signal, level: RecoveryLevel): String {
        val base = when (signal) {
            is Signal.SameAction -> when {
                signal.action.contains("find_and_tap") ->
                    "Tindakan find_and_tap Anda tidak berfungsi. Coba gunakan tap_node dengan ID node tertentu dari get_screen_info, atau gunakan system_key(key=\"enter\") untuk mengirim."
                signal.action.contains("scroll") ->
                    "Anda mungkin telah mencapai akhir konten yang dapat di-scroll. Coba pendekatan berbeda atau tekan back."
                signal.action.contains("tap") ->
                    "Tindakan tap Anda mungkin tidak mengenai target yang tepat. Panggil get_screen_info untuk menyegarkan status layar dan mencoba elemen yang berbeda."
                else ->
                    "Tindakan terakhir Anda '${signal.action}' tidak membuahkan hasil. Coba pendekatan yang sama sekali berbeda."
            }
            is Signal.ScreenUnchanged ->
                "Layar belum berubah selama ${signal.steps} langkah. Tindakan Anda mungkin tidak memiliki efek. Coba tekan system_key(key=\"back\") atau system_key(key=\"home\") dan mulai ulang dari sudut yang berbeda."
            is Signal.ZeroDiff ->
                "Tidak ada konten baru yang muncul di layar. Anda mungkin terjebak. Coba navigasi ke luar dan kembali, atau gunakan alat yang berbeda."
            is Signal.HighRepetition ->
                "Anda mengulangi '${signal.action}' terlalu sering. Pendekatan ini tidak berhasil. Coba sesuatu yang secara mendasar berbeda."
            is Signal.RepeatedError ->
                "Kesalahan yang sama terus terjadi: '${signal.error}'. Jangan coba lagi pendekatan yang sama. Coba alat atau strategi lain."
        }

        return when (level) {
            RecoveryLevel.HINT ->
                "[Pemberitahuan Sistem] $base"
            RecoveryLevel.STRATEGY_SWITCH ->
                "[Peringatan Sistem] Anda telah terjebak selama beberapa putaran. $base Jika Anda tidak dapat membuat kemajuan, panggil finish dan jelaskan apa yang salah."
            RecoveryLevel.AUTO_KILL ->
                "" // caller handles auto-kill
        }
    }

    fun reset() {
        actions.clear()
        screenHashes.clear()
        screenDiffCounts.clear()
        errors.clear()
        consecutiveStuckSteps = 0
    }

    companion object {
        private const val TAG = "StuckDetector"
    }
}
