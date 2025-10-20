package com.ralvin.pencatatankalori.Model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ralvin.pencatatankalori.Model.database.converter.Converters
import com.ralvin.pencatatankalori.Model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.Model.database.dao.ActivityPicturesDao
import com.ralvin.pencatatankalori.Model.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.Model.database.dao.UserDataDao
import com.ralvin.pencatatankalori.Model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.Model.database.entities.ActivityPicture
import com.ralvin.pencatatankalori.Model.database.entities.DailyData
import com.ralvin.pencatatankalori.Model.database.entities.UserData

@Database(
	entities = [
		UserData::class,
		ActivityLog::class,
		ActivityPicture::class,
		DailyData::class
	],
	version = 11,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDataDao(): UserDataDao
	abstract fun activityLogDao(): ActivityLogDao
	abstract fun activityPicturesDao(): ActivityPicturesDao
	abstract fun dailyDataDao(): DailyDataDao

} 