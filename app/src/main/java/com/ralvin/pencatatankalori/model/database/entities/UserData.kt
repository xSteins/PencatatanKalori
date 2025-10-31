package com.ralvin.pencatatankalori.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import java.util.UUID

@Entity(tableName = "user")
data class UserData(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "age") val age: Int,
	@ColumnInfo(name = "gender") val gender: String,
	@ColumnInfo(name = "weight") val weight: Float,
	@ColumnInfo(name = "height") val height: Float,
	@ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel,
	@ColumnInfo(name = "goal_type") val goalType: GoalType,
	@ColumnInfo(name = "daily_calorie_target") val dailyCalorieTarget: Int,
)