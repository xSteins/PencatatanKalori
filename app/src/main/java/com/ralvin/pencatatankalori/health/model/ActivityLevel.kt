package com.ralvin.pencatatankalori.health.model

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    MODERATELY_ACTIVE(1.55),
    VERY_ACTIVE(1.725);

    // untuk onboarding menu
    fun getDisplayName(): String {
        return when (this) {
            SEDENTARY -> "Tidak Aktif"
            LIGHTLY_ACTIVE -> "Cukup Aktif"
            MODERATELY_ACTIVE -> "Lumayan Aktif"
            VERY_ACTIVE -> "Sangat Aktif"
        }
    }

    fun getDescription(): String {
        return when (this) {
            SEDENTARY -> "Jarang bergerak / Tidak ada aktivitas"
            LIGHTLY_ACTIVE -> "Cukup Aktif, Sesekali berolah-raga (Cth: Pelajar / Pekerja)"
            MODERATELY_ACTIVE -> "Lumayan aktif, sering berolah-raga"
            VERY_ACTIVE -> "Sangat Aktif, Sering berolah-raga"
        }
    }
}