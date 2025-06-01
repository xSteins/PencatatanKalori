package com.ralvin.pencatatankalori.data.database.converter

import androidx.room.TypeConverter
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType

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
} 