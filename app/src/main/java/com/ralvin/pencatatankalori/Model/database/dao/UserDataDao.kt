package com.ralvin.pencatatankalori.Model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ralvin.pencatatankalori.Model.database.entities.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
	@Query("SELECT * FROM user LIMIT 1")
	fun getUserData(): Flow<UserData?>

	@Query("SELECT * FROM user WHERE id = :userId")
	suspend fun getUserById(userId: String): UserData?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertUserData(userData: UserData)

	@Update
	suspend fun updateUserData(userData: UserData)

	@Delete
	suspend fun deleteUserData(userData: UserData)

	@Query("SELECT COUNT(*) FROM user")
	suspend fun getUserDataCount(): Int
} 