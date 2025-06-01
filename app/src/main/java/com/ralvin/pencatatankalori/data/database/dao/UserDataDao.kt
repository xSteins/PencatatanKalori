package com.ralvin.pencatatankalori.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ralvin.pencatatankalori.data.database.entities.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM UserData LIMIT 1")
    fun getUserData(): Flow<UserData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userData: UserData)

    @Query("SELECT COUNT(*) FROM UserData")
    suspend fun getUserDataCount(): Int
} 