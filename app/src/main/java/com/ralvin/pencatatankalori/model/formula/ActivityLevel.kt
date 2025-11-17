package com.ralvin.pencatatankalori.model.formula

enum class ActivityLevel(val multiplier: Double) {
	SEDENTARY(1.0),
	LIGHTLY_ACTIVE(1.4),
	MODERATELY_ACTIVE(1.6),
	VERY_ACTIVE(1.9);

	// untuk onboarding menu, internationalization ada di bagian xml tapi belum bisa untuk kelas enum
	// TODO: Implement internationalization string untuk enum ini
	fun getDisplayName(): String {
		return when (this) {
			SEDENTARY -> "Tidak Aktif"
			LIGHTLY_ACTIVE -> "Sedikit Aktif"
			MODERATELY_ACTIVE -> "Cukup Aktif"
			VERY_ACTIVE -> "Sangat Aktif"
		}
	}

	fun getDescription(): String {
		return when (this) {
			SEDENTARY -> "Tidak melakukan aktivitas / jarang keluar rumah"
			LIGHTLY_ACTIVE -> "Tingkat Aktivitas Normal (Pelajar/Pekerja)"
			MODERATELY_ACTIVE -> "Rutin berolahraga (1-2x per minggu)"
			VERY_ACTIVE -> "Olahraga intens (>3x per minggu)"
		}
	}
//	fun getDisplayName(): String {
//		return when (this) {
//			SEDENTARY -> "Sedentary"
//			LIGHTLY_ACTIVE -> "Lightly Active"
//			MODERATELY_ACTIVE -> "Moderately Active"
//			VERY_ACTIVE -> "Very Active"
//		}
//	}
//
//	fun getDescription(): String {
//		return when (this) {
//			SEDENTARY -> "Not doing anything / stay at home person"
//			LIGHTLY_ACTIVE -> "Normal Activity Level (Student/Worker)"
//			MODERATELY_ACTIVE -> "Actively exercise (1-2x per week)"
//			VERY_ACTIVE -> "Intense exercise (>3x per week)"
//		}
//	}
}
