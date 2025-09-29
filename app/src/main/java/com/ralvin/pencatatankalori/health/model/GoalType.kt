package com.ralvin.pencatatankalori.health.model

enum class GoalType {
    LOSE_WEIGHT,
    GAIN_WEIGHT;
    // for onboarding / userprofile settings pop up
    fun getDisplayName(): String {
        return when (this) {
            LOSE_WEIGHT -> "Weight Loss (Cutting)"
            GAIN_WEIGHT -> "Weight Gain (Bulking)"
        }
    }
    
    fun getDescription(): String {
        return when (this) {
            LOSE_WEIGHT -> "Reducing weight by creating calorie deficit"
            GAIN_WEIGHT -> "Bulking out weight by gaining calorie"
        }
    }
} 