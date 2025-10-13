package com.ralvin.pencatatankalori.data.repository

import com.ralvin.pencatatankalori.data.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.data.database.dao.ActivityPicturesDao
import com.ralvin.pencatatankalori.data.database.dao.UserDataDao
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityPicture
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import com.ralvin.pencatatankalori.data.database.entities.UserData
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalorieRepository @Inject constructor(
    private val userDataDao: UserDataDao,
    private val activityLogDao: ActivityLogDao,
    private val activityPicturesDao: ActivityPicturesDao
) {
    
    private val _isDummyDataEnabled = MutableStateFlow(false)
    val isDummyDataEnabled: Flow<Boolean> = _isDummyDataEnabled
    
    
    fun getUserProfile(): Flow<UserData?> {
        return _isDummyDataEnabled.flatMapLatest { isDummy ->
            if (isDummy) {
                flowOf(createDummyUser())
            } else {
                userDataDao.getUserData()
            }
        }
    }
    
    suspend fun getUserProfileOnce(): UserData? {
        return if (_isDummyDataEnabled.value) {
            createDummyUser()
        } else {
            userDataDao.getUserData().first()
        }
    }
    
    suspend fun updateUserProfile(user: UserData) = userDataDao.updateUserData(user)
    
    suspend fun createUser(user: UserData) = userDataDao.insertUserData(user)
    
    suspend fun isUserCreated(): Boolean = userDataDao.getUserDataCount() > 0
    
    
    suspend fun logActivity(
        name: String,
        calories: Int,
        type: ActivityType,
        pictureId: String? = null,
        notes: String? = null
    ) {
        if (!_isDummyDataEnabled.value) {
            val user = getUserProfileOnce() ?: return
            val activityLog = ActivityLog(
                userId = user.id,
                type = type,
                timestamp = Date(),
                name = name,
                calories = calories,
                notes = notes,
                pictureId = pictureId
            )
            activityLogDao.insertActivity(activityLog)
        }
    }
    
    suspend fun updateActivity(activity: ActivityLog) {
        if (!_isDummyDataEnabled.value) {
            activityLogDao.updateActivity(activity)
        }
    }
    
    suspend fun deleteActivity(activityId: String) {
        if (!_isDummyDataEnabled.value) {
            activityLogDao.deleteActivityById(activityId)
        }
    }
    
    suspend fun savePicture(imagePath: String): String {
        if (_isDummyDataEnabled.value) {
            return "dummy_picture_id"
        }
        val picture = ActivityPicture(imagePath = imagePath)
        activityPicturesDao.insertPicture(picture)
        return picture.id
    }
    
    suspend fun getPicture(pictureId: String): ActivityPicture? {
        if (_isDummyDataEnabled.value || pictureId.endsWith("_placeholder")) {
            val imageName = pictureId.replace("_placeholder", "")
            return ActivityPicture(
                id = pictureId,
                imagePath = "android.resource://com.ralvin.pencatatankalori/assets/PlaceholderImage/$imageName.jpg"
            )
        }
        return activityPicturesDao.getPictureById(pictureId)
    }
    
    suspend fun deletePicture(pictureId: String) {
        if (!_isDummyDataEnabled.value) {
            activityPicturesDao.deletePictureById(pictureId)
        }
    }
    
    
    fun getTodayActivities(): Flow<List<ActivityLog>> {
        return _isDummyDataEnabled.flatMapLatest { isDummy ->
            if (isDummy) {
                val todayActivities = createDummyActivities().filter { activity ->
                    val calendar = Calendar.getInstance()
                    val today = calendar.time
                    calendar.time = activity.timestamp
                    val activityDate = calendar.time
                    
                    val todayCalendar = Calendar.getInstance().apply { time = today }
                    val activityCalendar = Calendar.getInstance().apply { time = activityDate }
                    
                    todayCalendar.get(Calendar.YEAR) == activityCalendar.get(Calendar.YEAR) &&
                    todayCalendar.get(Calendar.DAY_OF_YEAR) == activityCalendar.get(Calendar.DAY_OF_YEAR)
                }
                flowOf(todayActivities)
            } else {
                userDataDao.getUserData().flatMapLatest { user ->
                    user?.let { activityLogDao.getTodayActivities(it.id) } ?: flowOf(emptyList())
                }
            }
        }
    }
    
    suspend fun getTodayCaloriesConsumed(): Int {
        return if (_isDummyDataEnabled.value) {
            getActivitiesForDate(Date())
                .filter { it.type == ActivityType.CONSUMPTION }
                .sumOf { it.calories ?: 0 }
        } else {
            val user = getUserProfileOnce() ?: return 0
            activityLogDao.getTodayCaloriesConsumed(user.id, ActivityType.CONSUMPTION)
        }
    }
    
    suspend fun getTodayCaloriesBurned(): Int {
        return if (_isDummyDataEnabled.value) {
            getActivitiesForDate(Date())
                .filter { it.type == ActivityType.WORKOUT }
                .sumOf { it.calories ?: 0 }
        } else {
            val user = getUserProfileOnce() ?: return 0
            activityLogDao.getTodayCaloriesBurned(user.id, ActivityType.WORKOUT)
        }
    }
    
    
    // ========================
    // ========================
    
    suspend fun getActivitiesForDate(date: Date): List<ActivityLog> {
        return if (_isDummyDataEnabled.value) {
            createDummyActivities().filter { activity ->
                val activityCalendar = Calendar.getInstance().apply { time = activity.timestamp }
                val targetCalendar = Calendar.getInstance().apply { time = date }
                
                activityCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR) &&
                activityCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR)
            }
        } else {
            val user = getUserProfileOnce() ?: return emptyList()
            activityLogDao.getActivitiesForDate(user.id, date)
        }
    }
    
    suspend fun getActivitiesForPeriod(startDate: Date, endDate: Date): List<ActivityLog> {
        return if (_isDummyDataEnabled.value) {
            createDummyActivities().filter { activity ->
                activity.timestamp.time >= startDate.time && activity.timestamp.time <= endDate.time
            }
        } else {
            val user = getUserProfileOnce() ?: return emptyList()
            activityLogDao.getActivitiesForPeriod(user.id, startDate, endDate)
        }
    }
    
    
    fun getAllUserActivities(): Flow<List<ActivityLog>> {
        return _isDummyDataEnabled.flatMapLatest { isDummy ->
            if (isDummy) {
                flowOf(createDummyActivities())
            } else {
                userDataDao.getUserData().flatMapLatest { user ->
                    user?.let { activityLogDao.getActivitiesByUserId(it.id) } ?: flowOf(emptyList())
                }
            }
        }
    }
    
    // ========================
    // ========================
    
    fun toggleDummyData() {
        _isDummyDataEnabled.value = !_isDummyDataEnabled.value
    }
    
    private fun createDummyUser(): UserData {
        return UserData(
            id = "1000",
            name = "Demo User",
            age = 28,
            gender = "Male",
            weight = 70.0f,
            height = 175.0f,
            activityLevel = ActivityLevel.MODERATELY_ACTIVE,
            goalType = GoalType.LOSE_WEIGHT,
            dailyCalorieTarget = 2200
        )
    }
    
    private fun createDummyActivities(): List<ActivityLog> {
        val activities = mutableListOf<ActivityLog>()
        val calendar = Calendar.getInstance()
        
        for (dayOffset in 0..9) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val date = calendar.time
            
            val foodEntries = listOf(
                Triple("Oatmeal with Banana", 320, "1 bowl"),
                Triple("Grilled Chicken Salad", 450, "1 serving"),
                Triple("Rice Bowl with Vegetables", 380, "1 bowl"),
                Triple("Greek Yogurt with Berries", 180, "1 cup"),
                Triple("Whole Wheat Toast", 240, "2 slices"),
                Triple("Protein Smoothie", 290, "1 glass"),
                Triple("Pasta with Tomato Sauce", 420, "1 plate"),
                Triple("Apple with Peanut Butter", 190, "1 apple + 2 tbsp")
            )
            
            foodEntries.shuffled().take((2..3).random()).forEach { (name, calories, portion) ->
                calendar.time = date
                calendar.add(Calendar.HOUR_OF_DAY, (8..20).random())
                calendar.add(Calendar.MINUTE, (0..59).random())
                
                val foodImageId = "food${(1..3).random()}_placeholder"
                
                activities.add(ActivityLog(
                    userId = "1000",
                    type = ActivityType.CONSUMPTION,
                    timestamp = calendar.time,
                    name = name,
                    calories = calories,
                    notes = "Portion: $portion",
                    pictureId = foodImageId
                ))
            }
            
            val workoutEntries = listOf(
                Triple("Morning Run", 350, 30),
                Triple("Weight Training", 280, 45),
                Triple("Cycling", 400, 40),
                Triple("Swimming", 320, 25),
                Triple("Yoga", 150, 60),
                Triple("Walking", 200, 45),
                Triple("HIIT Workout", 380, 20),
                Triple("Basketball", 300, 35)
            )
            
            if ((0..2).random() > 0) { // 66% chance of having workout
                workoutEntries.shuffled().take(1).forEach { (name, calories, duration) ->
                    calendar.time = date
                    calendar.add(Calendar.HOUR_OF_DAY, (6..19).random())
                    calendar.add(Calendar.MINUTE, (0..59).random())
                    
                    val workoutImageId = "workout${(1..2).random()}_placeholder"
                    
                    activities.add(ActivityLog(
                        userId = "1000",
                        type = ActivityType.WORKOUT,
                        timestamp = calendar.time,
                        name = name,
                        calories = calories,
                        notes = "Duration: ${duration} minutes",
                        pictureId = workoutImageId
                    ))
                }
            }
        }
        
        return activities.sortedByDescending { it.timestamp }
    }
} 