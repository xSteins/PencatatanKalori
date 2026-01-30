package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.database.entities.UserData
import com.ralvin.pencatatankalori.model.CalorieRepository
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val repository: CalorieRepository
) : ViewModel() {

	private val _isCompleted = MutableStateFlow(false)
	val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

	fun createUserData(
		age: Int,
		gender: String,
		weight: Float,
		height: Float,
		activityLevel: ActivityLevel,
		goalType: GoalType,
		dailyCalorieTarget: Int
	) {
		viewModelScope.launch {
			val userData = UserData(
				age = age,
				gender = gender,
				weight = weight,
				height = height,
				activityLevel = activityLevel,
				goalType = goalType,
				dailyCalorieTarget = dailyCalorieTarget
			)

			repository.createUser(userData)
			repository.markOnboardingComplete()
			_isCompleted.value = true
		}
	}

	fun resetState() {
		_isCompleted.value = false
	}
}
