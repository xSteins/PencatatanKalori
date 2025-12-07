package com.ralvin.pencatatankalori.model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ActivityLogDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertActivity(activity: ActivityLog)

	@Update
	suspend fun updateActivity(activity: ActivityLog)

	@Query("DELETE FROM activity_log WHERE id = :id")
	suspend fun deleteActivityById(id: String)

	@Query("SELECT al.* FROM activity_log al INNER JOIN daily_data dd ON al.daily_data_id = dd.id WHERE dd.user_id = :userId ORDER BY al.timestamp DESC")
	fun getAllActivitiesByUserId(userId: String): Flow<List<ActivityLog>>

	@Query("SELECT al.* FROM activity_log al INNER JOIN daily_data dd ON al.daily_data_id = dd.id WHERE dd.user_id = :userId AND DATE(al.timestamp/1000, 'unixepoch') = DATE('now') ORDER BY al.timestamp DESC")
	fun getTodayActivities(userId: String): Flow<List<ActivityLog>>

	@Query("SELECT al.* FROM activity_log al INNER JOIN daily_data dd ON al.daily_data_id = dd.id WHERE dd.user_id = :userId AND DATE(al.timestamp/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch') ORDER BY al.timestamp DESC")
	suspend fun getActivitiesForDate(userId: String, date: Date): List<ActivityLog>

	@Query("SELECT COALESCE(SUM(al.calories), 0) FROM activity_log al INNER JOIN daily_data dd ON al.daily_data_id = dd.id WHERE dd.user_id = :userId AND al.type = :type AND DATE(al.timestamp/1000, 'unixepoch') = DATE('now')")
	suspend fun getTodayCaloriesByType(
		userId: String,
		type: ActivityType
	): Int

	@Query("SELECT al.* FROM activity_log al INNER JOIN daily_data dd ON al.daily_data_id = dd.id WHERE dd.user_id = :userId AND DATE(al.timestamp/1000, 'unixepoch') BETWEEN DATE(:startDate/1000, 'unixepoch') AND DATE(:endDate/1000, 'unixepoch') ORDER BY al.timestamp DESC")
	suspend fun getActivitiesForDateRange(
		userId: String,
		startDate: Date,
		endDate: Date
	): List<ActivityLog>
}
