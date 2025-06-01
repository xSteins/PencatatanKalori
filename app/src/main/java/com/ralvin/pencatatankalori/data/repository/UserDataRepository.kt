package com.ralvin.pencatatankalori.data.repository

import com.ralvin.pencatatankalori.data.database.entities.UserData
import com.ralvin.pencatatankalori.data.database.dao.UserDataDao
import com.ralvin.pencatatankalori.health.model.ActivityLevel
import com.ralvin.pencatatankalori.health.model.GoalType
import com.ralvin.pencatatankalori.health.model.MifflinModel
import kotlinx.coroutines.flow.Flow

class UserDataRepository(
    private val userDataDao: UserDataDao
) {
    fun getUserData(): Flow<UserData?> = userDataDao.getUserData()

    suspend fun hasUserData(): Boolean = userDataDao.getUserDataCount() > 0

    suspend fun createUserData(
        age: Int,
        weight: Double,
        height: Double,
        isMale: Boolean,
        activityLevel: ActivityLevel,
        goalType: GoalType
    ) {
        val rmr = MifflinModel.calculateRMR(weight, height, age, isMale)
        val dailyCaloriesTarget = MifflinModel.calculateDailyCaloriesTarget(rmr, activityLevel, goalType)

        val userData = UserData(
            uid = 1, // Since we only have one user data
            age = age,
            weight = weight,
            height = height,
            isMale = isMale,
            activityLevel = activityLevel,
            goalType = goalType,
            rmr = rmr,
            dailyCaloriesTarget = dailyCaloriesTarget
        )

        userDataDao.insertUserData(userData)
    }
} 