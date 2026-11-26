package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.StudyPreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyPreferencesDao {
    @Query("SELECT * FROM study_preferences WHERE userId = :userId LIMIT 1")
    fun getPreferences(userId: String): Flow<StudyPreferencesEntity?>

    @Query("SELECT * FROM study_preferences WHERE userId = :userId LIMIT 1")
    suspend fun getPreferencesDirect(userId: String): StudyPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preferences: StudyPreferencesEntity)

    @Update
    suspend fun updatePreferences(preferences: StudyPreferencesEntity)

    @Query("DELETE FROM study_preferences WHERE userId = :userId")
    suspend fun deletePreferences(userId: String)
}
