package com.ralvin.pencatatankalori.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ralvin.pencatatankalori.data.database.converter.Converters
import com.ralvin.pencatatankalori.data.database.dao.*
import com.ralvin.pencatatankalori.data.database.entities.*

@Database(
    entities = [
        UserData::class,
        ActivityLog::class,
        ActivityPicture::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun activityPicturesDao(): ActivityPicturesDao

} 