package com.ralvin.pencatatankalori.model.formula

enum class ActivityLevel(val multiplier: Double) {
	SEDENTARY(1.2),
	LIGHTLY_ACTIVE(1.375),
	MODERATELY_ACTIVE(1.55),
	VERY_ACTIVE(1.725);

	// untuk onboarding menu
	fun getDisplayName(): String {
		return when (this) {
			SEDENTARY -> "Tidak Aktif"
			LIGHTLY_ACTIVE -> "Cukup Aktif"
			MODERATELY_ACTIVE -> "Lumayan Aktif"
			VERY_ACTIVE -> "Sangat Aktif"
		}
	}

	fun getDescription(): String {
		return when (this) {
			SEDENTARY -> "Minim aktivitas bergerak"
			LIGHTLY_ACTIVE -> "Aktivitas normal (Pelajar / Pekerja)"
			MODERATELY_ACTIVE -> "Rutin olahraga teratur (1-2x per minggu)"
			VERY_ACTIVE -> "Sangat aktif olahraga (>3x per minggu)"
		}
	}
}