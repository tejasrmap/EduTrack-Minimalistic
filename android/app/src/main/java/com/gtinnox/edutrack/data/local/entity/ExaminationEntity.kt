package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "examinations",
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
data class ExaminationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subjectId: String,
    val examDate: String, // YYYY-MM-DD
    val examTime: String, // HH:mm
    val location: String = "Hall A",
    val examType: String, // 'midterm', 'final', 'quiz', 'practical', 'presentation', 'other'
    val preparationStatus: String, // 'not_prepared', 'in_progress', 'ready'
    val targetGrade: String = "A",
    val notes: String = "",
    val createdAt: String
)
