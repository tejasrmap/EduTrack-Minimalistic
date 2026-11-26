package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class StudySessionEntity(
    @PrimaryKey
    val id: String,
    val subjectId: String,
    val durationMinutes: Int,
    val sessionType: String, // 'pomodoro', 'deep_work', 'revision', 'reading', 'assignment'
    val notes: String = "",
    val focusScore: Int = 85,
    val isManual: Boolean = false,
    val startTime: String,
    val endTime: String,
    val createdAt: String
)
