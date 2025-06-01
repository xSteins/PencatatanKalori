package com.ralvin.pencatatankalori.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType

@Entity
data class UserData(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "height") val height: Double,
    @ColumnInfo(name = "is_male") val isMale: Boolean,
    @ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel,
    @ColumnInfo(name = "goal_type") val goalType: GoalType,
    @ColumnInfo(name = "rmr") val rmr: Double,
    @ColumnInfo(name = "daily_calories_target") val dailyCaloriesTarget: Double,
)