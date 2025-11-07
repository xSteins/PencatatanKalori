package com.ralvin.pencatatankalori.model.formula

enum class ActivityLevel(val multiplier: Double) {
	SEDENTARY(1.2),
	LIGHTLY_ACTIVE(1.375),
	MODERATELY_ACTIVE(1.55),
	VERY_ACTIVE(1.725);

	// untuk onboarding menu
	fun getDisplayName(): String {
		return when (this) {
			SEDENTARY -> "Sedentary"
			LIGHTLY_ACTIVE -> "Lightly Active"
			MODERATELY_ACTIVE -> "Moderately Active"
			VERY_ACTIVE -> "Very Active"
		}
	}

	fun getDescription(): String {
		return when (this) {
			SEDENTARY -> "Not doing anything / stay at home person"
			LIGHTLY_ACTIVE -> "Normal Activity Level (Student/Worker)"
			MODERATELY_ACTIVE -> "Actively exercise (1-2x per week)"
			VERY_ACTIVE -> "Intense exercise (>3x per week)"
		}
	}
}
