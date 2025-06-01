package com.ralvin.pencatatankalori.health.model

enum class GoalType {
    LOSE_WEIGHT,
    GAIN_WEIGHT;

    fun getDisplayName(): String {
        return when (this) {
            LOSE_WEIGHT -> "Menurunkan Berat Badan"
            GAIN_WEIGHT -> "Menambah Berat Badan"
        }
    }
} 