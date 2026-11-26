package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
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
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val subjectId: String,
    val category: String, // 'assignment', 'homework', 'revision', 'project', 'reading', 'exam_prep', 'other'
    val priority: String, // 'low', 'medium', 'high'
    val status: String, // 'not_started', 'in_progress', 'completed'
    val dueDate: String, // YYYY-MM-DD
    val dueTime: String = "23:59",
    val estimatedMinutes: Int = 60,
    val completedAt: String? = null,
    val createdAt: String
)
