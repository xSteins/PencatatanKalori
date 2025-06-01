package com.ralvin.pencatatankalori.health.model

class MifflinModel {
    companion object {
        fun calculateRMR(weight: Double, height: Double, age: Int, isMale: Boolean): Double {
            return if (isMale) {
                (10 * weight) + (6.25 * height) - (5 * age) + 5
            } else {
                (10 * weight) + (6.25 * height) - (5 * age) - 161
            }
        }

        fun calculateDailyCaloriesTarget(rmr: Double, activityLevel: ActivityLevel, goalType: GoalType): Double {
            val baseCalories = rmr * activityLevel.multiplier
            
            return when (goalType) {
                GoalType.LOSE_WEIGHT -> baseCalories - 500
                GoalType.GAIN_WEIGHT -> baseCalories + 500
            }
        }
    }
} 