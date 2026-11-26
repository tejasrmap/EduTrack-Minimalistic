package com.gtinnox.edutrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "user-default-1",
    val name: String,
    val email: String,
    val university: String,
    val program: String,
    val academicYear: String,
    val semester: String,
    val preferredStudyHours: Int = 25,
    val dailyTargetMinutes: Int = 180,
    val avatar: String = "JV",
    val isOnboarded: Boolean = true,
    val createdAt: String
)
