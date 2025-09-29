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
        private var granularityValue = 500
        fun adjustTargetCalorie(newValue: Int){
            this.granularityValue = newValue
        }
        
        fun getGranularityValue(): Int {
            return granularityValue
        }

        fun calculateDailyCaloriesTarget(rmr: Double, activityLevel: ActivityLevel, goalType: GoalType): Double {
            val baseCalories = rmr * activityLevel.multiplier
            
            return when (goalType) {
                GoalType.LOSE_WEIGHT -> baseCalories - granularityValue
                GoalType.GAIN_WEIGHT -> baseCalories + granularityValue
            }
        }
        
        fun calculateDailyCalories(
            weight: Float,
            height: Float,
            age: Int,
            isMale: Boolean,
            activityLevel: ActivityLevel,
            goalType: GoalType
        ): Int {
            val rmr = calculateRMR(
                weight = weight.toDouble(),
                height = height.toDouble(),
                age = age,
                isMale = isMale
            )
            val dailyCaloriesTarget = calculateDailyCaloriesTarget(rmr, activityLevel, goalType)
            return dailyCaloriesTarget.toInt()
        }
    }
} 