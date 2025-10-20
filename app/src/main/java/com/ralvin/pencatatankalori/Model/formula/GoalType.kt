package com.ralvin.pencatatankalori.Model.formula

enum class GoalType {
	LOSE_WEIGHT,
	GAIN_WEIGHT;

	// for onboarding / userprofile settings pop up
	fun getDisplayName(): String {
		return when (this) {
			LOSE_WEIGHT -> "Menurunkan berat badan (Cutting)"
			GAIN_WEIGHT -> "Meningkatkan berat badan (Bulking)"
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
			LOSE_WEIGHT -> "Mengurangi berat badan dengan menciptakan defisit kalori"
			GAIN_WEIGHT -> "Meningkatkan berat badan dengan surplus kalori"
		}
	}
}
