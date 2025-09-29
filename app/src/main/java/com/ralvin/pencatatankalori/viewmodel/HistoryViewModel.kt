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

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    private val _activitiesForSelectedDate = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activitiesForSelectedDate: StateFlow<List<ActivityLog>> = _activitiesForSelectedDate

    // Get all user activities
    val allActivities = repository.getAllUserActivities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Get user profile for daily target calculation
    val userProfile = repository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        loadActivitiesForSelectedDate()
    }

    fun selectDate(date: Date) {
        _selectedDate.value = date
        loadActivitiesForSelectedDate()
    }

    private fun loadActivitiesForSelectedDate() {
        viewModelScope.launch {
            try {
                _uiState.value = HistoryUiState.Loading
                val activities = repository.getActivitiesForDate(_selectedDate.value)
                _activitiesForSelectedDate.value = activities
                _uiState.value = HistoryUiState.Success
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load activities")
            }
        }
    }

    fun getCaloriesConsumedForDate(date: Date): Int {
        return activitiesForSelectedDate.value
            .filter { it.type == ActivityType.CONSUMPTION }
            .sumOf { it.calories ?: 0 }
    }

    fun getCaloriesBurnedForDate(date: Date): Int {
        return activitiesForSelectedDate.value
            .filter { it.type == ActivityType.WORKOUT }
            .sumOf { it.calories ?: 0 } // Using unified calories field
    }

    fun getNetCaloriesForDate(date: Date): Int {
        return getCaloriesConsumedForDate(date) - getCaloriesBurnedForDate(date)
    }

    fun getActivitiesForPeriod(startDate: Date, endDate: Date, callback: (List<ActivityLog>) -> Unit) {
        viewModelScope.launch {
            try {
                val activities = repository.getActivitiesForPeriod(startDate, endDate)
                callback(activities)
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to load period activities")
            }
        }
    }

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            try {
                repository.deleteActivity(activityId)
                // Reload activities for current date
                loadActivitiesForSelectedDate()
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to delete activity")
            }
        }
    }

    fun updateActivity(activity: ActivityLog) {
        viewModelScope.launch {
            try {
                repository.updateActivity(activity)
                // Reload activities for current date
                loadActivitiesForSelectedDate()
            } catch (e: Exception) {
                _uiState.value = HistoryUiState.Error(e.message ?: "Failed to update activity")
            }
        }
    }

    // Helper function to get last N days of data
    fun getLastNDaysData(days: Int): List<DayData> {
        val calendar = Calendar.getInstance()
        val dayDataList = mutableListOf<DayData>()
        
        // Generate days from 0 (today) to (days-1) going backwards
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
            val burned = activitiesForDay.filter { it.type == ActivityType.WORKOUT }.sumOf { it.calories ?: 0 } // Using unified calories field
            
            dayDataList.add(DayData(date, consumed, burned, consumed - burned))
        }
        
        // Return as-is since we're already building from today backwards (index 0 = today, index 1 = yesterday, etc.)
        return dayDataList
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