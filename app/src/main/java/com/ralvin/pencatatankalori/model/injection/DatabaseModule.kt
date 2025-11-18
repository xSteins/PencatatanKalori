package com.ralvin.pencatatankalori.model.injection

import android.content.Context
import androidx.room.Room
import com.ralvin.pencatatankalori.model.database.AppDatabase
import com.ralvin.pencatatankalori.model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.model.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.model.database.dao.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

//	private val MIGRATION_8_9 = object : Migration(8, 9) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			// Rename actual_weight column to weight in daily_data table
//			database.execSQL("ALTER TABLE daily_data RENAME COLUMN actual_weight TO weight")
//		}
//	}
//
//	private val MIGRATION_9_10 = object : Migration(9, 10) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			// Add advanced_enabled column to daily_data table with default value false
//			database.execSQL("ALTER TABLE daily_data ADD COLUMN advanced_enabled INTEGER NOT NULL DEFAULT 0")
//		}
//	}
//
//	private val MIGRATION_10_11 = object : Migration(10, 11) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			// Add height column to daily_data table
//			database.execSQL("ALTER TABLE daily_data ADD COLUMN height REAL")
//
//			// Add indices for foreign keys to improve performance
//			database.execSQL("CREATE INDEX IF NOT EXISTS index_activity_log_daily_data_id ON activity_log (daily_data_id)")
//			database.execSQL("CREATE INDEX IF NOT EXISTS index_daily_data_user_id ON daily_data (user_id)")
//		}
//	}
//
//	private val MIGRATION_11_12 = object : Migration(11, 12) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			database.execSQL("DROP TABLE IF EXISTS activity_picture")
//		}
//	}
//
//	private val MIGRATION_12_13 = object : Migration(12, 13) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			database.execSQL(
//				"""
//				CREATE TABLE activity_log_new (
//					calories INTEGER NOT NULL,
//					daily_data_id TEXT NOT NULL,
//					id TEXT NOT NULL PRIMARY KEY,
//					name TEXT NOT NULL,
//					notes TEXT,
//					picture_id TEXT,
//					timestamp INTEGER NOT NULL,
//					type TEXT NOT NULL,
//					user_id TEXT NOT NULL,
//					FOREIGN KEY(daily_data_id) REFERENCES daily_data(id) ON DELETE CASCADE
//				)
//				""".trimIndent()
//			)
//
//			database.execSQL(
//				"""
//				INSERT INTO activity_log_new (calories, daily_data_id, id, name, notes, picture_id, timestamp, type, user_id)
//				SELECT
//					CASE WHEN calories IS NULL THEN 0 ELSE calories END,
//					daily_data_id,
//					id,
//					CASE WHEN name IS NULL OR name = '' THEN 'Activity' ELSE name END,
//					notes,
//					picture_id,
//					timestamp,
//					type,
//					user_id
//				FROM activity_log
//				WHERE id IS NOT NULL
//				""".trimIndent()
//			)
//
//			database.execSQL("DROP TABLE activity_log")
//
//			database.execSQL("ALTER TABLE activity_log_new RENAME TO activity_log")
//
//			// Create index AFTER table is renamed to final name
//			database.execSQL(
//				"CREATE INDEX IF NOT EXISTS index_activity_log_daily_data_id ON activity_log (daily_data_id)"
//			)
//		}
//	}
//
//	private val MIGRATION_13_14 = object : Migration(13, 14) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			// This migration ensures the index exists after the 12->13 migration fix
//			database.execSQL(
//				"CREATE INDEX IF NOT EXISTS index_activity_log_daily_data_id ON activity_log (daily_data_id)"
//			)
//		}
//	}
//
//	private val MIGRATION_14_15 = object : Migration(14, 15) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			database.execSQL("ALTER TABLE user DROP COLUMN name")
//		}
//	}
//
//	private val MIGRATION_15_16 = object : Migration(14, 15) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			database.execSQL("ALTER TABLE DailyData DROP COLUMN advanced_enabled")
//			database.execSQL("ALTER TABLE DailyData DROP COLUMN calorie_strategy")
//		}
//	}

//	private val MIGRATION_16_17 = object : Migration(16, 17) {
//		override fun migrate(database: SupportSQLiteDatabase) {
//			// Add activity_level column to daily_data table
//			database.execSQL("ALTER TABLE daily_data ADD COLUMN activity_level TEXT")
//		}
//	}

	@Provides
	@Singleton
	fun provideAppDatabase(
		@ApplicationContext context: Context
	): AppDatabase {
		return Room.databaseBuilder(
			context.applicationContext,
			AppDatabase::class.java,
			"pencatatan_kalori"
		)
			.fallbackToDestructiveMigration(dropAllTables = true)
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
	fun provideDailyDataDao(database: AppDatabase): DailyDataDao {
		return database.dailyDataDao()
	}
}

