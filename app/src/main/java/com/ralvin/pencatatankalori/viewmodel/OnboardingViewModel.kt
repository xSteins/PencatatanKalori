package com.ralvin.pencatatankalori.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ralvin.pencatatankalori.data.database.AppDatabase
//import com.ralvin.pencatatankalori.data.database.entity.UserData
import com.ralvin.pencatatankalori.data.repository.UserDataRepository
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
//    private val repository: UserDataRepository
    
    // Inisialisasi sementara untuk membuat ViewModel compile
//    private val _existingUserData = MutableStateFlow<UserData?>(null)
//    val existingUserData: StateFlow<UserData?> = _existingUserData

    init {
//        val database = AppDatabase.getDatabase(application)
//        repository = UserDataRepository(database.userDataDao())
        // TODO: implement ulang
        /*
        existingUserData = repository.getUserData()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
        */
    }
//
//    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Initial)
//    val uiState: StateFlow<OnboardingUiState> = _uiState
//
//    fun createUserData(
//        age: Int,
//        weight: Double,
//        height: Double,
//        isMale: Boolean,
//        activityLevel: ActivityLevel,
//        goalType: GoalType
//    ) {
//        viewModelScope.launch {
//            try {
//                repository.createUserData(
//                    age = age,
//                    weight = weight,
//                    height = height,
//                    isMale = isMale,
//                    activityLevel = activityLevel,
//                    goalType = goalType
//                )
//                _uiState.value = OnboardingUiState.Success
//            } catch (e: Exception) {
//                _uiState.value = OnboardingUiState.Error(e.message ?: "Unknown error occurred")
//            }
//        }
//    }
}

sealed class OnboardingUiState {
    object Initial : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}