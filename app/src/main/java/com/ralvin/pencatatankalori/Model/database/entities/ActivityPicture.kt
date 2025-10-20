package com.ralvin.pencatatankalori.Model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "activity_picture")
data class ActivityPicture(
	@PrimaryKey val id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "image_path") val imagePath: String
)
