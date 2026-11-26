package com.gtinnox.edutrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExaminationDao {
    @Query("SELECT * FROM examinations ORDER BY examDate ASC, examTime ASC")
    fun getAllExaminations(): Flow<List<ExaminationEntity>>

    @Query("SELECT * FROM examinations WHERE subjectId = :subjectId ORDER BY examDate ASC")
    fun getExaminationsForSubject(subjectId: String): Flow<List<ExaminationEntity>>

    @Query("SELECT * FROM examinations WHERE id = :id LIMIT 1")
    fun getExaminationById(id: String): Flow<ExaminationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamination(exam: ExaminationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExaminations(exams: List<ExaminationEntity>)

    @Update
    suspend fun updateExamination(exam: ExaminationEntity)

    @Delete
    suspend fun deleteExamination(exam: ExaminationEntity)

    @Query("DELETE FROM examinations WHERE id = :id")
    suspend fun deleteExaminationById(id: String)
}
