package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: String, // 'daily_study', 'weekly_study', 'monthly_study', 'subject_target', 'assignment_completion', 'exam_prep'
    val subjectId: String? = null,
    val targetValue: Double,
    val currentValue: Double = 0.0,
    val unit: String, // "hours", "tasks", "sessions"
    val targetDate: String, // YYYY-MM-DD
    val isCompleted: Boolean = false,
    val completedAt: String? = null,
    val createdAt: String
)
