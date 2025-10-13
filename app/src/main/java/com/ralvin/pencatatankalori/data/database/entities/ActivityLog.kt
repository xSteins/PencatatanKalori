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
    // normalized
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "calories") val calories: Int? = null,
    @ColumnInfo(name = "notes") val notes: String? = null,
    
    // picture reference
    @ColumnInfo(name = "picture_id") val pictureId: String? = null

    // TODO: Add FK to daily data, make daily entity, user id move to daily
) 