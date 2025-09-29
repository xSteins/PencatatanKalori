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
        private var granularityValue = 250
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
        
        /**
         * Calculates remaining calories for the day including exercise calories based on strategy.
         * Uses CalorieStrategy to determine exercise calorie handling based on granularityValue.
         */
        fun calculateRemainingCalories(
            dailyCalorieTarget: Int,
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType
        ): Int {
            val strategy = CalorieStrategy.fromGranularityValue(granularityValue)
            
            return when (goalType) {
                GoalType.LOSE_WEIGHT -> {
                    val exerciseCaloriesEatenBack = (caloriesBurned * strategy.weightLossExercisePercentage).toInt()
                    dailyCalorieTarget - caloriesConsumed + exerciseCaloriesEatenBack
                }
                GoalType.GAIN_WEIGHT -> {
                    val exerciseCaloriesEatenBack = (caloriesBurned * strategy.weightGainExercisePercentage).toInt()
                    dailyCalorieTarget - caloriesConsumed + exerciseCaloriesEatenBack + strategy.weightGainAdditionalCalories
                }
            }
        }
        
        /**
         * Calculates net calories for tracking purposes using current strategy
         */
        fun calculateNetCalories(
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType
        ): Int {
            val strategy = CalorieStrategy.fromGranularityValue(granularityValue)
            
            return when (goalType) {
                GoalType.LOSE_WEIGHT -> {
                    val exerciseCaloriesEatenBack = (caloriesBurned * strategy.weightLossExercisePercentage).toInt()
                    caloriesConsumed - exerciseCaloriesEatenBack
                }
                GoalType.GAIN_WEIGHT -> {
                    val exerciseCaloriesEatenBack = (caloriesBurned * strategy.weightGainExercisePercentage).toInt()
                    caloriesConsumed - exerciseCaloriesEatenBack - strategy.weightGainAdditionalCalories
                }
            }
        }
        
        /**
         * Provides explanation text for calorie adjustments based on current strategy
         */
        fun getCalorieAdjustmentExplanation(goalType: GoalType): String {
            val strategy = CalorieStrategy.fromGranularityValue(granularityValue)
            return strategy.getExerciseCalorieExplanation(goalType)
        }
        
        /**
         * Gets the exercise calorie eating percentage based on current strategy
         */
        fun getExerciseCaloriePercentage(goalType: GoalType): Double {
            val strategy = CalorieStrategy.fromGranularityValue(granularityValue)
            return when (goalType) {
                GoalType.LOSE_WEIGHT -> strategy.weightLossExercisePercentage
                GoalType.GAIN_WEIGHT -> strategy.weightGainExercisePercentage
            }
        }
        
        /**
         * Gets the current calorie strategy
         */
        fun getCurrentStrategy(): CalorieStrategy {
            return CalorieStrategy.fromGranularityValue(granularityValue)
        }
    }
} 