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
        ActivityLog::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pencatatan_kalori_database"
                ).fallbackToDestructiveMigration() // TODO: FIX MIGRATION FOR PRODUCTION
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 