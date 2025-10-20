package com.ralvin.pencatatankalori.Model.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ralvin.pencatatankalori.Model.database.entities.ActivityPicture

@Dao
interface ActivityPicturesDao {
	@Query("SELECT * FROM activity_picture WHERE id = :pictureId")
	suspend fun getPictureById(pictureId: String): ActivityPicture?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPicture(picture: ActivityPicture): Long

	@Delete
	suspend fun deletePicture(picture: ActivityPicture)

	@Query("DELETE FROM activity_picture WHERE id = :pictureId")
	suspend fun deletePictureById(pictureId: String)
}
