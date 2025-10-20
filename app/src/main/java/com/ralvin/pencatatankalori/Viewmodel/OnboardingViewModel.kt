package com.ralvin.pencatatankalori.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.Model.database.entities.UserData
import com.ralvin.pencatatankalori.Model.repository.CalorieRepository
import com.ralvin.pencatatankalori.Model.formula.ActivityLevel
import com.ralvin.pencatatankalori.Model.formula.GoalType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val repository: CalorieRepository
) : ViewModel() {

	private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Initial)
	val uiState: StateFlow<OnboardingUiState> = _uiState


	fun createUserData(
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
				_uiState.value = OnboardingUiState.Loading

				val userData = UserData(
					name = name,
					age = age,
					gender = gender,
					weight = weight,
					height = height,
					activityLevel = activityLevel,
					goalType = goalType,
					dailyCalorieTarget = dailyCalorieTarget
				)

				repository.createUser(userData)
				_uiState.value = OnboardingUiState.Success
			} catch (e: Exception) {
				_uiState.value = OnboardingUiState.Error(e.message ?: "Unknown error occurred")
			}
		}
	}

	fun resetUiState() {
		_uiState.value = OnboardingUiState.Initial
	}
}

sealed class OnboardingUiState {
	object Initial : OnboardingUiState()
	object Loading : OnboardingUiState()
	object Success : OnboardingUiState()
	data class Error(val message: String) : OnboardingUiState()
}