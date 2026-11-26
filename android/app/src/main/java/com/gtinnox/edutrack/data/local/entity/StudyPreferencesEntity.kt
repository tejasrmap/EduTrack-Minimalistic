package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StudyPreferencesEntity(
    @PrimaryKey
    val userId: String,
    val dailyStudyGoalMinutes: Int = 180,
    val weeklyStudyGoalHours: Int = 20,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val reminderPreferences: Boolean = true,
    val typicalStudyTime: String = "afternoon", // 'morning', 'afternoon', 'evening', 'night'
    val preferredStudyDays: String = "Mon,Tue,Wed,Thu,Fri", // Comma-separated
    val updatedAt: String
)
