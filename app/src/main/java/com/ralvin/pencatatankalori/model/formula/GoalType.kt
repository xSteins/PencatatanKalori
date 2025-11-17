package com.ralvin.pencatatankalori.model.formula

enum class GoalType {
	LOSE_WEIGHT,
	GAIN_WEIGHT;

	// for onboarding / userprofile settings pop up
	fun getDisplayName(): String {
		return when (this) {
			LOSE_WEIGHT -> "Menurunkan Berat Badan (Cutting)"
			GAIN_WEIGHT -> "Meningkatkan Berat Badan (Bulking)"
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
			LOSE_WEIGHT -> "Mengurangi Berat Badan dengan defisit kalori"
			GAIN_WEIGHT -> "Meningkatkan Berat Badan dengan defisit kalori"
		}
	}
}
