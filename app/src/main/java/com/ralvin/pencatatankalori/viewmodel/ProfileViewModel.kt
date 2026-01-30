package com.ralvin.pencatatankalori.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.model.database.entities.UserData
import com.ralvin.pencatatankalori.model.formula.ActivityLevel
import com.ralvin.pencatatankalori.model.formula.GoalType
import com.ralvin.pencatatankalori.model.CalorieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
	val repository: CalorieRepository
) : ViewModel() {

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

	fun updateUserProfile(
		age: Int,
		gender: String,
		weight: Float,
		height: Float,
		activityLevel: ActivityLevel,
		goalType: GoalType,
		dailyCalorieTarget: Int
	) {
		viewModelScope.launch {
			val currentUser = repository.getUserProfileOnce()

			val updatedUser = currentUser?.copy(
				age = age,
				gender = gender,
				weight = weight,
				height = height,
				activityLevel = activityLevel,
				goalType = goalType,
				dailyCalorieTarget = dailyCalorieTarget
			)
				?: UserData(
					age = age,
					gender = gender,
					weight = weight,
					height = height,
					activityLevel = activityLevel,
					goalType = goalType,
					dailyCalorieTarget = dailyCalorieTarget
				)

			if (currentUser != null) {
				repository.updateUserDataAndTodayTdee(updatedUser)
			} else {
				repository.createUser(updatedUser)
			}
		}
	}

	fun updateCalorieSettings(granularityValue: Int) {
		viewModelScope.launch {
			repository.updateCalorieSettings(granularityValue)
		}
	}

	val todayDailyData = repository.getTodayDailyDataFlow()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = null
		)

	fun toggleDummyData() {
		repository.toggleDummyData()
	}

	fun markOnboardingComplete() {
		viewModelScope.launch {
			repository.markOnboardingComplete()
		}
	}

	suspend fun initializeOnboardingState() {
		repository.checkAndInitializeOnboardingState()
	}

	fun dismissInitialBottomSheet() {
		repository.dismissInitialBottomSheet()
	}
}
