package com.ralvin.pencatatankalori.di

import android.content.Context
import androidx.room.Room
import com.ralvin.pencatatankalori.data.database.AppDatabase
import com.ralvin.pencatatankalori.data.database.dao.ActivityLogDao
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

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pencatatan_kalori_database"
        ).fallbackToDestructiveMigration()
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
}

