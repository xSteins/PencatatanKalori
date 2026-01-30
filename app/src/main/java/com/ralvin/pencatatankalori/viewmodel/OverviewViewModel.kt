package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.UserData
import com.ralvin.pencatatankalori.model.formula.MifflinModel
import com.ralvin.pencatatankalori.model.CalorieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
	private val repository: CalorieRepository
) : ViewModel() {

	val userProfile = repository.getUserProfile()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = null
		)

	val todayActivities = repository.getTodayActivities()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = emptyList()
		)

	val overviewData = combine(
		userProfile,
		todayActivities,
		repository.getTodayDailyDataFlow()
	) { user, activities, dailyData ->
		user?.let { userData ->
			val consumed =
				activities.filter { it.type == com.ralvin.pencatatankalori.model.database.entities.ActivityType.CONSUMPTION }
					.sumOf { it.calories ?: 0 }
			val burned =
				activities.filter { it.type == com.ralvin.pencatatankalori.model.database.entities.ActivityType.WORKOUT }
					.sumOf { it.calories ?: 0 }

			val remainingCalories = if (dailyData != null) {
				MifflinModel.calculateRemainingCalories(
					dailyCalorieTarget = dailyData.tdee,
					caloriesConsumed = consumed,
					caloriesBurned = burned,
				)
			} else {
				MifflinModel.calculateRemainingCalories(
					dailyCalorieTarget = userData.dailyCalorieTarget,
					caloriesConsumed = consumed,
					caloriesBurned = burned,
				)
			}

			val netCalories = MifflinModel.calculateNetCalories(
				caloriesConsumed = consumed,
				caloriesBurned = burned,
			)

			OverviewData(
				user = userData,
				caloriesConsumed = consumed,
				caloriesBurned = burned,
				remainingCalories = remainingCalories,
				netCalories = netCalories,
				todayActivities = activities,
				dailyData = dailyData
			)
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = null
	)

	fun logActivity(
		name: String,
		calories: Int,
		type: com.ralvin.pencatatankalori.model.database.entities.ActivityType,
		pictureId: String? = null,
		notes: String? = null
	) {
		viewModelScope.launch {
			repository.logActivity(name, calories, type, pictureId, notes)
		}
	}

	fun updateActivity(activity: ActivityLog) {
		viewModelScope.launch {
			repository.updateActivity(activity)
		}
	}

	fun deleteActivity(activityId: String) {
		viewModelScope.launch {
			repository.deleteActivity(activityId)
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

	fun getPicture(pictureId: String, onSuccess: (String?) -> Unit) {
		viewModelScope.launch {
			try {
				val pictureUri = repository.getPicture(pictureId)
				onSuccess(pictureUri)
			} catch (e: Exception) {
				onSuccess(null)
			}
		}
	}
}

data class OverviewData(
	val user: UserData,
	val caloriesConsumed: Int,
	val caloriesBurned: Int,
	val remainingCalories: Int,
	val netCalories: Int,
	val todayActivities: List<ActivityLog>,
	val dailyData: com.ralvin.pencatatankalori.model.database.entities.DailyData? = null
)
