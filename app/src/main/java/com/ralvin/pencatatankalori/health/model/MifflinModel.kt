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
        private var calorieStrategy = CalorieStrategy.MODERATE
        private var isAdvancedEnabled = false
        
        fun adjustTargetCalorie(newValue: Int) {
            this.granularityValue = newValue
        }
        
        fun getGranularityValue(): Int {
            return granularityValue
        }
        
        fun setCalorieStrategy(strategy: CalorieStrategy) {
            this.calorieStrategy = strategy
        }
        
        fun getCalorieStrategy(): CalorieStrategy {
            return calorieStrategy
        }
        
        fun setAdvancedEnabled(enabled: Boolean) {
            this.isAdvancedEnabled = enabled
        }
        
        fun isAdvancedEnabled(): Boolean {
            return isAdvancedEnabled
        }

        fun calculateDailyCaloriesTarget(rmr: Double, activityLevel: ActivityLevel, goalType: GoalType, granularityValue: Int): Double {
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
            goalType: GoalType,
            granularityValue: Int = getGranularityValue()
        ): Int {
            val rmr = calculateRMR(
                weight = weight.toDouble(),
                height = height.toDouble(),
                age = age,
                isMale = isMale
            )
            val dailyCaloriesTarget = calculateDailyCaloriesTarget(rmr, activityLevel, goalType, granularityValue)
            return dailyCaloriesTarget.toInt()
        }

        fun calculateRemainingCalories(
            dailyCalorieTarget: Int,
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType,
            advancedEnabled: Boolean = isAdvancedEnabled,
            calorieStrategy: CalorieStrategy = getCalorieStrategy()
        ): Int {
            return if (advancedEnabled) {
                when (goalType) {
                    GoalType.LOSE_WEIGHT -> {
                        val exerciseCaloriesEatenBack = (caloriesBurned * calorieStrategy.weightLossExercisePercentage).toInt()
                        dailyCalorieTarget - caloriesConsumed + exerciseCaloriesEatenBack
                    }
                    GoalType.GAIN_WEIGHT -> {
                        val exerciseCaloriesEatenBack = (caloriesBurned * calorieStrategy.weightGainExercisePercentage).toInt()
                        dailyCalorieTarget - caloriesConsumed + exerciseCaloriesEatenBack + calorieStrategy.weightGainAdditionalCalories
                    }
                }
            } else {
                dailyCalorieTarget - caloriesConsumed + caloriesBurned
            }
        }
        
        fun calculateRemainingCalories(
            dailyCalorieTarget: Int,
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType
        ): Int {
            return calculateRemainingCalories(
                dailyCalorieTarget, caloriesConsumed, caloriesBurned, goalType,
                isAdvancedEnabled, calorieStrategy
            )
        }

        fun calculateNetCalories(
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType,
            advancedEnabled: Boolean = isAdvancedEnabled,
            calorieStrategy: CalorieStrategy = getCalorieStrategy()
        ): Int {
            val rawNet = if (advancedEnabled) {
                when (goalType) {
                    GoalType.LOSE_WEIGHT -> {
                        val exerciseCaloriesEatenBack = (caloriesBurned * calorieStrategy.weightLossExercisePercentage).toInt()
                        caloriesConsumed - exerciseCaloriesEatenBack
                    }
                    GoalType.GAIN_WEIGHT -> {
                        val exerciseCaloriesEatenBack = (caloriesBurned * calorieStrategy.weightGainExercisePercentage).toInt()
                        caloriesConsumed - exerciseCaloriesEatenBack - calorieStrategy.weightGainAdditionalCalories
                    }
                }
            } else {
                caloriesConsumed - caloriesBurned
            }
            return maxOf(0, rawNet)
        }
        
        fun calculateNetCalories(
            caloriesConsumed: Int,
            caloriesBurned: Int,
            goalType: GoalType
        ): Int {
            return calculateNetCalories(
                caloriesConsumed, caloriesBurned, goalType,
                isAdvancedEnabled, calorieStrategy
            )
        }

        fun getCalorieAdjustmentExplanation(
            goalType: GoalType, 
            weight: Double, 
            height: Double, 
            age: Int, 
            isMale: Boolean, 
            activityLevel: ActivityLevel,
            granularityValue: Int = getGranularityValue(),
            strategy: CalorieStrategy = getCalorieStrategy(),
            advancedEnabled: Boolean = isAdvancedEnabled()
        ): String {
            val rmr = calculateRMR(weight, height, age, isMale)
            val activityFactor = activityLevel.multiplier
            
            return strategy.getExerciseCalorieExplanation(
                goalType, 
                rmr, 
                activityFactor, 
                granularityValue, 
                advancedEnabled
            )
        }
        
        fun getCalorieAdjustmentExplanation(
            goalType: GoalType, 
            weight: Double, 
            height: Double, 
            age: Int, 
            isMale: Boolean, 
            activityLevel: ActivityLevel
        ): String {
            return getCalorieAdjustmentExplanation(
                goalType, weight, height, age, isMale, activityLevel,
                granularityValue, calorieStrategy, isAdvancedEnabled
            )
        }

        fun getExerciseCaloriePercentage(goalType: GoalType): Double {
            return if (isAdvancedEnabled) {
                when (goalType) {
                    GoalType.LOSE_WEIGHT -> calorieStrategy.weightLossExercisePercentage
                    GoalType.GAIN_WEIGHT -> calorieStrategy.weightGainExercisePercentage
                }
            } else {
                1.0
            }
        }

        fun getCurrentStrategy(): CalorieStrategy {
            return calorieStrategy
        }
    }
} 