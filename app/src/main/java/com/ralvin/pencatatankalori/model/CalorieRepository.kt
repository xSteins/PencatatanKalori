package com.ralvin.pencatatankalori.model

import androidx.room.Transaction
import com.ralvin.pencatatankalori.model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.model.database.dao.DailyDataDao
import com.ralvin.pencatatankalori.model.database.dao.UserDataDao
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.database.entities.DailyData
import com.ralvin.pencatatankalori.model.database.entities.UserData
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class CalorieRepository @Inject constructor(
	private val userDataDao: UserDataDao,
	private val activityLogDao: ActivityLogDao,
	private val dailyDataDao: DailyDataDao
) {

	private val _isDummyDataEnabled = MutableStateFlow(false)
	val isDummyDataEnabled: Flow<Boolean> = _isDummyDataEnabled

	private val _shouldShowInitialBottomSheet = MutableStateFlow(false)
	val shouldShowInitialBottomSheet: Flow<Boolean> = _shouldShowInitialBottomSheet

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
		return try {
			if (_isDummyDataEnabled.value) {
				createDummyUser()
			} else {
				userDataDao.getUserData().first()
			}
		} catch (e: Exception) {
			null
		}
	}

	suspend fun updateUserProfile(user: UserData) {
		try {
			userDataDao.updateUserData(user)
		} catch (e: Exception) {
			throw Exception("Gagal update profil user: ${e.message}")
		}
	}

	suspend fun createUser(user: UserData) {
		try {
			userDataDao.insertUserData(user)
		} catch (e: Exception) {
			throw Exception("Gagal membuat user baru: ${e.message}")
		}
	}

	suspend fun isUserCreated(): Boolean {
		return try {
			userDataDao.getUserDataCount() > 0
		} catch (e: Exception) {
			false
		}
	}

	suspend fun checkAndInitializeOnboardingState() {
		val userExists = isUserCreated()
		_shouldShowInitialBottomSheet.value = !userExists
	}

	fun dismissInitialBottomSheet() {
		_shouldShowInitialBottomSheet.value = false
	}

	fun markOnboardingComplete() {
		_shouldShowInitialBottomSheet.value = false
	}


	suspend fun logActivity(
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null,
		date: Date? = null,
		dailyDataId: String? = null
	) {
		if (_isDummyDataEnabled.value) return

		try {
			val dailyData = when {
				dailyDataId != null -> dailyDataDao.getDailyDataById(dailyDataId)
				date != null -> getOrCreateDailyDataForDate(date)
				else -> getOrCreateTodayDailyData()
			} ?: return

			val timestamp = when {
				date != null -> alignTimestampWithDate(date)
				else -> Date()
			}

			insertActivityForDailyData(
				dailyData = dailyData,
				timestamp = timestamp,
				name = name,
				calories = calories,
				type = type,
				pictureId = pictureId,
				notes = notes
			)
		} catch (e: Exception) {
			throw Exception("Gagal mencatat aktivitas: ${e.message}")
		}
	}

	@Transaction
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
			try {
				activityLogDao.updateActivity(activity)
			} catch (e: Exception) {
				throw Exception("Gagal update aktivitas: ${e.message}")
			}
		}
	}

	suspend fun deleteActivity(activityId: String) {
		if (!_isDummyDataEnabled.value) {
			try {
				activityLogDao.deleteActivityById(activityId)
			} catch (e: Exception) {
				throw Exception("Gagal menghapus aktivitas: ${e.message}")
			}
		}
	}

	fun savePicture(imagePath: String): String {
		if (_isDummyDataEnabled.value) {
			return "dummy_picture_id"
		}
		return imagePath
	}

	fun getPicture(pictureId: String): String? {
		if (_isDummyDataEnabled.value || pictureId.endsWith("_placeholder")) {
			val imageName = pictureId.replace("_placeholder", "")
			return "android.resource://com.ralvin.pencatatankalori/assets/PlaceholderImage/$imageName.jpg"
		}
		return pictureId
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

	suspend fun getOrCreateTodayDailyData(): DailyData? {
		if (_isDummyDataEnabled.value) return null

		return try {
			val user = getUserProfileOnce() ?: return null
			val existingData = dailyDataDao.getTodayDailyData(user.id)

			if (existingData != null) {
				existingData
			} else {
				createDailyDataForUser(user, Date())
			}
		} catch (e: Exception) {
			null
		}
	}

	suspend fun getOrCreateDailyDataForDate(date: Date): DailyData? {
		if (_isDummyDataEnabled.value) return null

		return try {
			val user = getUserProfileOnce() ?: return null
			val existingData = dailyDataDao.getDailyDataForDate(user.id, date)

			if (existingData != null) {
				existingData
			} else {
				createDailyDataForUser(user, date)
			}
		} catch (e: Exception) {
			null
		}
	}

	@Transaction
	private suspend fun createDailyDataForUser(user: UserData, date: Date): DailyData {
		val granularityValue = MifflinModel.Companion.getGranularityValue()
		val tdee = MifflinModel.Companion.calculateDailyCalories(
			user.weight, user.height, user.age,
			user.gender == "Male", user.activityLevel, granularityValue
		)
		val newDailyData = DailyData(
			userId = user.id,
			date = date,
			tdee = tdee,
			granularityValue = granularityValue,
			totalCaloriesConsumption = 0,
			goalType = user.goalType,
			weight = user.weight,
			activityLevel = user.activityLevel
		)
		dailyDataDao.insertDailyData(newDailyData)
		return newDailyData
	}

	@Transaction
	suspend fun updateCalorieSettings(
		granularityValue: Int
	) {
		if (_isDummyDataEnabled.value) return

		try {
			val user = getUserProfileOnce() ?: return

			val newTdee = MifflinModel.Companion.calculateDailyCalories(
				user.weight, user.height, user.age,
				user.gender == "Male", user.activityLevel, granularityValue
			)

			val existingData = dailyDataDao.getTodayDailyData(user.id)
			val updatedData = existingData?.copy(
				tdee = newTdee,
				granularityValue = granularityValue,
				goalType = user.goalType,
				weight = user.weight,
				activityLevel = user.activityLevel
			)
				?: DailyData(
					userId = user.id,
					date = Date(),
					tdee = newTdee,
					granularityValue = granularityValue,
					totalCaloriesConsumption = 0,
					goalType = user.goalType,
					weight = user.weight,
					activityLevel = user.activityLevel
				)

			if (existingData != null) {
				dailyDataDao.updateDailyData(updatedData)
			} else {
				dailyDataDao.insertDailyData(updatedData)
			}

			val updatedUser = user.copy(dailyCalorieTarget = newTdee)
			updateUserProfile(updatedUser)

			MifflinModel.Companion.adjustTargetCalorie(granularityValue)
		} catch (e: Exception) {
			throw Exception("Gagal update pengaturan kalori: ${e.message}")
		}
	}

	suspend fun loadCalorieSettingsFromDatabase() {
		if (_isDummyDataEnabled.value) return

		try {
			val user = getUserProfileOnce() ?: return
			val todayData = dailyDataDao.getTodayDailyData(user.id) ?: return

			MifflinModel.Companion.adjustTargetCalorie(todayData.granularityValue)
		} catch (e: Exception) {
			// silent fail, menggunakan default granularity value
		}
	}

	@Transaction
	suspend fun updateUserDataAndTodayTdee(userData: UserData) {
		try {
			updateUserProfile(userData)

			if (!_isDummyDataEnabled.value) {
				val todayData = dailyDataDao.getTodayDailyData(userData.id)
				if (todayData != null) {
					val newTdee = MifflinModel.Companion.calculateDailyCalories(
						userData.weight,
						userData.height,
						userData.age,
						userData.gender == "Male",
						userData.activityLevel,
						todayData.granularityValue
					)
					val updatedData = todayData.copy(
						tdee = newTdee,
						goalType = userData.goalType,
						weight = userData.weight,
						activityLevel = userData.activityLevel
					)
					dailyDataDao.updateDailyData(updatedData)
				}
			}
		} catch (e: Exception) {
			throw Exception("Gagal update data user dan TDEE: ${e.message}")
		}
	}


	fun getAllUserActivities(): Flow<List<ActivityLog>> {
		return _isDummyDataEnabled.flatMapLatest { isDummy ->
			if (isDummy) {
				flowOf(createDummyActivities())
			} else {
				userDataDao.getUserData().flatMapLatest { user ->
					user?.let { activityLogDao.getAllActivitiesByUserId(it.id) }
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
							notes = "Duration: $duration minutes",
							pictureId = workoutImageId
						)
					)
				}
			}
		}

		return activities.sortedByDescending { it.timestamp }
	}

	suspend fun getDailyDataForDateRange(startDate: Date, endDate: Date): List<DailyData> {
		return try {
			if (_isDummyDataEnabled.value) {
				emptyList()
			} else {
				val user = getUserProfileOnce() ?: return emptyList()
				dailyDataDao.getDailyDataForDateRange(user.id, startDate, endDate)
			}
		} catch (e: Exception) {
			emptyList()
		}
	}

	suspend fun getCurrentWeight(): Float? {
		if (_isDummyDataEnabled.value) return null

		return try {
			val user = getUserProfileOnce() ?: return null
			user.weight
		} catch (e: Exception) {
			null
		}
	}
}
