package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.data.repository.CalorieRepository
import com.ralvin.pencatatankalori.data.database.entities.UserData
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CalorieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    val userProfile = repository.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    val isDummyDataEnabled = repository.isDummyDataEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Success
        }
    }

    fun updateUserProfile(
        name: String,
        age: Int,
        gender: String,
        weight: Float,
        height: Float,
        activityLevel: ActivityLevel,
        goalType: GoalType,
        dailyCalorieTarget: Int
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading
                val currentUser = repository.getUserProfileOnce()
                
                val updatedUser = if (currentUser != null) {
                    currentUser.copy(
                        name = name,
                        age = age,
                        gender = gender,
                        weight = weight,
                        height = height,
                        activityLevel = activityLevel,
                        goalType = goalType,
                        dailyCalorieTarget = dailyCalorieTarget
                    )
                } else {
                    UserData(
                        name = name,
                        age = age,
                        gender = gender,
                        weight = weight,
                        height = height,
                        activityLevel = activityLevel,
                        goalType = goalType,
                        dailyCalorieTarget = dailyCalorieTarget
                    )
                }
                
                if (currentUser != null) {
                    repository.updateUserProfile(updatedUser)
                } else {
                    repository.createUser(updatedUser)
                }
                
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun updateWeight(newWeight: Float) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(weight = newWeight)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update weight")
            }
        }
    }

    fun updateHeight(newHeight: Float) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(height = newHeight)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update height")
            }
        }
    }

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(activityLevel = activityLevel)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update activity level")
            }
        }
    }

    fun updateGoalType(goalType: GoalType) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(goalType = goalType)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update goal type")
            }
        }
    }

    fun updateCalorieTarget(newTarget: Int) {
        viewModelScope.launch {
            try {
                val currentUser = repository.getUserProfileOnce()
                currentUser?.let { user ->
                    val updatedUser = user.copy(dailyCalorieTarget = newTarget)
                    repository.updateUserProfile(updatedUser)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Failed to update calorie target")
            }
        }
    }

    fun isUserCreated(): Boolean {
        return userProfile.value != null
    }

    suspend fun checkUserExists(): Boolean {
        return repository.isUserCreated()
    }

    fun calculateBMI(): Float? {
        val user = userProfile.value
        return if (user != null && user.height > 0) {
            val heightInMeters = user.height / 100
            user.weight / (heightInMeters * heightInMeters)
        } else null
    }

    fun getBMICategory(): String? {
        val bmi = calculateBMI()
        return when {
            bmi == null -> null
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal weight"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }
    
    fun toggleDummyData() {
        repository.toggleDummyData()
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}