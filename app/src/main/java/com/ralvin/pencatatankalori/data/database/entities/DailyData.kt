package com.ralvin.pencatatankalori.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ralvin.pencatatankalori.health.model.CalorieStrategy
import com.ralvin.pencatatankalori.health.model.GoalType
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
    ]
)
data class DailyData(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "tdee") val tdee: Int,
    @ColumnInfo(name = "granularity_value") val granularityValue: Int,
    @ColumnInfo(name = "calorie_strategy") val calorieStrategy: CalorieStrategy,
    @ColumnInfo(name = "advanced_enabled") val advancedEnabled: Boolean = false,
    @ColumnInfo(name = "total_calories_consumption") val totalCaloriesConsumption: Int = 0,
    @ColumnInfo(name = "goal_type") val goalType: com.ralvin.pencatatankalori.health.model.GoalType,
    @ColumnInfo(name = "weight") val weight: Float? = null
)