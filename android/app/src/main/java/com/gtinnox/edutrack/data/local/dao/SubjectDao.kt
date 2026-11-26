package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.relations.SubjectWithSessions
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    fun getSubjectById(id: String): Flow<SubjectEntity?>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectByIdDirect(id: String): SubjectEntity?

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    fun getSubjectWithSessions(id: String): Flow<SubjectWithSessions?>

    @Transaction
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjectsWithSessions(): Flow<List<SubjectWithSessions>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: String)

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectCount(): Int
}
