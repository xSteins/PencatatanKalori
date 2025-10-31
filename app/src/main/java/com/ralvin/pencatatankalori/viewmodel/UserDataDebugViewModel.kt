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

sealed class UserDataDebugUiState {
	object Loading : UserDataDebugUiState()
	data class Success(val debugData: DebugData) : UserDataDebugUiState()
	data class Error(val message: String) : UserDataDebugUiState()
}

@HiltViewModel
class UserDataDebugViewModel @Inject constructor(
	private val userDataDao: UserDataDao,
	private val activityLogDao: ActivityLogDao
) : ViewModel() {

	private val _uiState = MutableStateFlow<UserDataDebugUiState>(UserDataDebugUiState.Loading)
	val uiState: StateFlow<UserDataDebugUiState> = _uiState.asStateFlow()

	init {
		loadDebugData()
	}

	fun loadDebugData() {
		viewModelScope.launch {
			try {
				_uiState.value = UserDataDebugUiState.Loading

				val userData = userDataDao.getUserData().first()

				val activityLogs = if (userData != null) {
					activityLogDao.getActivitiesByUserId(userData.id).first()
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

				val debugData = DebugData(
					userData = userData,
					activityLogs = activityLogs,
					totalActivities = totalActivities,
					totalConsumptionActivities = totalConsumptionActivities,
					totalWorkoutActivities = totalWorkoutActivities,
					totalCaloriesConsumed = totalCaloriesConsumed,
					totalCaloriesBurned = totalCaloriesBurned
				)

				_uiState.value = UserDataDebugUiState.Success(debugData)
			} catch (e: Exception) {
				_uiState.value =
					UserDataDebugUiState.Error("Failed to load debug data: ${e.message}")
			}
		}
	}

	fun refreshData() {
		loadDebugData()
	}

	fun clearAllData() {
		viewModelScope.launch {
			try {
				_uiState.value = UserDataDebugUiState.Loading
				// Access repository through the DAOs for now since we don't inject repository
				val userData = userDataDao.getUserData().first()
				if (userData != null) {
					userDataDao.deleteUserData(userData)
				}
				// Refresh data to show empty state
				loadDebugData()
			} catch (e: Exception) {
				_uiState.value = UserDataDebugUiState.Error("Failed to clear data: ${e.message}")
			}
		}
	}
}
