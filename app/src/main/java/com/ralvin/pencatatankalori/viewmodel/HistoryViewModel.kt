package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import com.ralvin.pencatatankalori.model.repository.CalorieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
	private val repository: CalorieRepository
) : ViewModel() {

	private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
	val uiState: StateFlow<HistoryUiState> = _uiState

	private val _dateRange = MutableStateFlow(getDefaultDateRange())
	val dateRange: StateFlow<Pair<Date, Date>> = _dateRange

	private val _dailyDataList =
		MutableStateFlow<List<com.ralvin.pencatatankalori.model.database.entities.DailyData>>(
			emptyList()
		)
	val dailyDataList: StateFlow<List<com.ralvin.pencatatankalori.model.database.entities.DailyData>> =
		_dailyDataList

	val allActivities = repository.getAllUserActivities()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	val userProfile = repository.getUserProfile()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = null
		)

	init {
		_uiState.value = HistoryUiState.Success
		loadDailyData()

		// Reload daily data when user profile changes (to update today's data)
		viewModelScope.launch {
			userProfile.collect { profile ->
				if (profile != null) {
					loadDailyData()
				}
			}
		}
	}

	private fun getDefaultDateRange(): Pair<Date, Date> {
		val calendar = Calendar.getInstance()
		val endDate = calendar.time
		calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 days including today
		val startDate = calendar.time
		return Pair(startDate, endDate)
	}

	fun selectDateRange(startDate: Date, endDate: Date) {
		_dateRange.value = Pair(startDate, endDate)
		loadDailyData()
	}

	private fun loadDailyData() {
		viewModelScope.launch {
			try {
				_uiState.value = HistoryUiState.Loading
				val (startDate, endDate) = _dateRange.value
				val dailyData = repository.getDailyDataForDateRange(startDate, endDate)
				_dailyDataList.value = dailyData
				_uiState.value = HistoryUiState.Success
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to load daily data")
			}
		}
	}

	fun getLastNDaysData(days: Int): List<DayData> {
		val dayDataList = mutableListOf<DayData>()
		val calendar = Calendar.getInstance()

		repeat(days) { index ->
			calendar.time = Date()
			calendar.add(Calendar.DAY_OF_YEAR, -index)
			val date = calendar.time

			val activitiesForDay = allActivities.value.filter { activity ->
				val activityCalendar = Calendar.getInstance()
				activityCalendar.time = activity.timestamp

				val dateCalendar = Calendar.getInstance()
				dateCalendar.time = date

				activityCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
						activityCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
			}

			val consumed = activitiesForDay.filter { it.type == ActivityType.CONSUMPTION }
				.sumOf { it.calories ?: 0 }
			val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }
				.sumOf { it.calories ?: 0 }
			val mealCount = activitiesForDay.count { it.type == ActivityType.CONSUMPTION }
			val workoutCount = activitiesForDay.count { it.type == ActivityType.WORKOUT }

			// Find corresponding daily data or use defaults
			val dailyData = dailyDataList.value.find { dailyDataItem ->
				val dailyDataCalendar = Calendar.getInstance()
				dailyDataCalendar.time = dailyDataItem.date
				val dateCalendar = Calendar.getInstance()
				dateCalendar.time = date

				dailyDataCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
						dailyDataCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
			}

			val profile = userProfile.value
			val tdee = dailyData?.tdee ?: profile?.dailyCalorieTarget ?: 2000
			val goalType = dailyData?.goalType ?: profile?.goalType
			?: GoalType.LOSE_WEIGHT
			val weight = dailyData?.weight ?: profile?.weight
			val height = dailyData?.height ?: profile?.height

			// Check if this date is today
			val todayCalendar = Calendar.getInstance()
			val isToday =
				todayCalendar.get(Calendar.YEAR) == Calendar.getInstance().apply { time = date }
					.get(Calendar.YEAR) &&
						todayCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance()
					.apply { time = date }.get(Calendar.DAY_OF_YEAR)

			val netCalories =
				MifflinModel.calculateNetCalories(
					caloriesConsumed = consumed,
					caloriesBurned = burned,
					goalType = goalType
				)

			dayDataList.add(
				DayData(
					dailyDataId = dailyData?.id,
					date = date,
					caloriesConsumed = consumed,
					caloriesBurned = burned,
					netCalories = netCalories,
					tdee = tdee,
					goalType = goalType,
					mealCount = mealCount,
					workoutCount = workoutCount,
					weight = weight,
					height = height,
					isToday = isToday
				)
			)
		}

		return dayDataList
	}

	fun getDayDataForSelectedRange(): List<DayData> {
		return dailyDataList.value.map { dailyDataItem ->
			val activitiesForDay = allActivities.value.filter { activity ->
				val activityCalendar = Calendar.getInstance()
				activityCalendar.time = activity.timestamp

				val dailyDataCalendar = Calendar.getInstance()
				dailyDataCalendar.time = dailyDataItem.date

				activityCalendar.get(Calendar.YEAR) == dailyDataCalendar.get(Calendar.YEAR) &&
						activityCalendar.get(Calendar.DAY_OF_YEAR) == dailyDataCalendar.get(Calendar.DAY_OF_YEAR)
			}

			val consumed = activitiesForDay.filter { it.type == ActivityType.CONSUMPTION }
				.sumOf { it.calories ?: 0 }
			val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }
				.sumOf { it.calories ?: 0 }
			val mealCount = activitiesForDay.count { it.type == ActivityType.CONSUMPTION }
			val workoutCount = activitiesForDay.count { it.type == ActivityType.WORKOUT }

			val netCalories =
				MifflinModel.calculateNetCalories(
					caloriesConsumed = consumed,
					caloriesBurned = burned,
					goalType = dailyDataItem.goalType
				)

			// Check if this date is today
			val todayCalendar = Calendar.getInstance()
			val dateCalendar = Calendar.getInstance().apply { time = dailyDataItem.date }
			val isToday = todayCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
					todayCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)

			DayData(
				dailyDataId = dailyDataItem.id,
				date = dailyDataItem.date,
				caloriesConsumed = consumed,
				caloriesBurned = burned,
				netCalories = netCalories,
				tdee = dailyDataItem.tdee,
				goalType = dailyDataItem.goalType,
				mealCount = mealCount,
				workoutCount = workoutCount,
				weight = dailyDataItem.weight,
				height = dailyDataItem.height,
				isToday = isToday
			)
		}
	}

	fun logActivity(
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		viewModelScope.launch {
			try {
				repository.logActivity(name, calories, type, pictureId, notes)
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to log activity")
			}
		}
	}

	fun logActivityForDailyData(
		dailyDataId: String,
		date: Date,
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		viewModelScope.launch {
			try {
				repository.logActivityForDailyData(
					dailyDataId = dailyDataId,
					targetDate = date,
					name = name,
					calories = calories,
					type = type,
					pictureId = pictureId,
					notes = notes
				)
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to log activity")
			}
		}
	}

	fun logActivityForDate(
		date: Date,
		name: String,
		calories: Int,
		type: ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		viewModelScope.launch {
			try {
				repository.logActivityForDate(
					date = date,
					name = name,
					calories = calories,
					type = type,
					pictureId = pictureId,
					notes = notes
				)
				loadDailyData()
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to log activity")
			}
		}
	}

	fun savePicture(imagePath: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
		viewModelScope.launch {
			try {
				val pictureId = repository.savePicture(imagePath)
				onSuccess(pictureId)
			} catch (e: Exception) {
				onError(e.message ?: "Failed to save picture")
			}
		}
	}

	fun updateActivity(
		activityId: String,
		name: String,
		calories: Int,
		notes: String?,
		pictureId: String?
	) {
		viewModelScope.launch {
			try {
				val existingActivity = allActivities.value.find { it.id == activityId }
				if (existingActivity != null) {
					val updatedActivity = existingActivity.copy(
						name = name,
						calories = calories,
						notes = notes,
						pictureId = pictureId
					)
					repository.updateActivity(updatedActivity)
				}
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to update activity")
			}
		}
	}

	fun deleteActivity(activityId: String) {
		viewModelScope.launch {
			try {
				repository.deleteActivity(activityId)
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to delete activity")
			}
		}
	}

	fun updateTodayWeight(weight: Float) {
		viewModelScope.launch {
			try {
				repository.updateWeight(weight)
				loadDailyData() // Refresh data to reflect changes
			} catch (e: Exception) {
				_uiState.value = HistoryUiState.Error(e.message ?: "Failed to update weight")
			}
		}
	}

	suspend fun getCurrentWeight(): Float? {
		return repository.getCurrentWeight()
	}
}

data class DayData(
	val dailyDataId: String? = null,
	val date: Date,
	val caloriesConsumed: Int,
	val caloriesBurned: Int,
	val netCalories: Int,
	val tdee: Int,
	val goalType: GoalType,
	val mealCount: Int = 0,
	val workoutCount: Int = 0,
	val weight: Float? = null,
	val height: Float? = null,
	val isToday: Boolean = false
)

sealed class HistoryUiState {
	object Loading : HistoryUiState()
	object Success : HistoryUiState()
	data class Error(val message: String) : HistoryUiState()
}
