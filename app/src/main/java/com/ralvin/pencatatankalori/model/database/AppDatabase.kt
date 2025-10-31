package com.ralvin.pencatatankalori.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ralvin.pencatatankalori.model.database.converter.Converters
import com.ralvin.pencatatankalori.model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.model.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.model.database.dao.UserDataDao
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.DailyData
import com.ralvin.pencatatankalori.model.database.entities.UserData

@Database(
	entities = [
		UserData::class,
		ActivityLog::class,
		DailyData::class
	],
	version = 14,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDataDao(): UserDataDao
	abstract fun activityLogDao(): ActivityLogDao
	abstract fun dailyDataDao(): DailyDataDao

}
