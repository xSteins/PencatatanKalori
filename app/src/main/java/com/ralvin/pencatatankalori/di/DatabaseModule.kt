package com.ralvin.pencatatankalori.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ralvin.pencatatankalori.data.database.AppDatabase
import com.ralvin.pencatatankalori.data.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.data.database.dao.ActivityPicturesDao
import com.ralvin.pencatatankalori.data.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.data.database.dao.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Rename actual_weight column to weight in daily_data table
            database.execSQL("ALTER TABLE daily_data RENAME COLUMN actual_weight TO weight")
        }
    }

    private val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add advanced_enabled column to daily_data table with default value false
            database.execSQL("ALTER TABLE daily_data ADD COLUMN advanced_enabled INTEGER NOT NULL DEFAULT 0")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pencatatan_kalori_database"
        )
        .addMigrations(MIGRATION_8_9, MIGRATION_9_10)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUserDataDao(database: AppDatabase): UserDataDao {
        return database.userDataDao()
    }

    @Provides
    fun provideActivityLogDao(database: AppDatabase): ActivityLogDao {
        return database.activityLogDao()
    }

    @Provides
    fun provideActivityPicturesDao(database: AppDatabase): ActivityPicturesDao {
        return database.activityPicturesDao()
    }
    
    @Provides
    fun provideDailyDataDao(database: AppDatabase): DailyDataDao {
        return database.dailyDataDao()
    }
}

