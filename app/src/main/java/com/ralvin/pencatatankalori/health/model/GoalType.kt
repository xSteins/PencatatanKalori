package com.ralvin.pencatatankalori.health.model

enum class GoalType {
    LOSE_WEIGHT,
    GAIN_WEIGHT;
    // for onboarding / userprofile settings pop up
    fun getDisplayName(): String {
        return when (this) {
            LOSE_WEIGHT -> "Menurunkan Berat Badan"
            GAIN_WEIGHT -> "Menambah Berat Badan"
        }
    }
    
    fun getDescription(): String {
        return when (this) {
            LOSE_WEIGHT -> "Target untuk mengurangi berat badan dengan defisit kalori"
            GAIN_WEIGHT -> "Target untuk menambah berat badan dengan surplus kalori"
        }
    }
} 