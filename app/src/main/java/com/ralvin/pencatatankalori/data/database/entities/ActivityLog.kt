package com.ralvin.pencatatankalori.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "activity_log",
    foreignKeys = [
        ForeignKey(
            entity = UserData::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ActivityLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "type") val type: ActivityType,
    @ColumnInfo(name = "timestamp") val timestamp: Date,
    // used in both, food and workout
    @ColumnInfo(name = "calories") val calories: Int? = null,
    
    // food specific column
    @ColumnInfo(name = "food_name") val foodName: String? = null,
    @ColumnInfo(name = "protein") val protein: Float? = null,
    @ColumnInfo(name = "carbs") val carbs: Float? = null,
    @ColumnInfo(name = "portion") val portion: String? = null,
    
    // workout specific column
    @ColumnInfo(name = "workout_name") val workoutName: String? = null,
    @ColumnInfo(name = "duration") val duration: Int? = null,
    
    // picture reference
    @ColumnInfo(name = "picture_id") val pictureId: String? = null
) 