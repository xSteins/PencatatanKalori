package com.ralvin.pencatatankalori.health.model

enum class CalorieStrategy(
    val displayName: String,
    val description: String,
    val weightLossExercisePercentage: Double,
    val weightGainExercisePercentage: Double,
    val weightGainAdditionalCalories: Int
) {
    CONSERVATIVE(
        displayName = "Conservative",
        description = "Gentle approach for gradual progress",
        weightLossExercisePercentage = 0.75,
        weightGainExercisePercentage = 0.80,
        weightGainAdditionalCalories = 0
    ),
    MODERATE(
        displayName = "Moderate",
        description = "Balanced approach for steady progress",
        weightLossExercisePercentage = 0.70,
        weightGainExercisePercentage = 1.0,
        weightGainAdditionalCalories = 150
    ),
    AGGRESSIVE(
        displayName = "Aggressive",
        description = "Faster results approach",
        weightLossExercisePercentage = 0.60,
        weightGainExercisePercentage = 1.0,
        weightGainAdditionalCalories = 200
    );

    companion object {
        fun getDefault(): CalorieStrategy = MODERATE
    }
    
    fun getExerciseCalorieExplanation(
        goalType: GoalType, 
        rmrValue: Double, 
        activityFactorValue: Double, 
        granularityValue: Int,
        isAdvancedEnabled: Boolean
    ): String {
        val tdeeFormula = "TDEE = RMR (${rmrValue.toInt()}) × Activity Factor (${activityFactorValue}) + Granularity Value ($granularityValue)"
        val finalTdee = (rmrValue * activityFactorValue + granularityValue).toInt()
        
        val rmrExplanation = "RMR calculated using Mifflin-St Jeor equation based on your personal data"
        val activityFactorExplanation = "Activity Factor based on your selected activity level"
        val tdeeExplanation = "Final TDEE: $finalTdee calories"
        
        return if (isAdvancedEnabled) {
            val advancedFormula = when (goalType) {
                GoalType.LOSE_WEIGHT -> {
                    val lossPercentage = ((1.0 - weightLossExercisePercentage) * 100).toInt()
                    "Advanced Settings: Eating back ${(weightLossExercisePercentage * 100).toInt()}% of exercise calories " +
                    "(creating ${lossPercentage}% additional deficit) for controlled fat loss while maintaining performance."
                }
                GoalType.GAIN_WEIGHT -> {
                    val eatBackPercentage = (weightGainExercisePercentage * 100).toInt()
                    if (weightGainAdditionalCalories > 0) {
                        "Advanced Settings: Eating back ${eatBackPercentage}% of exercise calories plus $weightGainAdditionalCalories additional calories " +
                        "for optimized muscle growth with minimal fat gain."
                    } else {
                        "Advanced Settings: Eating back ${eatBackPercentage}% of exercise calories for conservative muscle growth."
                    }
                }
            }
            "$rmrExplanation\n$activityFactorExplanation\n\n$tdeeFormula\n$tdeeExplanation\n\n$advancedFormula"
        } else {
            val defaultBehavior = "Default behavior: Subtracts consumption calories with burned calories in 1:1 ratio"
            "$rmrExplanation\n$activityFactorExplanation\n\n$tdeeFormula\n$tdeeExplanation\n\n$defaultBehavior"
        }
    }
}
