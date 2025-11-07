package com.ralvin.pencatatankalori.model.repository

import com.ralvin.pencatatankalori.model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.model.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.model.database.dao.UserDataDao
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.database.entities.DailyData
import com.ralvin.pencatatankalori.model.database.entities.UserData
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.CalorieStrategy
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalorieRepository @Inject constructor(
	private val userDataDao: UserDataDao,
	private val activityLogDao: ActivityLogDao,
	private val dailyDataDao: DailyDataDao
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
		if (_isDummyDataEnabled.value) return

		val dailyData = getOrCreateTodayDailyData() ?: return
		insertActivityForDailyData(
			dailyData = dailyData,
			timestamp = Date(),
			name = name,
			calories = calories,
			type = type,
			pictureId = pictureId,
			notes = notes
		)
	}

	suspend fun logActivityForDate(
		date: Date,
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		if (_isDummyDataEnabled.value) return

		val dailyData = getOrCreateDailyDataForDate(date) ?: return
		val alignedTimestamp = alignTimestampWithDate(date)
		insertActivityForDailyData(
			dailyData = dailyData,
			timestamp = alignedTimestamp,
			name = name,
			calories = calories,
			type = type,
			pictureId = pictureId,
			notes = notes
		)
	}

	suspend fun logActivityForDailyData(
		dailyDataId: String,
		targetDate: Date,
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		if (_isDummyDataEnabled.value) return

		val dailyData = dailyDataDao.getDailyDataById(dailyDataId) ?: return
		val alignedTimestamp = alignTimestampWithDate(targetDate)
		insertActivityForDailyData(
			dailyData = dailyData,
			timestamp = alignedTimestamp,
			name = name,
			calories = calories,
			type = type,
			pictureId = pictureId,
			notes = notes
		)
	}

	private suspend fun insertActivityForDailyData(
		dailyData: DailyData,
		timestamp: Date,
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String?,
		notes: String?
	) {
		val activityLog = ActivityLog(
			userId = dailyData.userId,
			dailyDataId = dailyData.id,
			type = type,
			timestamp = timestamp,
			name = name,
			calories = calories,
			notes = notes,
			pictureId = pictureId
		)
		activityLogDao.insertActivity(activityLog)

		if (type == ActivityType.CONSUMPTION) {
			val currentDailyData = dailyDataDao.getDailyDataById(dailyData.id) ?: dailyData
			val newTotal = currentDailyData.totalCaloriesConsumption + calories
			dailyDataDao.updateTotalCaloriesConsumption(dailyData.id, newTotal)
		}
	}

	private fun alignTimestampWithDate(targetDate: Date): Date {
		val targetCalendar = Calendar.getInstance().apply { time = targetDate }
		val nowCalendar = Calendar.getInstance()

		targetCalendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
		targetCalendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE))
		targetCalendar.set(Calendar.SECOND, nowCalendar.get(Calendar.SECOND))
		targetCalendar.set(Calendar.MILLISECOND, nowCalendar.get(Calendar.MILLISECOND))

		return targetCalendar.time
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
		return imagePath
	}

	suspend fun getPicture(pictureId: String): String? {
		if (_isDummyDataEnabled.value || pictureId.endsWith("_placeholder")) {
			val imageName = pictureId.replace("_placeholder", "")
			return "android.resource://com.ralvin.pencatatankalori/assets/PlaceholderImage/$imageName.jpg"
		}
		return pictureId
	}

	suspend fun deletePicture(pictureId: String) {
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
					user?.let { activityLogDao.getTodayActivitiesNew(it.id) } ?: flowOf(emptyList())
				}
			}
		}
	}

	fun getTodayDailyDataFlow(): Flow<DailyData?> {
		return _isDummyDataEnabled.flatMapLatest { isDummy ->
			if (isDummy) {
				flowOf(null) // Return null for dummy data
			} else {
				userDataDao.getUserData().flatMapLatest { user ->
					user?.let { dailyDataDao.getTodayDailyDataFlow(it.id) } ?: flowOf(null)
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
			activityLogDao.getTodayCaloriesConsumedNew(user.id, ActivityType.CONSUMPTION)
		}
	}

	suspend fun getTodayCaloriesBurned(): Int {
		return if (_isDummyDataEnabled.value) {
			getActivitiesForDate(Date())
				.filter { it.type == ActivityType.WORKOUT }
				.sumOf { it.calories ?: 0 }
		} else {
			val user = getUserProfileOnce() ?: return 0
			activityLogDao.getTodayCaloriesBurnedNew(user.id, ActivityType.WORKOUT)
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
			activityLogDao.getActivitiesForDateNew(user.id, date)
		}
	}

	suspend fun getActivitiesForPeriod(startDate: Date, endDate: Date): List<ActivityLog> {
		return if (_isDummyDataEnabled.value) {
			createDummyActivities().filter { activity ->
				activity.timestamp.time >= startDate.time && activity.timestamp.time <= endDate.time
			}
		} else {
			val user = getUserProfileOnce() ?: return emptyList()
			activityLogDao.getActivitiesForPeriodNew(user.id, startDate, endDate)
		}
	}

	suspend fun getOrCreateTodayDailyData(): DailyData? {
		if (_isDummyDataEnabled.value) return null

		val user = getUserProfileOnce() ?: return null
		val existingData = dailyDataDao.getTodayDailyData(user.id)

		return if (existingData != null) {
			existingData
		} else {
			val granularityValue = MifflinModel.getGranularityValue()
			val tdee = MifflinModel.calculateDailyCalories(
				user.weight, user.height, user.age,
				user.gender == "Male", user.activityLevel, user.goalType, granularityValue
			)
			val newDailyData = DailyData(
				userId = user.id,
				date = Date(),
				tdee = tdee,
				granularityValue = granularityValue,
				calorieStrategy = MifflinModel.getCalorieStrategy(),
				advancedEnabled = MifflinModel.isAdvancedEnabled(),
				totalCaloriesConsumption = 0,
				goalType = user.goalType,
				weight = user.weight,
				height = user.height
			)
			dailyDataDao.insertDailyData(newDailyData)
			newDailyData
		}
	}

	suspend fun getOrCreateDailyDataForDate(date: Date): DailyData? {
		if (_isDummyDataEnabled.value) return null

		val user = getUserProfileOnce() ?: return null
		val existingData = dailyDataDao.getDailyDataForDate(user.id, date)

		return if (existingData != null) {
			existingData
		} else {
			val granularityValue = MifflinModel.getGranularityValue()
			val tdee = MifflinModel.calculateDailyCalories(
				user.weight, user.height, user.age,
				user.gender == "Male", user.activityLevel, user.goalType, granularityValue
			)
			val newDailyData = DailyData(
				userId = user.id,
				date = date,
				tdee = tdee,
				granularityValue = granularityValue,
				calorieStrategy = MifflinModel.getCalorieStrategy(),
				advancedEnabled = MifflinModel.isAdvancedEnabled(),
				totalCaloriesConsumption = 0,
				goalType = user.goalType,
				weight = user.weight,
				height = user.height
			)
			dailyDataDao.insertDailyData(newDailyData)
			newDailyData
		}
	}

	suspend fun updateCalorieSettings(
		granularityValue: Int,
		strategy: CalorieStrategy?,
		advancedEnabled: Boolean
	) {
		if (_isDummyDataEnabled.value) return

		val user = getUserProfileOnce() ?: return
		val effectiveStrategy = strategy ?: CalorieStrategy.MODERATE

		val newTdee = MifflinModel.calculateDailyCalories(
			user.weight, user.height, user.age,
			user.gender == "Male", user.activityLevel, user.goalType, granularityValue
		)

		val existingData = dailyDataDao.getTodayDailyData(user.id)
		val updatedData = if (existingData != null) {
			existingData.copy(
				tdee = newTdee,
				granularityValue = granularityValue,
				calorieStrategy = effectiveStrategy,
				advancedEnabled = advancedEnabled,
				goalType = user.goalType,
				weight = user.weight,
				height = user.height
			)
		} else {
			DailyData(
				userId = user.id,
				date = Date(),
				tdee = newTdee,
				granularityValue = granularityValue,
				calorieStrategy = effectiveStrategy,
				advancedEnabled = advancedEnabled,
				totalCaloriesConsumption = 0,
				goalType = user.goalType,
				weight = user.weight,
				height = user.height
			)
		}

		if (existingData != null) {
			dailyDataDao.updateDailyData(updatedData)
		} else {
			dailyDataDao.insertDailyData(updatedData)
		}

		val updatedUser = user.copy(dailyCalorieTarget = newTdee)
		updateUserProfile(updatedUser)

		MifflinModel.adjustTargetCalorie(granularityValue)
		MifflinModel.setAdvancedEnabled(advancedEnabled)
		MifflinModel.setCalorieStrategy(effectiveStrategy)
	}

	suspend fun loadCalorieSettingsFromDatabase() {
		if (_isDummyDataEnabled.value) return

		val user = getUserProfileOnce() ?: return
		val todayData = dailyDataDao.getTodayDailyData(user.id) ?: return

		// Load settings from database into MifflinModel static variables
		MifflinModel.adjustTargetCalorie(todayData.granularityValue)
		MifflinModel.setCalorieStrategy(todayData.calorieStrategy)
		MifflinModel.setAdvancedEnabled(todayData.advancedEnabled)
	}

	suspend fun updateUserDataAndTodayTdee(userData: UserData) {
		updateUserProfile(userData)

		if (!_isDummyDataEnabled.value) {
			val todayData = dailyDataDao.getTodayDailyData(userData.id)
			if (todayData != null) {
				val newTdee = MifflinModel.calculateDailyCalories(
					userData.weight,
					userData.height,
					userData.age,
					userData.gender == "Male",
					userData.activityLevel,
					userData.goalType,
					todayData.granularityValue
				)
				val updatedData = todayData.copy(
					tdee = newTdee,
					goalType = userData.goalType,
					weight = userData.weight,
					height = userData.height
				)
				dailyDataDao.updateDailyData(updatedData)
			}
		}
	}


	fun getAllUserActivities(): Flow<List<ActivityLog>> {
		return _isDummyDataEnabled.flatMapLatest { isDummy ->
			if (isDummy) {
				flowOf(createDummyActivities())
			} else {
				userDataDao.getUserData().flatMapLatest { user ->
					user?.let { activityLogDao.getActivitiesByUserIdNew(it.id) }
						?: flowOf(emptyList())
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

				activities.add(
					ActivityLog(
						userId = "1000",
						dailyDataId = "dummy_daily_data_id",
						type = ActivityType.CONSUMPTION,
						timestamp = calendar.time,
						name = name,
						calories = calories,
						notes = "Portion: $portion",
						pictureId = foodImageId
					)
				)
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

					activities.add(
						ActivityLog(
							userId = "1000",
							dailyDataId = "dummy_daily_data_id",
							type = ActivityType.WORKOUT,
							timestamp = calendar.time,
							name = name,
							calories = calories,
							notes = "Duration: ${duration} minutes",
							pictureId = workoutImageId
						)
					)
				}
			}
		}

		return activities.sortedByDescending { it.timestamp }
	}

	suspend fun getDailyDataByUserId(userId: String): List<DailyData> {
		return if (_isDummyDataEnabled.value) {
			emptyList()
		} else {
			dailyDataDao.getDailyDataByUserId(userId).first()
		}
	}

	suspend fun getDailyDataForDateRange(startDate: Date, endDate: Date): List<DailyData> {
		return if (_isDummyDataEnabled.value) {
			emptyList()
		} else {
			val user = getUserProfileOnce() ?: return emptyList()
			dailyDataDao.getDailyDataForDateRange(user.id, startDate, endDate)
		}
	}

	suspend fun getDailyDataForDate(date: Date): DailyData? {
		return if (_isDummyDataEnabled.value) {
			null
		} else {
			val user = getUserProfileOnce() ?: return null
			dailyDataDao.getDailyDataForDate(user.id, date)
		}
	}

	suspend fun updateWeight(weight: Float) {
		if (_isDummyDataEnabled.value) return

		val user = getUserProfileOnce() ?: return

		// Update user profile weight
		val updatedUser = user.copy(weight = weight)
		updateUserProfile(updatedUser)

		// Update today's daily data weight and recalculate TDEE
		val todayData = dailyDataDao.getTodayDailyData(user.id)
		if (todayData != null) {
			val newTdee = MifflinModel.calculateDailyCalories(
				weight, user.height, user.age,
				user.gender == "Male", user.activityLevel, user.goalType, todayData.granularityValue
			)

			val updatedData = todayData.copy(
				weight = weight,
				height = user.height,
				tdee = newTdee
			)
			dailyDataDao.updateDailyData(updatedData)
		}
	}

	suspend fun getCurrentWeight(): Float? {
		if (_isDummyDataEnabled.value) return null

		val user = getUserProfileOnce() ?: return null

		// Always return user profile weight (single source of truth)
		return user.weight
	}

	suspend fun clearAllData() {
		if (_isDummyDataEnabled.value) return

		val user = getUserProfileOnce() ?: return

		userDataDao.deleteUserData(user)
	}
}
