package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Transaction
    @Query("SELECT * FROM study_sessions ORDER BY startTime DESC")
    fun getAllSessionsWithSubject(): Flow<List<SessionWithSubject>>

    @Transaction
    @Query("SELECT * FROM study_sessions WHERE id = :id LIMIT 1")
    fun getSessionWithSubject(id: String): Flow<SessionWithSubject?>

    @Query("SELECT * FROM study_sessions WHERE subjectId = :subjectId ORDER BY startTime DESC")
    fun getSessionsForSubject(subjectId: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE id = :id LIMIT 1")
    fun getSessionById(id: String): Flow<StudySessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<StudySessionEntity>)

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalStudyMinutes(): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE subjectId = :subjectId")
    fun getStudyMinutesForSubject(subjectId: String): Flow<Int?>

    @Query("SELECT COUNT(*) FROM study_sessions")
    suspend fun getSessionCount(): Int
}
