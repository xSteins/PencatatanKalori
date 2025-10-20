package com.ralvin.pencatatankalori.Model.database.converter

import androidx.room.TypeConverter
import com.ralvin.pencatatankalori.Model.database.entities.ActivityType
import com.ralvin.pencatatankalori.Model.formula.ActivityLevel
import com.ralvin.pencatatankalori.Model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.Model.formula.GoalType
import java.util.Date

class Converters {
	@TypeConverter
	fun fromActivityLevel(value: ActivityLevel): String {
		return value.name
	}

	@TypeConverter
	fun toActivityLevel(value: String): ActivityLevel {
		return ActivityLevel.valueOf(value)
	}

	@TypeConverter
	fun fromGoalType(value: GoalType): String {
		return value.name
	}

	@TypeConverter
	fun toGoalType(value: String): GoalType {
		return GoalType.valueOf(value)
	}

	@TypeConverter
	fun fromDate(value: Date?): Long? {
		return value?.time
	}

	@TypeConverter
	fun toDate(value: Long?): Date? {
		return value?.let { Date(it) }
	}

	@TypeConverter
	fun fromActivityType(value: ActivityType): String {
		return value.name
	}

	@TypeConverter
	fun toActivityType(value: String): ActivityType {
		return ActivityType.valueOf(value)
	}

	@TypeConverter
	fun fromCalorieStrategy(value: CalorieStrategy): String {
		return value.name
	}

	@TypeConverter
	fun toCalorieStrategy(value: String): CalorieStrategy {
		return CalorieStrategy.valueOf(value)
	}
}