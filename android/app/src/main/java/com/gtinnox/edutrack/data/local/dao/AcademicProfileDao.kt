package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.AcademicProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicProfileDao {
    @Query("SELECT * FROM academic_profiles WHERE userId = :userId LIMIT 1")
    fun getProfile(userId: String): Flow<AcademicProfileEntity?>

    @Query("SELECT * FROM academic_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileDirect(userId: String): AcademicProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: AcademicProfileEntity)

    @Update
    suspend fun updateProfile(profile: AcademicProfileEntity)

    @Query("DELETE FROM academic_profiles WHERE userId = :userId")
    suspend fun deleteProfile(userId: String)
}
