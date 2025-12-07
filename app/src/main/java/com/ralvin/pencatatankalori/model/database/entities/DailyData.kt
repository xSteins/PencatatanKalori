package com.ralvin.pencatatankalori.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import java.util.Date
import java.util.UUID

@Entity(
	tableName = "daily_data",
	foreignKeys = [
		ForeignKey(
			entity = UserData::class,
			parentColumns = ["id"],
			childColumns = ["user_id"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index(value = ["user_id"])]
)
data class DailyData(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "user_id") val userId: String,
	@ColumnInfo(name = "date") val date: Date,
	@ColumnInfo(name = "tdee") val tdee: Int,
	@ColumnInfo(name = "granularity_value") val granularityValue: Int,
	@ColumnInfo(name = "total_calories_consumption") val totalCaloriesConsumption: Int = 0,
	@ColumnInfo(name = "goal_type") val goalType: GoalType,
	@ColumnInfo(name = "weight") val weight: Float? = null,
	@ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel? = null
)
