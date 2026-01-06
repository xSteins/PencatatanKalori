package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.CalorieRepository
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.formula.MifflinModel
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
		loadDailyData()
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
		calendar.add(Calendar.DAY_OF_YEAR, -6) // pilih 6 hari sebelumnya + 1 (hari ini)
		val startDate = calendar.time
		return Pair(startDate, endDate)
	}

	fun selectDateRange(startDate: Date, endDate: Date) {
		_dateRange.value = Pair(startDate, endDate)
		loadDailyData()
	}

	private fun loadDailyData() {
		viewModelScope.launch {
			val (startDate, endDate) = _dateRange.value
			val dailyData = repository.getDailyDataForDateRange(startDate, endDate)
			_dailyDataList.value = dailyData
		}
	}

	fun getLastNDaysData(days: Int): List<DayData> {
		val profile = userProfile.value ?: return emptyList()

		val dayDataList = mutableListOf<DayData>()
		val calendar = Calendar.getInstance()

		repeat(days) { index ->
			calendar.time = Date()
			calendar.add(Calendar.DAY_OF_YEAR, -index)
			val date = calendar.time

			val activitiesForDay = allActivities.value.filter { activity ->
				val activityCal = Calendar.getInstance().apply { time = activity.timestamp }
				val dateCal = Calendar.getInstance().apply { time = date }
				activityCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) &&
					activityCal.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR)
			}

			val consumed = activitiesForDay.filter { it.type == ActivityType.CONSUMPTION }
				.sumOf { it.calories ?: 0 }
			val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }
				.sumOf { it.calories ?: 0 }
			val mealCount = activitiesForDay.count { it.type == ActivityType.CONSUMPTION }
			val workoutCount = activitiesForDay.count { it.type == ActivityType.WORKOUT }

			val dailyData = dailyDataList.value.find { dailyDataItem ->
				val dailyDataCal = Calendar.getInstance().apply { time = dailyDataItem.date }
				val dateCal = Calendar.getInstance().apply { time = date }
				dailyDataCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) &&
					dailyDataCal.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR)
			}

			if (dailyData != null || activitiesForDay.isNotEmpty()) {
				val tdee = dailyData?.tdee ?: profile.dailyCalorieTarget
				val goalType = dailyData?.goalType ?: profile.goalType
				val weight = dailyData?.weight ?: profile.weight
				val activityLevel = dailyData?.activityLevel ?: profile.activityLevel

				val todayCalendar = Calendar.getInstance()
				val dateCal = Calendar.getInstance().apply { time = date }
				val isToday = todayCalendar.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) &&
					todayCalendar.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR)

				val netCalories =
					MifflinModel.calculateNetCalories(
						caloriesConsumed = consumed,
						caloriesBurned = burned
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
						activityLevel = activityLevel,
						isToday = isToday
					)
				)
			}
		}

		return dayDataList
	}

	fun getDayDataForSelectedRange(): List<DayData> {
		val profile = userProfile.value ?: return emptyList()

		return dailyDataList.value.map { dailyDataItem ->
			val activitiesForDay = allActivities.value.filter { activity ->
				val activityCal = Calendar.getInstance().apply { time = activity.timestamp }
				val dailyDataCal = Calendar.getInstance().apply { time = dailyDataItem.date }
				activityCal.get(Calendar.YEAR) == dailyDataCal.get(Calendar.YEAR) &&
					activityCal.get(Calendar.DAY_OF_YEAR) == dailyDataCal.get(Calendar.DAY_OF_YEAR)
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
					caloriesBurned = burned
				)
			val todayCalendar = Calendar.getInstance()
			val dailyDataCal = Calendar.getInstance().apply { time = dailyDataItem.date }
			val isToday = todayCalendar.get(Calendar.YEAR) == dailyDataCal.get(Calendar.YEAR) &&
				todayCalendar.get(Calendar.DAY_OF_YEAR) == dailyDataCal.get(Calendar.DAY_OF_YEAR)

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
				activityLevel = dailyDataItem.activityLevel,
				isToday = isToday
			)
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
			repository.logActivity(
				name = name,
				calories = calories,
				type = type,
				pictureId = pictureId,
				notes = notes,
				date = date
			)
			loadDailyData()
		}
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
	val activityLevel: ActivityLevel? = null,
	val isToday: Boolean = false
)
