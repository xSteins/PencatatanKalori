package com.ralvin.pencatatankalori.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.ralvin.pencatatankalori.data.database.entities.DailyData
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailyDataDao {
    @Query("SELECT * FROM daily_data WHERE user_id = :userId ORDER BY date DESC")
    fun getDailyDataByUserId(userId: String): Flow<List<DailyData>>
    
    @Query("SELECT * FROM daily_data WHERE user_id = :userId AND DATE(date/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch')")
    suspend fun getDailyDataForDate(userId: String, date: Date): DailyData?
    
    @Query("SELECT * FROM daily_data WHERE user_id = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    suspend fun getDailyDataForDateRange(userId: String, startDate: Date, endDate: Date): List<DailyData>
    
    @Query("SELECT * FROM daily_data WHERE user_id = :userId AND DATE(date/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodayDailyData(userId: String): DailyData?
    
    @Query("SELECT * FROM daily_data WHERE user_id = :userId AND DATE(date/1000, 'unixepoch') = DATE('now')")
    fun getTodayDailyDataFlow(userId: String): Flow<DailyData?>
    
    @Query("SELECT * FROM daily_data WHERE id = :id")
    suspend fun getDailyDataById(id: String): DailyData?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyData(dailyData: DailyData)
    
    @Update
    suspend fun updateDailyData(dailyData: DailyData)
    
    @Delete
    suspend fun deleteDailyData(dailyData: DailyData)
    
    @Query("DELETE FROM daily_data WHERE user_id = :userId")
    suspend fun deleteDailyDataByUserId(userId: String)
    
    @Query("UPDATE daily_data SET total_calories_consumption = :totalCalories WHERE id = :dailyDataId")
    suspend fun updateTotalCaloriesConsumption(dailyDataId: String, totalCalories: Int)
    
    @Query("UPDATE daily_data SET weight = :weight WHERE id = :dailyDataId")
    suspend fun updateWeight(dailyDataId: String, weight: Float)
}