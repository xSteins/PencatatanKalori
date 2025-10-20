package com.ralvin.pencatatankalori.Model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
	tableName = "activity_log",
	foreignKeys = [
		ForeignKey(
			entity = DailyData::class,
			parentColumns = ["id"],
			childColumns = ["daily_data_id"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index(value = ["daily_data_id"])]
)
data class ActivityLog(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "user_id") val userId: String,
	@ColumnInfo(name = "daily_data_id") val dailyDataId: String,
	@ColumnInfo(name = "type") val type: ActivityType,
	@ColumnInfo(name = "timestamp") val timestamp: Date,
	@ColumnInfo(name = "name") val name: String? = null,
	@ColumnInfo(name = "calories") val calories: Int? = null,
	@ColumnInfo(name = "notes") val notes: String? = null,
	@ColumnInfo(name = "picture_id") val pictureId: String? = null
)