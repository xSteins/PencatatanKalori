package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.data.repository.CalorieRepository
import com.ralvin.pencatatankalori.data.database.entities.ActivityLog
import com.ralvin.pencatatankalori.data.database.entities.ActivityType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    }

    fun getLastNDaysData(days: Int): List<DayData> {
        val calendar = Calendar.getInstance()
        val dayDataList = mutableListOf<DayData>()
        
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
            
            val consumed = activitiesForDay.filter { it.type == ActivityType.CONSUMPTION }.sumOf { it.calories ?: 0 }
            val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }.sumOf { it.calories ?: 0 }
            
            // Use MifflinModel for consistent net calorie calculation
            val profile = userProfile.value
            val netCalories = if (profile != null) {
                com.ralvin.pencatatankalori.health.model.MifflinModel.calculateNetCalories(
                    caloriesConsumed = consumed,
                    caloriesBurned = burned,
                    goalType = profile.goalType
                )
            } else {
                consumed - burned
            }
            
            dayDataList.add(DayData(date, consumed, burned, netCalories))
        }
        
        return dayDataList
    }

    fun getDayDataForSelectedRange(): List<DayData> {
        val (startDate, endDate) = _dateRange.value
        val dayDataList = mutableListOf<DayData>()
        
        val currentDate = Calendar.getInstance()
        currentDate.time = startDate
        
        while (currentDate.time <= endDate) {
            val date = currentDate.time
            
            val activitiesForDay = allActivities.value.filter { activity ->
                val activityCalendar = Calendar.getInstance()
                activityCalendar.time = activity.timestamp
                
                val dateCalendar = Calendar.getInstance()
                dateCalendar.time = date
                
                activityCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
                activityCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)
            }
            
            val consumed = activitiesForDay.filter { it.type == ActivityType.CONSUMPTION }.sumOf { it.calories ?: 0 }
            val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }.sumOf { it.calories ?: 0 }
            
            // Use MifflinModel for consistent net calorie calculation
            val profile = userProfile.value
            val netCalories = if (profile != null) {
                com.ralvin.pencatatankalori.health.model.MifflinModel.calculateNetCalories(
                    caloriesConsumed = consumed,
                    caloriesBurned = burned,
                    goalType = profile.goalType
                )
            } else {
                consumed - burned
            }
            
            dayDataList.add(DayData(date, consumed, burned, netCalories))
            currentDate.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return dayDataList.reversed()
    }

    fun logFood(foodName: String, calories: Int, protein: Float, carbs: Float, portion: String, pictureId: String? = null) {
        viewModelScope.launch {
            try {
                repository.logFood(foodName, calories, protein, carbs, portion, pictureId)
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to log food")
            }
        }
    }

    fun logWorkout(workoutName: String, caloriesBurned: Int, duration: Int, pictureId: String? = null) {
        viewModelScope.launch {
            try {
                repository.logWorkout(workoutName, caloriesBurned, duration, pictureId)
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to log workout")
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

    fun updateActivity(activityId: String, name: String, calories: Int, protein: Float?, carbs: Float?, portion: String?, duration: Int?, pictureId: String?) {
        viewModelScope.launch {
            try {
                val existingActivity = allActivities.value.find { it.id == activityId }
                if (existingActivity != null) {
                    val updatedActivity = existingActivity.copy(
                        foodName = if (existingActivity.type == ActivityType.CONSUMPTION) name else existingActivity.foodName,
                        workoutName = if (existingActivity.type == ActivityType.WORKOUT) name else existingActivity.workoutName,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        portion = portion,
                        duration = duration,
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
}

data class DayData(
    val date: Date,
    val caloriesConsumed: Int,
    val caloriesBurned: Int,
    val netCalories: Int
)

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    object Success : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}