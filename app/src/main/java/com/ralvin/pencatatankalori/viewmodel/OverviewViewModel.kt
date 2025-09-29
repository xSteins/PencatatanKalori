package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.data.repository.CalorieRepository
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val repository: CalorieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OverviewUiState>(OverviewUiState.Loading)
    val uiState: StateFlow<OverviewUiState> = _uiState

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

    // Combined overview data
    val overviewData = combine(
        userProfile,
        todayActivities
    ) { user, activities ->
        user?.let { userData ->
            val consumed = activities.filter { it.type == com.ralvin.pencatatankalori.data.database.entities.ActivityType.CONSUMPTION }
                .sumOf { it.calories ?: 0 }
            val burned = activities.filter { it.type == com.ralvin.pencatatankalori.data.database.entities.ActivityType.WORKOUT }
                .sumOf { it.calories ?: 0 } // Using unified calories field
            
            OverviewData(
                user = userData,
                caloriesConsumed = consumed,
                caloriesBurned = burned,
                remainingCalories = userData.dailyCalorieTarget - consumed + burned,
                todayActivities = activities
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            _uiState.value = OverviewUiState.Success
        }
    }

    // Handle weight update from BMI card
    fun updateUserWeight(newWeight: Float) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(weight = newWeight)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = OverviewUiState.Error(e.message ?: "Failed to update weight")
            }
        }
    }

    fun logFood(foodName: String, calories: Int, protein: Float, carbs: Float, portion: String) {
        viewModelScope.launch {
            try {
                repository.logFood(foodName, calories, protein, carbs, portion)
            } catch (e: Exception) {
                _uiState.value = OverviewUiState.Error(e.message ?: "Failed to log food")
            }
        }
    }

    fun logWorkout(workoutName: String, caloriesBurned: Int, duration: Int) {
        viewModelScope.launch {
            try {
                repository.logWorkout(workoutName, caloriesBurned, duration)
            } catch (e: Exception) {
                _uiState.value = OverviewUiState.Error(e.message ?: "Failed to log workout")
            }
        }
    }

    fun updateActivity(activity: ActivityLog) {
        viewModelScope.launch {
            try {
                repository.updateActivity(activity)
            } catch (e: Exception) {
                _uiState.value = OverviewUiState.Error(e.message ?: "Failed to update activity")
            }
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            try {
                repository.deleteActivity(activityId)
            } catch (e: Exception) {
                _uiState.value = OverviewUiState.Error(e.message ?: "Failed to delete activity")
            }
        }
    }
}

data class OverviewData(
    val user: UserData,
    val caloriesConsumed: Int,
    val caloriesBurned: Int,
    val remainingCalories: Int,
    val todayActivities: List<ActivityLog>
)

sealed class OverviewUiState {
    object Loading : OverviewUiState()
    object Success : OverviewUiState()
    data class Error(val message: String) : OverviewUiState()
}