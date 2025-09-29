package com.ralvin.pencatatankalori.health.model

enum class CalorieStrategy(
    val granularityValue: Int,
    val displayName: String,
    val description: String,
    val weightLossExercisePercentage: Double,
    val weightGainExercisePercentage: Double,
    val weightGainAdditionalCalories: Int
) {
    CONSERVATIVE(
        granularityValue = 0,
        displayName = "Conservative",
        description = "Gentle approach with minimal base adjustment",
        weightLossExercisePercentage = 0.75, // Eat back 75% (lose 25%)
        weightGainExercisePercentage = 0.80, // Eat back 80%
        weightGainAdditionalCalories = 0
    ),
    MODERATE(
        granularityValue = 250,
        displayName = "Moderate",
        description = "Balanced approach for steady progress",
        weightLossExercisePercentage = 0.70, // Eat back 70% (lose 30%)
        weightGainExercisePercentage = 1.0,  // Eat back 100%
        weightGainAdditionalCalories = 150
    ),
    AGGRESSIVE(
        granularityValue = 500,
        displayName = "Aggressive",
        description = "Faster results with larger base adjustment",
        weightLossExercisePercentage = 0.60, // Eat back 60% (lose 40%)
        weightGainExercisePercentage = 1.0,  // Eat back 100%
        weightGainAdditionalCalories = 200
    );

    companion object {
        fun fromGranularityValue(value: Int): CalorieStrategy {
            return values().find { it.granularityValue == value } ?: MODERATE
        }
        
        fun getBaseCalorieAdjustmentTooltip(): String {
            return "Base calorie adjustment creates the foundation deficit/surplus:\n" +
                   "• Conservative (0 cal): Relies mainly on exercise calorie management\n" +
                   "• Moderate (250 cal): Balanced approach for steady progress\n" +
                   "• Aggressive (500 cal): Larger base adjustment for faster results"
        }
    }
    
    fun getExerciseCalorieExplanation(goalType: GoalType): String {
        return when (goalType) {
            GoalType.LOSE_WEIGHT -> {
                val lossPercentage = ((1.0 - weightLossExercisePercentage) * 100).toInt()
                "Eating back ${(weightLossExercisePercentage * 100).toInt()}% of exercise calories " +
                "(creating ${lossPercentage}% additional deficit) helps maintain performance while ensuring consistent fat loss."
            }
            GoalType.GAIN_WEIGHT -> {
                val eatBackPercentage = (weightGainExercisePercentage * 100).toInt()
                if (weightGainAdditionalCalories > 0) {
                    "Eating back ${eatBackPercentage}% of exercise calories plus $weightGainAdditionalCalories additional calories " +
                    "creates a moderate surplus for muscle growth while minimizing fat gain."
                } else {
                    "Eating back ${eatBackPercentage}% of exercise calories provides a conservative surplus " +
                    "focused on gradual, lean muscle growth."
                }
            }
        }
    }
}
