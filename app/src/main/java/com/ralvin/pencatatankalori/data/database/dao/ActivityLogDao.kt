package com.ralvin.pencatatankalori.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ActivityLogDao {
    // get all activities
    @Query("SELECT * FROM activity_log WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getActivitiesByUserId(userId: String): Flow<List<ActivityLog>>
    
    // get activities for specific date (datetimepicker history)
    @Query("SELECT * FROM activity_log WHERE user_id = :userId AND timestamp >= strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))*1000 AND timestamp < (strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))+86400)*1000 ORDER BY timestamp DESC")
    suspend fun getActivitiesForDate(userId: String, date: Date): List<ActivityLog>
    
    // get today activities (overview data)
    @Query("SELECT * FROM activity_log WHERE user_id = :userId AND timestamp >= strftime('%s','now','localtime','start of day')*1000 AND timestamp < (strftime('%s','now','localtime','start of day')+86400)*1000 ORDER BY timestamp DESC")
    fun getTodayActivities(userId: String): Flow<List<ActivityLog>>
    
    // get activities by type
    @Query("SELECT * FROM activity_log WHERE user_id = :userId AND type = :type ORDER BY timestamp DESC")
    fun getActivitiesByType(userId: String, type: ActivityType): Flow<List<ActivityLog>>
    
    // get activities in range (history)
    @Query("SELECT * FROM activity_log WHERE user_id = :userId AND timestamp >= strftime('%s', datetime(:startDate/1000, 'unixepoch','localtime','start of day'))*1000 AND timestamp < (strftime('%s', datetime(:endDate/1000, 'unixepoch','localtime','start of day'))+86400)*1000 ORDER BY timestamp DESC")
    suspend fun getActivitiesForPeriod(userId: String, startDate: Date, endDate: Date): List<ActivityLog>
    
    // get today calories consumption
    @Query("SELECT COALESCE(SUM(calories), 0) FROM activity_log WHERE user_id = :userId AND type = :type AND timestamp >= strftime('%s','now','localtime','start of day')*1000 AND timestamp < (strftime('%s','now','localtime','start of day')+86400)*1000")
    suspend fun getTodayCaloriesConsumed(userId: String, type: ActivityType = ActivityType.CONSUMPTION): Int
    
    // get today calories burned
    @Query("SELECT COALESCE(SUM(calories), 0) FROM activity_log WHERE user_id = :userId AND type = :type AND timestamp >= strftime('%s','now','localtime','start of day')*1000 AND timestamp < (strftime('%s','now','localtime','start of day')+86400)*1000")
    suspend fun getTodayCaloriesBurned(userId: String, type: ActivityType = ActivityType.WORKOUT): Int
    
    @Query("SELECT COALESCE(SUM(calories), 0) FROM activity_log WHERE user_id = :userId AND type = :type AND timestamp >= strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))*1000 AND timestamp < (strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))+86400)*1000")
    suspend fun getCaloriesConsumedForDate(userId: String, date: Date, type: ActivityType = ActivityType.CONSUMPTION): Int

    @Query("SELECT COALESCE(SUM(calories), 0) FROM activity_log WHERE user_id = :userId AND type = :type AND timestamp >= strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))*1000 AND timestamp < (strftime('%s', datetime(:date/1000, 'unixepoch','localtime','start of day'))+86400)*1000")
    suspend fun getCaloriesBurnedForDate(userId: String, date: Date, type: ActivityType = ActivityType.WORKOUT): Int
    
    @Query("SELECT * FROM activity_log WHERE id = :id")
    suspend fun getActivityById(id: String): ActivityLog?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLog)
    
    @Update
    suspend fun updateActivity(activity: ActivityLog)
    
    @Delete
    suspend fun deleteActivity(activity: ActivityLog)
    
    @Query("DELETE FROM activity_log WHERE id = :id")
    suspend fun deleteActivityById(id: String)
    
    @Query("DELETE FROM activity_log WHERE user_id = :userId")
    suspend fun deleteActivitiesByUserId(userId: String)
} 