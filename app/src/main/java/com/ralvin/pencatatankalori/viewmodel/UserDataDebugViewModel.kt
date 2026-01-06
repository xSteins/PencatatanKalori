package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.database.dao.ActivityLogDao
import com.ralvin.pencatatankalori.model.database.dao.UserDataDao
import com.ralvin.pencatatankalori.model.database.entities.ActivityLog
import com.ralvin.pencatatankalori.model.database.entities.ActivityType
import com.ralvin.pencatatankalori.model.database.entities.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebugData(
	val userData: UserData?,
	val activityLogs: List<ActivityLog>,
	val totalActivities: Int,
	val totalConsumptionActivities: Int,
	val totalWorkoutActivities: Int,
	val totalCaloriesConsumed: Int,
	val totalCaloriesBurned: Int
)

@HiltViewModel
class UserDataDebugViewModel @Inject constructor(
	private val userDataDao: UserDataDao,
	private val activityLogDao: ActivityLogDao
) : ViewModel() {

	private val _debugData = MutableStateFlow<DebugData?>(null)
	val debugData: StateFlow<DebugData?> = _debugData.asStateFlow()

	private val _errorMessage = MutableStateFlow<String?>(null)
	val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

	init {
		loadDebugData()
	}

	fun loadDebugData() {
		viewModelScope.launch {
			try {
				_errorMessage.value = null

				val userData = userDataDao.getUserData().first()

				val activityLogs = if (userData != null) {
					activityLogDao.getAllActivitiesByUserId(userData.id).first()
				} else {
					emptyList()
				}

				val totalActivities = activityLogs.size
				val totalConsumptionActivities =
					activityLogs.count { it.type == ActivityType.CONSUMPTION }
				val totalWorkoutActivities = activityLogs.count { it.type == ActivityType.WORKOUT }
				val totalCaloriesConsumed = activityLogs
					.filter { it.type == ActivityType.CONSUMPTION }
					.sumOf { it.calories ?: 0 }
				val totalCaloriesBurned = activityLogs
					.filter { it.type == ActivityType.WORKOUT }
					.sumOf { it.calories ?: 0 }

				_debugData.value = DebugData(
					userData = userData,
					activityLogs = activityLogs,
					totalActivities = totalActivities,
					totalConsumptionActivities = totalConsumptionActivities,
					totalWorkoutActivities = totalWorkoutActivities,
					totalCaloriesConsumed = totalCaloriesConsumed,
					totalCaloriesBurned = totalCaloriesBurned
				)
			} catch (e: Exception) {
				_errorMessage.value = "Failed to load debug data: ${e.message}"
			}
		}
	}

	fun refreshData() {
		loadDebugData()
	}

	fun clearAllData() {
		viewModelScope.launch {
			try {
				_errorMessage.value = null
				val userData = userDataDao.getUserData().first()
				if (userData != null) {
					userDataDao.deleteUserData(userData)
				}
				_debugData.value = DebugData(
					userData = null,
					activityLogs = emptyList(),
					totalActivities = 0,
					totalConsumptionActivities = 0,
					totalWorkoutActivities = 0,
					totalCaloriesConsumed = 0,
					totalCaloriesBurned = 0
				)
			} catch (e: Exception) {
				_errorMessage.value = "Failed to clear data: ${e.message}"
			}
		}
	}
}
