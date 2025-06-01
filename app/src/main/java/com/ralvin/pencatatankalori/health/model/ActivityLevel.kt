package com.ralvin.pencatatankalori.health.model

enum class ActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    MODERATELY_ACTIVE(1.55),
    VERY_ACTIVE(1.725);
    // untuk onboarding menu
    fun getDisplayName(): String {
        return when (this) {
            SEDENTARY -> "Sedentary / Rarely Exercise"
            LIGHTLY_ACTIVE -> "Light Activity (Exercise 1-3x per week)"
            MODERATELY_ACTIVE -> "Moderately Active (Exercise 3-5x per week)"
            VERY_ACTIVE -> "Very Active (Exercise 6-7x per week)"
        }
    }
    fun getDescription(): String {
        return when (this) {
            SEDENTARY -> "Little to no physical activity (e.g., unemployed) / rarely exercise."
            LIGHTLY_ACTIVE -> "Moderate activity (e.g., office worker) or occasional exercise."
            MODERATELY_ACTIVE -> "Regular moderate physical activity or exercise 3-5 times per week."
            VERY_ACTIVE -> "Frequent intense physical activity (e.g., intense exercise almost daily)."
        }
    }
}