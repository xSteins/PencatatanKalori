package com.ralvin.pencatatankalori.model.formula

enum class GoalType {
	LOSE_WEIGHT,
	GAIN_WEIGHT;

	// for onboarding / userprofile settings pop up
	fun getDisplayName(): String {
		return when (this) {
			LOSE_WEIGHT -> "Reduce weight (Cutting)"
			GAIN_WEIGHT -> "Gain weight (Bulking)"
		}
	}

	fun getShortDisplayName(): String {
		return when (this) {
			LOSE_WEIGHT -> "Cutting"
			GAIN_WEIGHT -> "Bulking"
		}
	}

	fun getDescription(): String {
		return when (this) {
			LOSE_WEIGHT -> "Reduce weight by making calorie deficit"
			GAIN_WEIGHT -> "Adding weight by increasing calorie surplus"
		}
	}
}
